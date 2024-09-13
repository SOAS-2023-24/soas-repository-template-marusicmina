package api.services;

import api.dto.CryptoWalletDto;
import api.dto.NewCryptoWalletDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

public interface CryptoWalletService {

    @GetMapping("/crypto-wallet/wallets")
    public List<CryptoWalletDto> getAllCryptoWallets();

    @GetMapping("/crypto-wallet/wallets/{email}")
    public ResponseEntity getCryptoWalletByEmail(@PathVariable String email);

    @GetMapping("/crypto-wallet/wallets/exists/{email}")
    public Boolean getCryptoWalletExists(@PathVariable String email);

    @PostMapping("/crypto-wallet/wallets")
    public ResponseEntity<?> createCryptoWallet (
            @RequestBody NewCryptoWalletDto cryptoWallet,
            @RequestHeader("Authorization") String authorizationHeader);

    @PutMapping("/crypto-wallet/wallets/{id}")
    public ResponseEntity<?> updateCryptoWallet (
            @PathVariable Long id,
            @RequestBody CryptoWalletDto cryptoWallet,
            @RequestHeader("Authorization") String authorizationHeader);

    @PutMapping("/crypto-wallet/wallets/email/{email}/new/{newEmail}")
    public ResponseEntity<String> updateEmailForCryptoWallet(@PathVariable String email, @PathVariable String newEmail );

    @GetMapping("/crypto-wallet/wallets/update-amount/user/{email}/amount/{amount}/from/{currencyOne}")
    public Boolean getposibilityOfConversion(@PathVariable String email, @PathVariable BigDecimal amount,
                                             @PathVariable String currencyOne);

    @PutMapping("/crypto-wallet/wallets/{email}/update/currency/{currencyTo}/for/{amount}")
    public  ResponseEntity<String> updateAmount(@PathVariable String email, @PathVariable String currencyTo,@PathVariable BigDecimal amount);


    @DeleteMapping("/crypto-wallet/wallets/{email}")
    public ResponseEntity<String> deleteCryptoWallet(@PathVariable String email);
}
