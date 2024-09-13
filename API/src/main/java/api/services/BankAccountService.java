package api.services;

import api.dto.BankAccountDto;
import api.dto.NewBankAccountDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

public interface BankAccountService {
    @GetMapping("/bank-account/accounts")
    public List<BankAccountDto> getAllBankAccounts();

    @GetMapping("/bank-account/accounts/{email}")
    public ResponseEntity getBankAccountByEmail(@PathVariable String email);

    @GetMapping("/bank-account/accounts/exists/{email}")
    public Boolean getBankAccountExists(@PathVariable String email);

    @PostMapping("/bank-account/accounts")
    public ResponseEntity<?> createBankAccount (@RequestBody NewBankAccountDto bankAccount,
                                                @RequestHeader("Authorization") String authorizationHeader);

    @PutMapping("/bank-account/accounts/{id}")
    public ResponseEntity<?> updateBankAccount (
            @PathVariable Long id,
            @RequestBody BankAccountDto bankAccount,
            @RequestHeader("Authorization") String authorizationHeader);

    @PutMapping("/bank-account/accounts/email/{email}/new/{newEmail}")
    public ResponseEntity<String> updateEmailForBankAccount(@PathVariable String email, @PathVariable String newEmail );

    @GetMapping("/bank-account/accounts/update-amount/user/{email}/amount/{amount}/from/{currencyOne}")
    public Boolean getposibilityOfConversion(@PathVariable String email, @PathVariable BigDecimal amount,
                                             @PathVariable String currencyOne);

    @PutMapping("/bank-account/accounts/{email}/update/currency/{currencyTo}/for/{amount}")
    public  ResponseEntity<String> updateAmount(
            @PathVariable String email,
            @PathVariable String currencyTo,
            @PathVariable BigDecimal amount);

    @DeleteMapping("/bank-account/accounts/{email}")
    public ResponseEntity<String> deleteBankAccount(@PathVariable String email);
}
