package usersService.model;

import jakarta.persistence.*;

@Entity
public class CustomUser {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;

	@Column(unique = true, nullable = false)
	private String email;
	@Column(nullable = false)
	private String password;
	@Column(nullable = false, columnDefinition = "VARCHAR(20) CHECK(role IN('ADMIN','USER','OWNER'))")
	private String role;

	
	//KONSTRUKTOR I GET I SET METODE
	public CustomUser() {

	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

}
