package com.cisco.applicationprofiler.services;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import com.cisco.applicationprofiler.domain.FilterEntry;
import com.cisco.applicationprofiler.exceptions.AciEntityNotFound;
import com.cisco.applicationprofiler.repo.FilterEntryRepository;

@Service
public class FilterEntryServices {
	@Inject
	private FilterEntryRepository filterEntryRepository;

	public List<FilterEntry> getFilterEntries() {
		List<FilterEntry> filterEntry = filterEntryRepository.findAll();
		return filterEntry;
	}

	public FilterEntry getFilterEntry(int id) throws AciEntityNotFound {
		FilterEntry filterEntry = ValidateAndGetFilterEntry(id);
		return filterEntry;
	}

	public FilterEntry deleteFilterEntry(int id) throws AciEntityNotFound {
		FilterEntry filterEntry = ValidateAndGetFilterEntry(id);
		filterEntryRepository.delete(id);
		return filterEntry;
	}

	private FilterEntry ValidateAndGetFilterEntry(int id) throws AciEntityNotFound {
		FilterEntry filterEntry = filterEntryRepository.findOne(id);
		if (null == filterEntry) {
			throw new AciEntityNotFound("FilterEntry do not exist");
		}
		return filterEntry;
	}

}
