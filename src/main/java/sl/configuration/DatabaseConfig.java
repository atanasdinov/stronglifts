package sl.configuration;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;


/**
 * <b>Database configuration class.</b>
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = {"sl.repository"})
@PropertySource(value = {"classpath:application.properties"})
public class DatabaseConfig {

    private static final String DB_DRIVER = "db.driver";
    private static final String DB_URL = "db.url";
    private static final String DB_USERNAME = "db.username";
    private static final String DB_PASSWORD = "db.password";

    @Autowired
    private Environment env;

    /**
     * DataSource configuration bean. Sets Username, Password and DB_URL.
     *
     * @return configured {@link HikariDataSource} object.
     */
    @Bean
    public DataSource dataSource() {
        HikariDataSource dataSource = new HikariDataSource();

        dataSource.setJdbcUrl(env.getProperty(DB_URL));
        dataSource.setUsername(env.getProperty(DB_USERNAME));
        dataSource.setPassword(env.getProperty(DB_PASSWORD));
        dataSource.setDriverClassName(env.getProperty(DB_DRIVER));

        return dataSource;
    }
}
