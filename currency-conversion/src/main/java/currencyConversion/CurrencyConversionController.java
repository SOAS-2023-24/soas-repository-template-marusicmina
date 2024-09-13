package currencyConversion;

import java.math.BigDecimal;
import java.util.HashMap;

import api.dto.BankAccountDto;
import api.dto.CurrencyExchangeDto;
import api.feignProxies.BankAccountServiceProxy;
import api.feignProxies.CurrencyExchangeProxy;
import api.feignProxies.UsersServiceProxy;
import api.services.CurrencyConversionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import feign.FeignException;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;

@RestController
public class CurrencyConversionController implements CurrencyConversionService {
	
	@Autowired
	private CurrencyExchangeProxy proxy;
	@Autowired
	private BankAccountServiceProxy bankAccountProxy;
	@Autowired
	private UsersServiceProxy userServiceProxy;

	@GetMapping("/currency-conversion-feign")
	@RateLimiter(name="default")
	public ResponseEntity<?> getConversionFeign(@RequestParam String from, @RequestParam String to, @RequestParam double quantity,@RequestHeader("Authorization") String authorizationHeader){
		try {
			String role = userServiceProxy.extractRole(authorizationHeader);
			String email = userServiceProxy.getEmailOfCurrentUser(authorizationHeader);


			ResponseEntity<CurrencyExchangeDto> response = proxy.getExchange(from, to);
			CurrencyExchangeDto responseBody = response.getBody();
			BigDecimal result = responseBody.getConversionMultiple().multiply(BigDecimal.valueOf(quantity));

			BigDecimal bigDecimalValue = new BigDecimal(quantity);
			if (role.equals("USER")) {
				boolean enoughMoney = bankAccountProxy.getConversionPosibility(email, bigDecimalValue, from);

				if(enoughMoney) {
					bankAccountProxy.updateCurrencyAmount(email, to, result);
					BankAccountDto bankDto = bankAccountProxy.getBankAccountByEmail(email);
					HashMap<String, Object> responseMap = new HashMap<>();
					responseMap.put("bankDto", bankDto);
					responseMap.put("message", "Uspešno je izvršena razmena " + quantity + " " + from + " za " + result + " " + to);

					return new ResponseEntity<>(responseMap, HttpStatus.OK);
				}else {
					return ResponseEntity.status(HttpStatus.CONFLICT).body("Korisnik nema dovoljno sredstava za razmenu.");
				}
			
			}else {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
			}

		} catch(FeignException e) {
			return ResponseEntity.status(e.status()).body(e.getMessage());
		}
	}
	
	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ResponseEntity<String> handleMissingParams(MissingServletRequestParameterException ex) {
	    String parameter = ex.getParameterName();
	    return ResponseEntity.status(ex.getStatusCode()).body("Value [" + ex.getParameterType() + "] of parameter [" + parameter + "] has been ommited");
	}
	
	@ExceptionHandler(RequestNotPermitted.class)
	public ResponseEntity<String> rateLimiterExceptionHandler(RequestNotPermitted ex){
		return ResponseEntity.status(503).body("Currency conversion service can only serve up to 2 requests every 45 seconds");
	}
	
	
}
