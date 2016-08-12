package com.cisco.applicationprofiler.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.cisco.applicationprofiler.domain.User;




public interface UserRepository extends JpaRepository<User, Integer>  {
	
	@Query("SELECT p FROM User p WHERE p.username=?1")	
	User findUserByUserName(String username);

}
