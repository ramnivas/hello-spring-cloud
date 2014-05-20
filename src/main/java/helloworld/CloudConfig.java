package helloworld;

import org.springframework.cloud.config.java.AbstractCloudConfig;
import org.springframework.cloud.config.java.ServiceScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@ServiceScan
@Profile("cloud")
public class CloudConfig extends AbstractCloudConfig {
//    @Bean
//    public ConnectionFactory rabbitConnectionFactory() {
//        return connectionFactory().rabbitConnectionFactory();
//    }
//    
//    @Bean
//    public DataSource dataSource() {
//        return connectionFactory().dataSource();
//    }
//
//    @Bean
//    public MongoDbFactory mongoDb() {
//        return connectionFactory().mongoDbFactory();
//    }

//    @Bean
//    public RedisConnectionFactory redisConnectionFactory() {
//        return connectionFactory().redisConnectionFactory();
//    }
}
