package com.palo;

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
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
@EnableAutoConfiguration
@EnableTransactionManagement
public class DatabaseConfiguration implements EnvironmentAware {

	private final Logger log = LoggerFactory.getLogger(DatabaseConfiguration.class);

	private RelaxedPropertyResolver propertyResolver;

	private Environment environment;

	@Override
	public void setEnvironment(Environment environment) {
		this.environment = environment;
		this.propertyResolver = new RelaxedPropertyResolver(environment, "spring.datasource.");
	}

	@Bean
	public DataSource dataSource() {
		log.debug("Configuring Datasource");
		if (propertyResolver.getProperty("url") == null && propertyResolver.getProperty("databaseName") == null) {
			log.error(
					"Your database connection pool configuration is incorrect! The application"
							+ "cannot start. Please check your Spring profile, current profiles are: {}",
					Arrays.toString(environment.getActiveProfiles()));

			throw new ApplicationContextException("Database connection pool is not configured correctly");
		}
		HikariConfig config = new HikariConfig();

		if (propertyResolver.getProperty("url") == null || "".equals(propertyResolver.getProperty("url"))) {
			config.addDataSourceProperty("databaseName", propertyResolver.getProperty("databaseName"));
			config.addDataSourceProperty("serverName", propertyResolver.getProperty("serverName"));
		} else {
			config.addDataSourceProperty("url", propertyResolver.getProperty("url"));
		}

		config.addDataSourceProperty("user", propertyResolver.getProperty("user"));

		config.addDataSourceProperty("password", propertyResolver.getProperty("password"));

		config.setDataSourceClassName(propertyResolver.getProperty("dataSourceClassName"));

		config.setPoolName(propertyResolver.getProperty("poolName"));

		config.setMaximumPoolSize(Integer.valueOf(propertyResolver.getProperty("maximumPoolSize")));

		config.setMinimumIdle(Integer.valueOf(propertyResolver.getProperty("minimumIdle")));

		config.setConnectionTimeout(Long.valueOf(propertyResolver.getProperty("connectionTimeout")));

		config.setIdleTimeout(Long.valueOf(propertyResolver.getProperty("idleTimeout")));

		HikariDataSource datasource = new HikariDataSource(config);

		Properties dsProps = new Properties();
		dsProps.put("prepStmtCacheSize", Integer.valueOf(propertyResolver.getProperty("prepStmtCacheSize")));
		dsProps.put("cachePrepStmts", propertyResolver.getProperty("cachePrepStmts"));
		dsProps.put("useServerPrepStmts", propertyResolver.getProperty("useServerPrepStmts"));

		datasource.setDataSourceProperties(dsProps);
		return datasource;
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
		em.setDataSource(dataSource());
		em.setPackagesToScan(new String[] { "com.palo.domain" });

		em.getJpaPropertyMap().put("hibernate.cache.region.factory_class",
				"org.hibernate.cache.ehcache.SingletonEhCacheRegionFactory");
		em.getJpaPropertyMap().put("hibernate.generate_statistics", "true");
		em.getJpaPropertyMap().put("hibernate.cache.use_second_level_cache", "true");
		em.getJpaPropertyMap().put("hibernate.cache.use_query_cache", "true");
		em.getJpaPropertyMap().put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
		em.getJpaPropertyMap().put("hibernate.jdbc.batch_size", 100);
		em.getJpaPropertyMap().put("hibernate.order_inserts", true);
		em.getJpaPropertyMap().put("hibernate.order_updates", true);
		// em.getJpaPropertyMap().put("hibernate.show_sql", "true");
		// em.getJpaPropertyMap().put("org.hibernate.SQL", "DEBUG");

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

}
