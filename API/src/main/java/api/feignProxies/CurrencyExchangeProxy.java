package api.feignProxies;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import api.dto.CurrencyExchangeDto;

@FeignClient("currency-exchange")
public interface CurrencyExchangeProxy {

	@GetMapping("/currency-exchange/from/{from}/to/{to}")
	ResponseEntity<CurrencyExchangeDto> getExchange(
			@PathVariable("from") String from,
			@PathVariable("to") String to);
}
