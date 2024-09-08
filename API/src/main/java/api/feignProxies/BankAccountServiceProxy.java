package api.feignProxies;

import api.dto.BankAccountDto;
import api.dto.NewBankAccountDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@FeignClient(name= "bank-account")
public interface BankAccountServiceProxy {

	@DeleteMapping("/bank-account/accounts/{email}")
	ResponseEntity<String> deleteBankAccount(@PathVariable("email") String email);

	@PutMapping("/bank-account/accounts/email/{email}/new/{newEmail}")
	ResponseEntity<String> updateEmailForBankAccount(@PathVariable("email") String email, @PathVariable("newEmail") String newEmail );

	@PostMapping("/bank-account/accounts")
	ResponseEntity<String> createBankAccount(@RequestBody NewBankAccountDto bankAccountDto, @RequestHeader("Authorization") String header);

	@GetMapping("/bank-account/accounts/exists/{email}")
	Boolean bankAccountExists(@PathVariable("email") String email);

	@GetMapping("/bank-account/accounts/exists/{email}")
	public Boolean getBankAccountExists(@PathVariable("email") String email);

	@GetMapping("/bank-account/accounts/update-amount/user/{email}/amount/{amount}/from/{currencyOne}")
	public Boolean getConversionPosibility(@PathVariable("email") String email, @PathVariable("amount") BigDecimal amount,
										   @PathVariable("currencyOne") String currencyOne);
	@PutMapping("/bank-account/accounts/{email}/update/currency/{currencyTo}/for/{amount}")
	public ResponseEntity<String> updateCurrencyAmount(@PathVariable("email") String email, @PathVariable("currencyTo") String currencyTo,@PathVariable("amount") BigDecimal amount);

	@GetMapping("/bank-account/accounts/{email}")
	public BankAccountDto getBankAccountByEmail(@PathVariable("email") String email);
}
