package cryptoWallet;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import api.dto.CryptoWalletDto;
import api.dto.NewCryptoWalletDto;
import api.feignProxies.BankAccountServiceProxy;
import api.feignProxies.UsersServiceProxy;
import api.services.CryptoWalletService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import cryptoWallet.model.CryptoWallet;
import jakarta.transaction.Transactional;

@RestController
public class CryptoWalletController implements CryptoWalletService {
	
	@Autowired
	private CustomCryptoWalletRepository repo;
	
	@Autowired
	private UsersServiceProxy userServiceProxy;
	
	@Autowired
	private BankAccountServiceProxy bankAccountProxy;
	
	@GetMapping("/crypto-wallet/wallets")
	public List<CryptoWalletDto> getAllCryptoWallets(){

		return repo.findAll().stream().map(cryptoWallet -> new CryptoWalletDto(cryptoWallet.getId(), cryptoWallet.getBtc_amount(), cryptoWallet.getEth_amount(), cryptoWallet.getUsdt_amount())).toList();
	}
	
	@GetMapping("/crypto-wallet/wallets/{email}")
	public ResponseEntity getCryptoWalletByEmail(@PathVariable String email) {
		CryptoWallet cryptoWallet = repo.getByEmail(email);
		if (cryptoWallet == null) {
			return ResponseEntity.notFound().build();
		}
	    return ResponseEntity.ok(cryptoWallet);
	}
	
	@GetMapping("/crypto-wallet/wallets/exists/{email}")
	public Boolean getCryptoWalletExists(@PathVariable String email) {
		CryptoWallet CryptoWallet = repo.getByEmail(email);
	    if (CryptoWallet == null) {
	    	return false;
	    }
	    else {
			return true;
		}
	}
	
	
	@PostMapping("/crypto-wallet/wallets")
	public ResponseEntity<?> createCryptoWallet (
			@RequestBody NewCryptoWalletDto cryptoWallet,
			@RequestHeader("Authorization") String authorizationHeader){
		String role = userServiceProxy.extractRole(authorizationHeader);
		if(role.equals("ADMIN")) {
			if(repo.existsByEmail(cryptoWallet.getEmail())) {
				return ResponseEntity.status(HttpStatus.CONFLICT).body("Cripto novcanik sa unetim email-om vec postoji");
			}else {
				CryptoWallet newWallet = new CryptoWallet();
				newWallet.setEmail(cryptoWallet.getEmail());
				CryptoWallet createdCryptoWallet = repo.save(newWallet);
				return new ResponseEntity<CryptoWallet>(createdCryptoWallet, HttpStatus.CREATED);
			}
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}
	
	@PutMapping("/crypto-wallet/wallets/{id}")
	public ResponseEntity<?> updateCryptoWallet (
			@PathVariable Long id,
			@RequestBody CryptoWalletDto cryptoWallet,
			@RequestHeader("Authorization") String authorizationHeader){
		String role = userServiceProxy.extractRole(authorizationHeader);
		if(role.equals("ADMIN")) {
			Optional<CryptoWallet> check = repo.findById(id);
			if(check.isPresent()) {
				CryptoWallet existingWallet = check.get();
				String email = existingWallet.getEmail();
				BeanUtils.copyProperties(cryptoWallet, existingWallet);
				existingWallet.setId(id);
				existingWallet.setEmail(email);
				repo.save(existingWallet);
				return ResponseEntity.status(HttpStatus.OK).build();
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
			}
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}
	
	@PutMapping("/crypto-wallet/wallets/email/{email}/new/{newEmail}")
	public ResponseEntity<String> updateEmailForCryptoWallet(@PathVariable String email, @PathVariable String newEmail ){
		CryptoWallet CryptoWallet = repo.getByEmail(email);
		if(CryptoWallet != null) {
			CryptoWallet.setEmail(newEmail);
			repo.save(CryptoWallet);
			String errorMessage = "Stari email: " + email + " , novi email: " + newEmail;
			return ResponseEntity.status(HttpStatus.OK).body(errorMessage);
		} else {
			String errorMessage = "Novcanik sa email-om " + email + " ne postoji.";
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
		}
	}
	
	@GetMapping("/crypto-wallet/wallets/update-amount/user/{email}/amount/{amount}/from/{currencyOne}")
	public Boolean getposibilityOfConversion(@PathVariable String email, @PathVariable BigDecimal amount,
	                                                       @PathVariable String currencyOne) {
	    CryptoWallet CryptoWallet = repo.getByEmail(email);

	    BigDecimal current = BigDecimal.ZERO;
	    BigDecimal newAmount = BigDecimal.ZERO;

	    switch (currencyOne) {
	        case "BTC":
	            current = CryptoWallet.getBtc_amount();
	            newAmount = current.subtract(amount);
	            CryptoWallet.setBtc_amount(newAmount);
	            break;
	        case "ETH":
	            current = CryptoWallet.getEth_amount();
	            newAmount = current.subtract(amount);
	            CryptoWallet.setEth_amount(newAmount);
	            break;
	        case "USDT":
	            current = CryptoWallet.getUsdt_amount();
	            newAmount = current.subtract(amount);
	            CryptoWallet.setUsdt_amount(newAmount);
	            break;
	    }
        if (current == null || amount.compareTo(current) > 0) //current amount SMALER than amount
        {
        	return false;
        }else {
        	repo.save(CryptoWallet);
        	return true;
        	
        }
	}
	
	@PutMapping("/crypto-wallet/wallets/{email}/update/currency/{currencyTo}/for/{amount}")
    public  ResponseEntity<String> updateAmount(@PathVariable String email, @PathVariable String currencyTo,@PathVariable BigDecimal amount) {
    	CryptoWallet cryptoWallet = repo.getByEmail(email);
		
    	if (cryptoWallet!=null) {
        BigDecimal current = BigDecimal.ZERO;
		BigDecimal newAmount = BigDecimal.ZERO;

	    switch (currencyTo) {
	        case "BTC":
	            current = cryptoWallet.getBtc_amount() != null ? cryptoWallet.getBtc_amount() : BigDecimal.ZERO;
				newAmount=current.add(amount);
				cryptoWallet.setBtc_amount(newAmount);
	            break;
	        case "ETH":
	            current = cryptoWallet.getEth_amount() != null ? cryptoWallet.getEth_amount() : BigDecimal.ZERO;
				newAmount=current.add(amount);
				cryptoWallet.setEth_amount(newAmount);
	            break;
	        case "USDT":
	            current = cryptoWallet.getUsdt_amount() != null ? cryptoWallet.getUsdt_amount() : BigDecimal.ZERO;;
				newAmount=current.add(amount);
				cryptoWallet.setUsdt_amount(newAmount);
	            break;
	    }
           repo.save(cryptoWallet);
           	String errorMessage = "Novcanik sa email-om " + cryptoWallet.getEmail() + " azuriran.";
			return ResponseEntity.status(HttpStatus.OK).body(errorMessage);
        } else {
        	String errorMessage = "Novcanik sa email-om " + email+" ne postoji.";
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorMessage);
        }
    }
	
	@DeleteMapping("/crypto-wallet/wallets/{email}")
	@Transactional
	public ResponseEntity<String> deleteCryptoWallet(@PathVariable String email) {
			if (repo.existsByEmail(email)) {
					repo.deleteByEmail(email);
					String successMessage = "Korisnik sa email-om  " + email + " obrisan.";
					return ResponseEntity.ok(successMessage);
			} else {
				String errorMessage = "Korisnik sa email-om " + email + " ne postoji.";
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
			}
		
		}

}
