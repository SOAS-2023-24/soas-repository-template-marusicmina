package bankAccount;

import org.springframework.boot.SpringApplication;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;
import util.exceptions.GlobalExceptionHandler;

@EnableFeignClients(basePackages = "api")
@SpringBootApplication
@Import(GlobalExceptionHandler.class)
public class BankAccountApplication {

	public static void main(String[] args) {
		SpringApplication.run(BankAccountApplication.class, args);
	}

}
