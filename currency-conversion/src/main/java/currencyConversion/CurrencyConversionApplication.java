package currencyConversion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;
import util.exceptions.GlobalExceptionHandler;

@EnableFeignClients("api.feignProxies")
@SpringBootApplication
@Import(GlobalExceptionHandler.class)
public class CurrencyConversionApplication {

	public static void main(String[] args) {
		SpringApplication.run(CurrencyConversionApplication.class, args);
	}

}
