package com.allra.assignment.config;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "allraEntityManagerFactory",
        transactionManagerRef = "allraTransactionManager",
        basePackages = {
                "com.allra.assignment.dev.item.repository",
                "com.allra.assignment.dev.order.repository",
                "com.allra.assignment.dev.user.repository",
                "com.allra.assignment.dev.cart.repository"
        }
)
public class DataSourceConfig {
    @Bean(name = "allraDataSource")
    @ConfigurationProperties(prefix = "spring.allra.datasource.hikari")
    public DataSource dataSource() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }


    @Bean(name = "allraEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(EntityManagerFactoryBuilder builder, @Qualifier("allraDataSource") DataSource dataSource) {

        Map<String, String> properties = new HashMap<>();
        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect");

        return builder.dataSource(dataSource).packages(
            "com.allra.assignment.dev.item.model.entity",
                            "com.allra.assignment.dev.cart.model.entity",
                            "com.allra.assignment.dev.user.model.entity",
                            "com.allra.assignment.dev.order.model.entity"
        ).persistenceUnit("allra").properties(properties).build();
    }

    @Bean(name = "allraTransactionManager")
    PlatformTransactionManager transactionManager(@Qualifier("allraEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

}
