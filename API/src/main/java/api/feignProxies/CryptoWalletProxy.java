package api.feignProxies;

import api.dto.CryptoWalletDto;
import api.dto.NewCryptoWalletDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@FeignClient(name="crypto-wallet")
public interface CryptoWalletProxy {

	@DeleteMapping("/crypto-wallet/wallets/{email}")
	public ResponseEntity<String> deleteCryptoWallet(@PathVariable("email") String email);

	@PutMapping("/crypto-wallet/wallets/email/{email}/new/{newEmail}")
	public ResponseEntity<String> updateEmailForCryptoWallet(@PathVariable("email") String email, @PathVariable("newEmail") String newEmail );

	@PostMapping("/crypto-wallet/wallets")
	public ResponseEntity<String> createCryptoWallet(@RequestBody NewCryptoWalletDto dto, @RequestHeader("Authorization") String auhtorization );

	@GetMapping("/crypto-wallet/wallets/exists/{email}")
	public Boolean getCryptoWalletExists(@PathVariable("email") String email);

	@GetMapping("/crypto-wallet/wallets/update-amount/user/{email}/amount/{amount}/from/{currencyOne}")
	public Boolean getConversionPosibility(@PathVariable("email") String email, @PathVariable("amount") BigDecimal amount,
										   @PathVariable("currencyOne") String currencyOne);

	@GetMapping("/crypto-wallet/wallets/{email}")
	public CryptoWalletDto getCryptoWalletByEmail(@PathVariable("email") String email);

	@PutMapping("/crypto-wallet/wallets/{email}/update/currency/{currencyTo}/for/{amount}")
	public ResponseEntity<String> updateCurrencyAmount(@PathVariable("email") String email,
													   @PathVariable("currencyTo") String currencyTo,
													   @PathVariable("amount") BigDecimal amount);

}
