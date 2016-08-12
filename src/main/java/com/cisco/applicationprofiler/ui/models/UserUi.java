/**
 * 
 */
package com.cisco.applicationprofiler.ui.models;

/**
 * @author Mahesh
 *
 */
public class UserUi {
	private String username;
	private String password;
	private String role;
	private String email;
	private String authentication;

	/**
	 * @return the userName
	 */
	public String getUsername() {
		return username;
	}
	/**
	 * @param userName
	 *            the userName to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}
	/**
	 * @param password
	 *            the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getEmail() {
		return email;
	}

	public String getAuthentication() {
		return authentication;
	}

	public void setAuthentication(String authentication) {
		this.authentication = authentication;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}
