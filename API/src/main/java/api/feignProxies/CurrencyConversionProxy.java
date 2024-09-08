package api.feignProxies;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name="currency-conversion")
public interface CurrencyConversionProxy {
	@GetMapping("/currency-conversion-feign")
	public ResponseEntity<?> getConversionFeign(
			@RequestParam("from") String from,
			@RequestParam("to") String to,
			@RequestParam("quantity") double quantity,
			@RequestHeader("Authorization") String authorizationHeader);


}
