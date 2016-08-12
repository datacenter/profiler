package com.cisco.applicationprofiler.repo;

import java.util.List;

import javax.inject.Inject;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Component;

import com.cisco.applicationprofiler.domain.RepoObjects;
import com.cisco.applicationprofiler.models.Repos;
import com.cisco.applicationprofiler.util.ApplicationProfilerConstants;

@Component
public class RepoDao {
	@Inject
	private SessionFactory sessionFactory;

	public Repos RepoDaoService(String searchString, int startRecord, int numRecords, String sourceDeviceName,
			String type, String startDate, String endDate, int deviceId) {
		Session session = sessionFactory.openSession();
		Criteria cr = session.createCriteria(RepoObjects.class);
		
		if (!(null == searchString)) {
			cr.add(Restrictions.like(ApplicationProfilerConstants.OBJECT_NAME, "%" + searchString + "%").ignoreCase());
		}
		if (!(null == sourceDeviceName)) {
			cr.add(Restrictions.like(ApplicationProfilerConstants.SOURCE_BASED, "%" + sourceDeviceName + "%")
					.ignoreCase());
		}
		
		if(!(deviceId==0)){
			cr.add(Restrictions.eq(ApplicationProfilerConstants.DEVICE_ID, +deviceId));
		}

		if (!("" == type)) {
			cr.add(Restrictions.like(ApplicationProfilerConstants.TYPE, type).ignoreCase());
		}

		if (!("" == startDate)) {
			cr.add(Restrictions.ge(ApplicationProfilerConstants.IMPORTED_ON, startDate));
		}
		if (!("" == endDate)) {
			cr.add(Restrictions.lt(ApplicationProfilerConstants.IMPORTED_ON, endDate));
		}
		int totalMatchedRecords = cr.list().size();
		cr.setFirstResult(startRecord - 1);
		cr.setMaxResults(numRecords);
		cr.addOrder(Order.asc(ApplicationProfilerConstants.OBJECT_NAME));

		List<RepoObjects> repos = cr.list();
		Repos repo = new Repos();
		repo.setRecords(repos);
		repo.setTotalRecords(totalMatchedRecords);
		session.close();
		return repo;
	}
}
