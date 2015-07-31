package net.devkhan.spring.repository.configuration;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.core.io.Resource;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Properties;

/**
 * Repository Spring Configuration.
 * Created by KHAN on 2015-07-30.
 */
@Configuration
@EnableTransactionManagement
@ComponentScan(
        basePackages = { "net.devkhan.spring.repository" },
        excludeFilters = @ComponentScan.Filter(value={Configuration.class}, type = FilterType.ANNOTATION)
)
public class RepositoryConfiguration {

    private static final Logger log = LoggerFactory.getLogger(RepositoryConfiguration.class);

    @Bean
    public Properties repoProps(@Value("classpath:repository.properties") Resource propFile)
            throws IOException {
        Properties props = new Properties();
        props.load(propFile.getInputStream());
        return props;
    }

    @Bean
    public DataSource restDataSource(
            @Value("#{repoProps['jdbc.url']}") String jdbcUrl,
            @Value("#{repoProps['jdbc.driverClassName']}") String jdbcDriverClassName,
            @Value("#{repoProps['jdbc.username']}") String jdbcUsername,
            @Value("#{repoProps['jdbc.password']}") String jdbcPassword) {

        log.debug(String.format("jdbcUrl: [%s], driverClassName: [%s], username: [%s], password: [%s]"
                , jdbcUrl, jdbcDriverClassName, jdbcUsername, jdbcPassword));

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(jdbcUrl);
        hikariConfig.setDriverClassName(jdbcDriverClassName);
        hikariConfig.setUsername(jdbcUsername);
        hikariConfig.setPassword(jdbcPassword);

        hikariConfig.setMaximumPoolSize(5);
        hikariConfig.setConnectionTestQuery("SELECT 1");
        hikariConfig.setPoolName("springHikariCP");
        hikariConfig.setIdleTimeout(30000);
        hikariConfig.setMaxLifetime(30000);

        hikariConfig.addDataSourceProperty("dataSource.cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("dataSource.prepStmtCacheSize", "250");
        hikariConfig.addDataSourceProperty("dataSource.prepStmtCacheSqlLimit", "2048");
        hikariConfig.addDataSourceProperty("dataSource.useServerPrepStmts", "true");

        HikariDataSource dataSource = new HikariDataSource(hikariConfig);
        return dataSource;
    }

    @Bean
    @Autowired
    public LocalSessionFactoryBean sessionFactory(
            DataSource dataSource,
            @Value("#{repoProps['scanPackages']}") String[] packages,
            @Value("#{repoProps['hibernate.hbm2ddl.auto']}") String hbm2ddlAuto,
            @Value("#{repoProps['hibernate.dialect']}") String dialect,
            @Value("#{repoProps['hibernate.show_sql']}") String showSql,
            @Value("#{repoProps['hibernate.format_sql']}") String formatSql
    ) {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        log.debug(String.format("hbm2ddlAuto: %s", hbm2ddlAuto));
        sessionFactory.setPackagesToScan(packages);
        sessionFactory.setHibernateProperties(hibernateProperties(hbm2ddlAuto, dialect, showSql, formatSql));

        return sessionFactory;
    }

    @Bean
    @Autowired
    public HibernateTransactionManager transactionManager(SessionFactory sessionFactory) {
        HibernateTransactionManager txManager = new HibernateTransactionManager();
        txManager.setSessionFactory(sessionFactory);
        return txManager;
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }

    Properties hibernateProperties(String hbm2ddlAuto, String dialect, String showSql, String formatSql) {
        return new Properties() {
            {
                setProperty("hibernate.hbm2ddl.auto", hbm2ddlAuto);
                setProperty("hibernate.dialect", dialect);
                setProperty("hibernate.show_sql", showSql);
                setProperty("hibernate.format_sql", formatSql);
                setProperty("hibernate.globally_quoted_identifiers", Boolean.TRUE.toString());
                setProperty("hibernate.generate_statistics", Boolean.TRUE.toString());
                setProperty("hibernate.cache.use_structured_entire", Boolean.TRUE.toString());
            }
        };
    }
}
