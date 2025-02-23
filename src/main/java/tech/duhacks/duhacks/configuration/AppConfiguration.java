package tech.duhacks.duhacks.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfiguration {

    @Bean
    public Thread threadBean(Runnable runnable){
        return new Thread(runnable);
    }
}
