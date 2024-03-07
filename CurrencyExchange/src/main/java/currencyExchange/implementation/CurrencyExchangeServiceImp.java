package currencyExchange.implementation;

import java.math.BigDecimal;

import org.springframework.web.bind.annotation.RestController;

import api.dtos.CurrencyExchangeDto;
import api.services.CurrencyExchangeService;
@RestController
public class CurrencyExchangeServiceImp implements CurrencyExchangeService{
	
	@Override
	public CurrencyExchangeDto getExchange() {
		return new CurrencyExchangeDto("EUR","RSD",BigDecimal.valueOf(117.5));
	}

}
