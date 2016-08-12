/**
 * 
 */
package com.cisco.applicationprofiler.rest.controllers;

import java.util.List;

import javax.inject.Inject;
import javax.management.InvalidAttributeValueException;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cisco.applicationprofiler.domain.User;
import com.cisco.applicationprofiler.exceptions.AciEntityNotFound;
import com.cisco.applicationprofiler.models.UserResponse;
import com.cisco.applicationprofiler.repo.UserRepository;
import com.cisco.applicationprofiler.security.util.JWTUtil;
import com.cisco.applicationprofiler.services.UserServices;
import com.cisco.applicationprofiler.ui.models.UserUi;
import com.cisco.applicationprofiler.util.ApplicationProfilerConstants;
import com.google.gson.Gson;
import com.wordnik.swagger.annotations.ApiOperation;

/**
 * @author Admin
 *
 */
@Controller
public class UserController {

	@Inject
	private JWTUtil jwtUtil;

	@Inject
	private UserRepository userRepository;

	@Inject
	private Gson gson;

	@Inject
	public UserServices userServices;

	@RequestMapping(value = ApplicationProfilerConstants.LOGIN, method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	private UserResponse login(@RequestBody UserUi userUi) throws AciEntityNotFound {
		com.cisco.applicationprofiler.domain.User user = userRepository.findUserByUserName(userUi.getUsername());
		if (!user.getPassword().equals(userUi.getPassword())) {
			throw new AciEntityNotFound();
		}

		UserResponse userResponse = outputMapping(user);
		userResponse.setJwtKey(jwtUtil.createJWT("" + Math.random(), gson.toJson(userResponse)));
		return userResponse;

	}

	private UserResponse outputMapping(User user) {
		UserResponse userResponse = new UserResponse();
		userResponse.setRole(user.getRole());
		userResponse.setUserId(user.getId());
		userResponse.setUsername(user.getUsername());
		return userResponse;
	}
	
	

	@ApiOperation(value = "Add a user", notes = "Only single user can be added with the call", response = UserUi.class, responseContainer = "Self")
	@RequestMapping(value = ApplicationProfilerConstants.ADMIN_URL, method = RequestMethod.POST, produces = ApplicationProfilerConstants.JSON)
	@ResponseBody
	public User addUser(@RequestBody UserUi userUi) throws AciEntityNotFound, InvalidAttributeValueException {
		return userServices.addUser(userUi);
	}

	@ApiOperation(value = "delete a user", notes = "Only single user can be deleted with the call", response = UserUi.class, responseContainer = "Self")
	@RequestMapping(value = ApplicationProfilerConstants.ADMIN_URL+"/{id}", method = RequestMethod.DELETE, produces = ApplicationProfilerConstants.JSON)
	@ResponseBody
	public User deleteUser(@PathVariable("id") int Id) throws AciEntityNotFound {
		return userServices.deleteUser(Id);
	}

	@ApiOperation(value = "update a user", notes = "Only single user can be updated with the call", response = UserUi.class, responseContainer = "Self")
	@RequestMapping(value = ApplicationProfilerConstants.ADMIN_URL+"/{id}", method = RequestMethod.PUT, produces = ApplicationProfilerConstants.JSON)
	@ResponseBody
	public User updateUser(@PathVariable("id") int Id, @RequestBody UserUi userUi) throws AciEntityNotFound {
		return userServices.updateUser(Id, userUi);
	}

	@ApiOperation(value = "fetch a user", notes = "Only single user can be fetched with the call", response = UserUi.class, responseContainer = "Self")
	@RequestMapping(value = ApplicationProfilerConstants.ADMIN_URL+"/{id}", method = RequestMethod.GET, produces = ApplicationProfilerConstants.JSON )
	@ResponseBody
	public User getUser(@PathVariable("id") int Id) throws AciEntityNotFound {
		return userServices.getUser(Id);
	}

	@ApiOperation(value = "fetch all user", notes = "all user can be fetched with the call", response = UserUi.class, responseContainer = "Self")
	@RequestMapping(value = ApplicationProfilerConstants.ADMIN_URL, method = RequestMethod.GET, produces = ApplicationProfilerConstants.JSON)
	@ResponseBody
	public List<User> getUsers() throws AciEntityNotFound {
		return userServices.getUsers();
	}

}
