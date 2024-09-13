package api.services;

import java.util.List;

import api.dto.CustomUserDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import api.dto.UserDto;

public interface UsersService {

	@GetMapping("/users-service/users")
	public List<CustomUserDto> getAllUsers();

	@PostMapping("/users-service/users")
	public ResponseEntity<?> createUser(
			@RequestBody UserDto user,
			@RequestHeader("Authorization") String authorizationHeader);

	@PutMapping("/users-service/users/{id}")
	public ResponseEntity<?> updateUser(
			@PathVariable long id,
			@RequestBody CustomUserDto userUpdate,
			@RequestHeader("Authorization") String authorizationHeader);

	@DeleteMapping("/users-service/users/{id}")
	public ResponseEntity<?> deleteUser(@PathVariable long id,
										@RequestHeader("Authorization") String authorizationHeader);

	@GetMapping("/users-service/user/role/{email}")
	public String getUsersRoleByEmail(@PathVariable String email);

	@GetMapping("/users-service/email-logged-user")
	public String extractEmail(@RequestHeader("Authorization") String authorizationHeader);

	@GetMapping("/users-service/id-logged-user/{email}")
	public Long extractId(@PathVariable String email);

}
