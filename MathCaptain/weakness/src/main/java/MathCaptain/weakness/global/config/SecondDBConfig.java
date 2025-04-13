package MathCaptain.weakness.global.config;

import javax.sql.DataSource;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "MathCaptain.weakness.domain.Record.repository.userLog", // 새 DB를 쓰는 Repository 위치
        entityManagerFactoryRef = "secondEntityManagerFactory",
        transactionManagerRef = "secondTransactionManager"
)
public class SecondDBConfig {

    // DataSourceProperties는 application.yml에 설정된 DB 정보를 읽어옴
    @Bean(name = "secondDataSourceProperties")
    @ConfigurationProperties(prefix = "second.datasource")
    public DataSourceProperties secondDataSourceProperties() {
        return new DataSourceProperties();
    }

    // DataSource는 DataSourceProperties를 통해 DB에 연결하는 객체
    @Bean(name = "secondDataSource")
    public DataSource secondDataSource() {
        return secondDataSourceProperties().initializeDataSourceBuilder().build();
    }

    // EntityManagerFactory는 JPA에서 DB와의 연결을 관리하는 객체
    @Bean(name = "secondEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean secondEntityManagerFactory(
            EntityManagerFactoryBuilder builder) {
        Map<String, Object> props = new HashMap<>();
        props.put("hibernate.hbm2ddl.auto", "update");
        props.put("hibernate.format_sql", true);
        props.put("hibernate.jdbc.lob.non_contextual_creation", true);

        return builder
                .dataSource(secondDataSource())
                .packages("MathCaptain.weakness.domain.Record.entity.UserLog") // entity 위치
                .properties(props)
                .persistenceUnit("second")
                .build();
    }

    // TransactionManager는 DB 트랜잭션을 관리하는 객체
    @Bean(name = "secondTransactionManager")
    public PlatformTransactionManager secondTransactionManager(
            @Qualifier("secondEntityManagerFactory") EntityManagerFactory factory) {
        return new JpaTransactionManager(factory);
    }
}
