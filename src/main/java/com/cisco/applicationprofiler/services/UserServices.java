package com.cisco.applicationprofiler.services;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.management.InvalidAttributeValueException;

import org.springframework.stereotype.Service;

import com.cisco.applicationprofiler.domain.User;
import com.cisco.applicationprofiler.exceptions.AciEntityNotFound;
import com.cisco.applicationprofiler.repo.UserRepository;
import com.cisco.applicationprofiler.ui.models.UserUi;

@Service
public class UserServices {

	@Inject
	private UserRepository userRepository;

	public User addUser(UserUi userUi) throws InvalidAttributeValueException {
		User user = new User();
		user.setUsername(userUi.getUsername());
		user.setPassword(userUi.getPassword());
		user.setEmail(userUi.getEmail());
		user.setAuthentication(userUi.getAuthentication());
		user.setRole(userUi.getRole());

		Date date = new Date();
		user.setCreatedTime(new Timestamp(date.getTime()));
		user.setLastUpdatedTime(new Timestamp(date.getTime()));

		userRepository.save(user);
		return user;
	}

	public User deleteUser(int id) throws AciEntityNotFound {
		User user = ValidateIdAndGetUser(id);
		userRepository.delete(id);
		return user;
	}

	public User updateUser(int id, UserUi userUi) throws AciEntityNotFound {
		User user = ValidateIdAndGetUser(id);
		user.setPassword(userUi.getPassword());
		user.setUsername(userUi.getUsername());
		user.setEmail(userUi.getEmail());
		user.setAuthentication(userUi.getAuthentication());
		user.setRole(userUi.getRole());
		Date date = new Date();
		user.setLastUpdatedTime(new Timestamp(date.getTime()));

		userRepository.save(user);
		return user;

	}

	public User getUser(int id) throws AciEntityNotFound {
		User user = ValidateIdAndGetUser(id);
		return user;
	}

	public List<User> getUsers() throws AciEntityNotFound {
		List<User> user = userRepository.findAll();
		if (null == user) {
			throw new AciEntityNotFound("No users found");
		}
		return user;
	}

	private User ValidateIdAndGetUser(int id) throws AciEntityNotFound {
		User user = userRepository.findOne(id);
		if (null == user) {
			throw new AciEntityNotFound(" user dont exist for the given id");
		}
		return user;
	}
}
