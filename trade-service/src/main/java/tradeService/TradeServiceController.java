package tradeService;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import api.feignProxies.BankAccountServiceProxy;
import api.feignProxies.CryptoWalletProxy;
import api.feignProxies.CurrencyConversionProxy;
import api.feignProxies.UsersServiceProxy;
import api.services.TradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import feign.FeignException;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import tradeService.model.Trade;

@RestController
public class TradeServiceController implements TradeService {

	@Autowired
	private CustomTradeServiceRepository repo;

	@Autowired
	private UsersServiceProxy userServiceProxy;

	@Autowired
	private CurrencyConversionProxy currencyConversionProxy;

	@Autowired
	private BankAccountServiceProxy bankAccountProxy;

	@Autowired
	private CryptoWalletProxy cryptoWalletProxy;

	public ResponseEntity<Trade> getTrade(@PathVariable String from, @PathVariable String to) {
		Trade kurs = repo.findByFromAndToIgnoreCase(from, to);
		if (kurs != null) {
			return ResponseEntity.ok(kurs);
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); 
		}
	}

	@GetMapping("/trade-service-feign")
	@RateLimiter(name="default")
	public ResponseEntity<?> getTradeFeign(@RequestParam String from, @RequestParam String to,
			@RequestParam double quantity, @RequestHeader("Authorization") String authorizationHeader) {
		try {
			String role = userServiceProxy.extractRole(authorizationHeader);
			String email = userServiceProxy.getEmailOfCurrentUser(authorizationHeader);
			BigDecimal previousAmount = bankAccountProxy.getBankAccountByEmail(email).getEur_amount() != null
					? bankAccountProxy.getBankAccountByEmail(email).getEur_amount()
					: BigDecimal.ZERO;
			BigDecimal bigDecimalValue = BigDecimal.valueOf(quantity);

			from = from.toUpperCase();
			String previousFrom = null;
			to = to.toUpperCase();
			String previousTo = null;

			List<String> cryptoCurrencies = Arrays.asList("ETH", "USDT", "BTC");
			
			if(!cryptoCurrencies.contains(to) && !cryptoCurrencies.contains(from)) {
            	return ResponseEntity.status(HttpStatus.CONFLICT).body("Dozvoljena je samo konverzija CRYPTO u FIAT valute i obrnuto.");
			}

			List<String> fiatCurrencies = Arrays.asList("RSD", "USD", "EUR", "GBP", "CHF");
			if(!fiatCurrencies.contains(to) && !fiatCurrencies.contains(from)) {
				return ResponseEntity.status(HttpStatus.CONFLICT).body("Dozvoljena je samo konverzija CRYPTO u FIAT valute i obrnuto.");
			}

			if (cryptoCurrencies.contains(to)) {
				if (!"USD".equals(from) && !"EUR".equals(from)) {
					currencyConversionProxy.getConversionFeign(from, "EUR", quantity, authorizationHeader);
					BigDecimal newAmount = bankAccountProxy.getBankAccountByEmail(email).getEur_amount();
					bigDecimalValue = newAmount.subtract(previousAmount);
					previousFrom = from;
					from = "EUR";
				}

				if ("USER".equals(role)) {
					boolean check = bankAccountProxy.getConversionPosibility(email, bigDecimalValue, from);
					if (check) {
						ResponseEntity<Trade> trade = getTrade(from, to);
						Trade body = trade.getBody();
						BigDecimal tradeResult = body.getConversionMultiple().multiply(bigDecimalValue);

						cryptoWalletProxy.updateCurrencyAmount(email, to, tradeResult);

						var walletDto = cryptoWalletProxy.getCryptoWalletByEmail(email);
						HashMap<String, Object> responseMap = new HashMap<>();
						responseMap.put("walletDto", walletDto);
						if (previousFrom == null) {
							responseMap.put("message", "Uspešno je izvršena razmena " + quantity + " " + from + " za " + tradeResult + " " + to);
							return new ResponseEntity<>(responseMap, HttpStatus.OK);
						} else {
							responseMap.put("message", "Uspešno je izvršena razmena " + quantity + " " + previousFrom
									+ " za " + bigDecimalValue + " " + from + " a zatim iz " + from + " za " + tradeResult + " " + to);
							return new ResponseEntity<>(responseMap, HttpStatus.OK);
						}
					} else {
						return ResponseEntity.status(HttpStatus.CONFLICT).body("Korisnik nema dovoljno sredstava za razmenu.");
					}
				} else {
					return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
				}
			} else {
				if ("USER".equals(role)) {
					boolean check = cryptoWalletProxy.getConversionPosibility(email, bigDecimalValue, from);
					if (check) {
						if (!"USD".equals(to) && !"EUR".equals(to)) {
							previousTo = to;
							to = "EUR";
						}

						ResponseEntity<Trade> trade = getTrade(from, to);
						Trade body = trade.getBody();
						BigDecimal resultTrade = body.getConversionMultiple().multiply(bigDecimalValue);

						bankAccountProxy.updateCurrencyAmount(email, to, resultTrade);

						HashMap<String, Object> responseMap = new HashMap<>();
						if (previousTo != null) {
							BigDecimal newAmount = bankAccountProxy.getBankAccountByEmail(email).getEur_amount();
							currencyConversionProxy.getConversionFeign("EUR", previousTo, newAmount.subtract(previousAmount).doubleValue(), authorizationHeader);
							responseMap.put("message", "Uspešno je izvršena razmena " + quantity + " " + from + " u "
									+ to + " a zatim u " +  resultTrade + " "  + previousTo);
						} else {
							responseMap.put("message",
									"Uspešno je izvršena razmena " + quantity + " " + from + " u " + resultTrade + " " + to);
						}

						var bankDto = bankAccountProxy.getBankAccountByEmail(email);
						responseMap.put("bankDto", bankDto);

						return new ResponseEntity<>(responseMap, HttpStatus.OK);

					} else {
						return ResponseEntity.status(HttpStatus.CONFLICT).body("Korisnik nema dovoljno sredstava za razmenu.");
					}
				}
			}

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error occurred.");
		} catch (FeignException e) {
			return ResponseEntity.status(e.status()).body(e.getMessage());
		}
	}
	
	@ExceptionHandler(RequestNotPermitted.class)
	public ResponseEntity<String> rateLimiterExceptionHandler(RequestNotPermitted ex){
		return ResponseEntity.status(503).body("Trade service can only serve up to 2 requests every 45 seconds");
	}
}
