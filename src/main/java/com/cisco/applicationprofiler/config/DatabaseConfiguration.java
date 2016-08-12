package com.cisco.applicationprofiler.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaSessionFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import liquibase.integration.spring.SpringLiquibase;

@Configuration
@EnableAutoConfiguration

@EnableTransactionManagement

public class DatabaseConfiguration implements EnvironmentAware {

	private final Logger log = LoggerFactory
			.getLogger(DatabaseConfiguration.class);

	private RelaxedPropertyResolver propertyResolver;

	private Environment environment;

	@Override
	public void setEnvironment(Environment environment) {
		this.environment = environment;
		this.propertyResolver = new RelaxedPropertyResolver(environment,
				"spring.datasource.");
	}

	@Bean
	public DataSource dataSource() {
		log.debug("Configuring Datasource");
		if (propertyResolver.getProperty("url") == null
				&& propertyResolver.getProperty("databaseName") == null) {
			log.error(
					"Your database connection pool configuration is incorrect! The application"
							+ "cannot start. Please check your Spring profile, current profiles are: {}",
					Arrays.toString(environment.getActiveProfiles()));

			throw new ApplicationContextException(
					"Database connection pool is not configured correctly");
		}
		HikariConfig config = new HikariConfig();
		
		if (propertyResolver.getProperty("url") == null
				|| "".equals(propertyResolver.getProperty("url"))) {
			config.addDataSourceProperty("databaseName",
					propertyResolver.getProperty("databaseName"));
			config.addDataSourceProperty("serverName",
					propertyResolver.getProperty("serverName"));
		} else {
			config.addDataSourceProperty("url",
					propertyResolver.getProperty("url"));
		} // Sets dataSource property: url
		
		config.addDataSourceProperty("user",
				propertyResolver.getProperty("user")); // Sets dataSource property: username
		
		config.addDataSourceProperty("password",
				propertyResolver.getProperty("password")); // Sets dataSource property: password
		
		config.setDataSourceClassName(propertyResolver
				.getProperty("dataSourceClassName")); // Sets Hikari Config property: data source class name
				
		config.setPoolName(propertyResolver
				.getProperty("poolName")); // Sets Hikari Config property: poolName
		
		config.setMaximumPoolSize(Integer.valueOf(propertyResolver
				.getProperty("maximumPoolSize"))); // Sets Hikari Config property: maximumPoolSize
		
		config.setMinimumIdle(Integer.valueOf(propertyResolver
				.getProperty("minimumIdle"))); // Sets Hikari Config property: minimumIdle
		
		config.setConnectionTimeout(Long.valueOf(propertyResolver
				.getProperty("connectionTimeout"))); // Sets Hikari Config property: connectionTimeout
		
		config.setIdleTimeout(Long.valueOf(propertyResolver
				.getProperty("idleTimeout"))); // Sets Hikari Config property: idleTimeout
				
		HikariDataSource datasource = new HikariDataSource(config);
		
		Properties dsProps = new Properties();
	       dsProps.put("prepStmtCacheSize",Integer.valueOf(propertyResolver
					.getProperty("prepStmtCacheSize")));
	       dsProps.put("cachePrepStmts",propertyResolver
					.getProperty("cachePrepStmts"));
	       dsProps.put("useServerPrepStmts",propertyResolver
					.getProperty("useServerPrepStmts"));
		
	    datasource.setDataSourceProperties(dsProps);
		return datasource;
	}

	@Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource());
        em.setPackagesToScan(new String[] {"com.cisco.applicationprofiler.domain"});
        
        em.getJpaPropertyMap().put("hibernate.cache.region.factory_class",
                "org.hibernate.cache.ehcache.SingletonEhCacheRegionFactory");
        em.getJpaPropertyMap().put("hibernate.generate_statistics", "true");
        em.getJpaPropertyMap().put("hibernate.cache.use_second_level_cache", "true");
        em.getJpaPropertyMap().put("hibernate.cache.use_query_cache","true");
        em.getJpaPropertyMap().put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        em.getJpaPropertyMap().put("hibernate.jdbc.batch_size", 100);
        em.getJpaPropertyMap().put("hibernate.order_inserts", true);
        em.getJpaPropertyMap().put("hibernate.order_updates", true);
        //em.getJpaPropertyMap().put("hibernate.show_sql", "true");
        //em.getJpaPropertyMap().put("org.hibernate.SQL", "DEBUG");
        
        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
       
        em.setJpaProperties(additionalProperties());
        return em;
    }
	
	Properties additionalProperties() {
        Properties properties = new Properties();
        properties.setProperty("javax.persistence.sharedCache.mode", "ENABLE_SELECTIVE");
        
        return properties;
    }
	
	@Bean(name = { "org.springframework.boot.autoconfigure.AutoConfigurationUtils.basePackages" })
	public List<String> getBasePackages() {
		List<String> basePackages = new ArrayList<>();
		basePackages.add("com.cisco.applicationprofiler");
		return basePackages;
	}

	@Bean
	public SpringLiquibase liquibase() {
		log.debug("CoSpringLiquibasenfiguring Liquibase");
		SpringLiquibase liquibase = new SpringLiquibase();
		liquibase.setDataSource(dataSource());
		//liquibase.setChangeLog("classpath:config/schema_1.0.sql");
		liquibase.setChangeLog("classpath:config/master.xml");
		liquibase.setContexts("development, production");
		return liquibase;
	}
	
	@Bean
	public HibernateJpaSessionFactoryBean sessionFactory() {
	    return new HibernateJpaSessionFactoryBean();
	}
}


