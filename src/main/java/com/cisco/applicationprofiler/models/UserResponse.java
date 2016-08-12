/**
 * 
 */
package com.cisco.applicationprofiler.models;

/**
 * @author Mahesh
 *
 */
public class UserResponse {
	private String jwtKey;
	private String role;
	private String username;
	private int userId;

	/**
	 * @return the jwtKey
	 */
	public String getJwtKey() {
		return jwtKey;
	}

	/**
	 * @param jwtKey the jwtKey to set
	 */
	public void setJwtKey(String jwtKey) {
		this.jwtKey = jwtKey;
	}

	/**
	 * @return the role
	 */
	public String getRole() {
		return role;
	}

	/**
	 * @param role the role to set
	 */
	public void setRole(String role) {
		this.role = role;
	}

	/**
	 * @return the userName
	 */
	public String getUsername() {
		return username;
	}
	/**
	 * @param userName the userName to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	/**
	 * @return the userId
	 */
	public int getUserId() {
		return userId;
	}
	/**
	 * @param userId the userId to set
	 */
	public void setUserId(int userId) {
		this.userId = userId;
	}

}
