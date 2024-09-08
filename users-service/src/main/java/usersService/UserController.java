package usersService;

import java.util.List;

import api.dto.NewBankAccountDto;
import api.dto.NewCryptoWalletDto;
import api.dto.UserDto;
import api.feignProxies.BankAccountServiceProxy;
import api.feignProxies.CryptoWalletProxy;
import org.bouncycastle.util.encoders.Base64;
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

import jakarta.transaction.Transactional;
import usersService.model.CustomUser;
import util.exceptions.NoDataFoundException;

@RestController
public class UserController {

	@Autowired
	private BankAccountServiceProxy proxy;

	@Autowired
	private CryptoWalletProxy cryptoWalletProxy;

	@Autowired
	private CustomUserRepository repo;

	@GetMapping("/users-service/users")
	public List<CustomUser> getAllUsers() {
		return repo.findAll();
	}

	@PostMapping("/users-service/users")
	public ResponseEntity<?> createUser(
			@RequestBody UserDto user,
			@RequestHeader("Authorization") String authorizationHeader) {

		String role = extractRoleFromAuthorizationHeader(authorizationHeader);
		if (role.equals("ADMIN")) {
			if (user.getRole().equals("USER")) {
				if (repo.existsByEmail(user.getEmail())) {
					String errorMessage = "Korisnik sa prosledjenom email adresom vec postoji.";
					return ResponseEntity.status(HttpStatus.CONFLICT).body(errorMessage);
				} else {
					CustomUser forCreate = new CustomUser();
					BeanUtils.copyProperties(user, forCreate);
					CustomUser createdUser = repo.save(forCreate);

					NewBankAccountDto dto = new NewBankAccountDto();
					dto.setEmail(forCreate.getEmail());
					proxy.createBankAccount(dto, authorizationHeader);
					NewCryptoWalletDto newCryptoWalletDto = new NewCryptoWalletDto();
					newCryptoWalletDto.setEmail(forCreate.getEmail());
					cryptoWalletProxy.createCryptoWallet(newCryptoWalletDto, authorizationHeader);

					return new ResponseEntity<CustomUser>(createdUser, HttpStatus.CREATED);
				}
			} else {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
			}

		} else if (role.equals("OWNER")) {
			if (user.getRole().equals("USER") || user.getRole().equals("ADMIN")) {
				if (repo.existsByEmail(user.getEmail())) {
					String errorMessage = "Korisnik sa prosledjenom email adresom vec postoji.";
					return ResponseEntity.status(HttpStatus.CONFLICT).body(errorMessage);
				}  else {
				CustomUser forCreate = new CustomUser();
				BeanUtils.copyProperties(user, forCreate);
				CustomUser createdUser = repo.save(forCreate);
				NewBankAccountDto dto = new NewBankAccountDto();
				dto.setEmail(forCreate.getEmail());
				proxy.createBankAccount(dto, authorizationHeader);

				NewCryptoWalletDto newCryptoWalletDto = new NewCryptoWalletDto();
				newCryptoWalletDto.setEmail(forCreate.getEmail());
				cryptoWalletProxy.createCryptoWallet(newCryptoWalletDto, authorizationHeader);
				return new ResponseEntity<CustomUser>(createdUser, HttpStatus.CREATED);
				}
			} else {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
			}
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}

	@PutMapping("/users-service/users/{id}")
	public ResponseEntity<?> updateUser(
			@PathVariable long id,
			@RequestBody CustomUser userUpdate,
			@RequestHeader("Authorization") String authorizationHeader) {
		String role = extractRoleFromAuthorizationHeader(authorizationHeader);
		String email = extractEmail(authorizationHeader);

		CustomUser user = repo.findById(id);
		if (user == null) return ResponseEntity.notFound().build();

		if (role.equals("ADMIN")) {
			if (user.getRole().equals("USER")) {
				user.setPassword(userUpdate.getPassword());
				repo.save(user);
				return ResponseEntity.status(HttpStatus.OK).build();
			} else if (user.getEmail().equals(email)) {
				user.setPassword(userUpdate.getPassword());
				repo.save(user);
				return ResponseEntity.status(HttpStatus.OK).build();
			}else {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
			}

		} else if (role.equals("OWNER")) {
			if (userUpdate.getRole().equals("OWNER") && !user.getRole().equals("OWNER")) {
				return ResponseEntity.status(HttpStatus.CONFLICT).body("Moze da postoji samo jedan admin u sistemu");
			}
			user.setPassword(userUpdate.getPassword());
			repo.save(user);
			return ResponseEntity.status(HttpStatus.OK).build();
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}

	@DeleteMapping("/users-service/users/{id}")
	@Transactional
	public ResponseEntity<?> deleteUser(@PathVariable long id,
			@RequestHeader("Authorization") String authorizationHeader) {
		String role = extractRoleFromAuthorizationHeader(authorizationHeader);

		if ("OWNER".equals(role)) {
			if (repo.existsById(id)) {
				CustomUser user = repo.findById(id);
				if (user.getRole().equals("USER") && proxy.bankAccountExists(user.getEmail())) {
					proxy.deleteBankAccount(user.getEmail());
					if (cryptoWalletProxy.getCryptoWalletExists(user.getEmail())) {
						cryptoWalletProxy.deleteCryptoWallet(user.getEmail());
					}
				}
				repo.deleteById(id);
				return ResponseEntity.ok().build();
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
			}
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}

	@GetMapping("/users-service/user/{email}")
	public Boolean getUserByEmail(@PathVariable String email) {
		CustomUser user = repo.findByEmail(email);
		if (user == null) {
			return false;
		} else
			return true;
	}

	@GetMapping("/users-service/user/role/{email}")
	public String getUsersRoleByEmail(@PathVariable String email) {
		CustomUser user = repo.findByEmail(email);
		if (user == null) {
			return null;
		} else
			return user.getRole();
	}

	public String extractRoleFromAuthorizationHeader(String authorizationHeader) {
		String encodedCredentials = authorizationHeader.replaceFirst("Basic ", "");
		byte[] decodedBytes = Base64.decode(encodedCredentials.getBytes());
		String decodedCredentials = new String(decodedBytes);
		String[] credentials = decodedCredentials.split(":");
		String role = credentials[0]; 
		CustomUser user = repo.findByEmail(role);
		return user.getRole();
	}

	public String extractEmailFromAuthorizationHeader(String authorizationHeader) {
		String encodedCredentials = authorizationHeader.replaceFirst("Basic ", "");
		byte[] decodedBytes = Base64.decode(encodedCredentials.getBytes());
		String decodedCredentials = new String(decodedBytes);
		String[] credentials = decodedCredentials.split(":");
		String role = credentials[0]; 
		CustomUser user = repo.findByEmail(role);
		return user.getEmail();
	}

	@GetMapping("/users-service/logged-user")
	public String extractRole(@RequestHeader("Authorization") String authorizationHeader) {
		String role = extractRoleFromAuthorizationHeader(authorizationHeader);
		return role;
	}

	@GetMapping("/users-service/email-logged-user")
	public String extractEmail(@RequestHeader("Authorization") String authorizationHeader) {
		String email = extractEmailFromAuthorizationHeader(authorizationHeader);
		return email;
	}

	@GetMapping("/users-service/id-logged-user/{email}")
	public Long extractId(@PathVariable String email) {
		CustomUser user = repo.findByEmail(email);
		Long id = user.getId();
		return id;
	}
}
