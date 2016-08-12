package com.cisco.applicationprofiler.services;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import com.cisco.applicationprofiler.domain.Subject;
import com.cisco.applicationprofiler.exceptions.AciEntityNotFound;
import com.cisco.applicationprofiler.repo.SubjectRepository;

@Service
public class SubjectServices {
	@Inject
	private SubjectRepository subjectRepository;

	public Subject getSubject(int id) throws AciEntityNotFound {
		Subject subject  =subjectRepository.findOne(id);
		if (null == subject) {
			throw new AciEntityNotFound("subject do not exist");
		}
		return subject;
	}

	public List<Subject> getSubjects() {
		List<Subject> subject  =subjectRepository.findAll();
		return subject;
	}

	public Subject deleteSubjects(int id) throws AciEntityNotFound {
		Subject subject = subjectRepository.findOne(id);
		if (null == subject) {
			throw new AciEntityNotFound("subject do not exist");
		}
		subjectRepository.delete(id);
		return subject;
	}

}
