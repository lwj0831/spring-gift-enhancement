package gift.product.config;

import gift.product.repository.JdbcProductRepository;
import gift.product.repository.JpaProductRepository;
import gift.product.repository.JpaProductRepositoryAdapter;
import gift.product.repository.ProductRepository;
import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class ProductRepositoryConfig {

    @Bean
    @Profile({"test", "dev"})
    public ProductRepository jpaProductRepositoryBean(JpaProductRepository jpaProductRepository) {
        return new JpaProductRepositoryAdapter(jpaProductRepository);
    }

    @Bean
    @Profile("default")
    public ProductRepository jdbcProductRepositoryBean(DataSource dataSource) {
        return new JdbcProductRepository(dataSource);
    }
}

