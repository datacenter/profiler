package com.cisco.applicationprofiler.domain;

import java.sql.Timestamp;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import com.cisco.applicationprofiler.models.View;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonView;

@Entity
@Table(name = "user")
@XmlRootElement
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	@JsonView(View.User.class)
	private int id;

	@OneToMany(mappedBy = "user", cascade = { CascadeType.ALL })
	@JsonManagedReference
	private List<Project> projects;

	@Column(name = "username")
	@JsonView(View.User.class)
	private String username;

	@Column(name = "password")
	private String password;

	@Column(name = "role")
	@JsonView(View.User.class)
	private String role;

	@Column(name = "email")
	@JsonView(View.User.class)
	private String email;

	@Column(name = "authentication")
	@JsonView(View.User.class)
	private String authentication;

	@Column(name = "created_time")
	private Timestamp createdTime;

	@Column(name = "last_updated_time")
	private Timestamp lastUpdatedTime;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
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

	public Timestamp getCreatedTime() {
		return createdTime;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAuthentication() {
		return authentication;
	}

	public void setAuthentication(String authentication) {
		this.authentication = authentication;
	}

	public void setCreatedTime(Timestamp createdTime) {
		this.createdTime = createdTime;
	}

	public Timestamp getLastUpdatedTime() {
		return lastUpdatedTime;
	}

	public void setLastUpdatedTime(Timestamp lastUpdatedTime) {
		this.lastUpdatedTime = lastUpdatedTime;
	}

}
