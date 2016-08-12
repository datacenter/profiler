package com.cisco.applicationprofiler.services;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import com.cisco.applicationprofiler.models.LogicalSummary;
import com.cisco.applicationprofiler.domain.Project;
import com.cisco.applicationprofiler.exceptions.AciEntityNotFound;
import com.cisco.applicationprofiler.models.UserResponse;
import com.cisco.applicationprofiler.repo.ProjectRepository;
import com.cisco.applicationprofiler.repo.UserRepository;
import com.cisco.applicationprofiler.ui.models.ProjectUi;
import com.cisco.applicationprofiler.util.UserUtil;
import com.google.gson.Gson;
@Service
public class ProjectServices {

	@Inject
	private ProjectRepository projectRepository;

	@Inject
	private UserRepository userRepository;
	
	@Inject
	private Gson gson;

	public Project addProject(ProjectUi projectUi) {
		Project project = new Project();
		project.setName(projectUi.getName());
		project.setDescription(projectUi.getDescription());

		UserResponse response = UserUtil.getCurrentUser();
		Date date = new Date();
		LogicalSummary logicalSummary = new LogicalSummary();
		project.setLogicalSummary(logicalSummary);
		project.setCreatedTime(new Timestamp(date.getTime()));
		project.setLastUpdatedTime(new Timestamp(date.getTime()));
		project.setUser(userRepository.findOne(response.getUserId()));
		projectRepository.save(project);
		return project;
	}

	public Project updateProject(int id, ProjectUi projectUi) throws AciEntityNotFound {
		Project project = ValidateAndGetProject(id);
		UserResponse response = UserUtil.getCurrentUser();
		if (!(project.getUser().getId() == response.getUserId())) {
			throw new AciEntityNotFound("project not found");
		}

		project.setName(projectUi.getName());
		project.setDescription(projectUi.getDescription());
		Date date = new Date();
		project.setLastUpdatedTime(new Timestamp(date.getTime()));
		projectRepository.save(project);
		return project;
	}

	public Project deleteProject(int id) throws AciEntityNotFound {
		Project project = ValidateAndGetProject(id); // validation of project
														// existence
		UserResponse response = UserUtil.getCurrentUser();
		if (!(project.getUser().getId() == response.getUserId())) {
			throw new AciEntityNotFound("project not found");
		}
		projectRepository.delete(id);
		return project;
	}

	public Project getProject(int id) throws AciEntityNotFound {
		Project project = ValidateAndGetProject(id);
		UserResponse response = UserUtil.getCurrentUser();
		if (!(project.getUser().getId() == response.getUserId())) {
			throw new AciEntityNotFound("project not found");
		}

		return project;
	}

	public List<Project> getProjects() {
		UserResponse response = UserUtil.getCurrentUser();
		List<Project> project = projectRepository.findProjectsByUserId(response.getUserId());
		return project;
	}

	private Project ValidateAndGetProject(int id) throws AciEntityNotFound {
		Project project = projectRepository.findOne(id);
		if (null == project) {
			throw new AciEntityNotFound("Project do not exist");
		}
		return project;
	}

}
