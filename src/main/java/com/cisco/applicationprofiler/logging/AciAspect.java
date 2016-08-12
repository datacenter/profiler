/**
 *
 */
package com.cisco.applicationprofiler.logging;

import javax.inject.Inject;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;

/**
 * @author Mahesh
 *
 */
@Aspect
@Component
public class AciAspect {
	@Inject
	private Gson gson;
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@AfterReturning("execution(* com.cisco.acisizer.services.*.*(..))")
	public void logMethodAccessAfter(JoinPoint joinPoint) {
		log.info("***** Completed: " + joinPoint.getSignature() + " *****");
	}

	@Before("execution(* com.cisco.acisizer.services.*.*(..))")
	public void logMethodAccessBefore(JoinPoint joinPoint) {
		log.info("***** Starting: " +joinPoint.getSignature() + " *****");
		/*Object[] args = joinPoint.getArgs();
		for (int i = 0; i < args.length; i++) {
			log.info("With Arguments: " +args[i].getClass()+"{"+ gson.toJson(args[i])+"}");
		}*/

	}

}
