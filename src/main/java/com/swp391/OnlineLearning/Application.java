package com.swp391.OnlineLearning;

import com.swp391.OnlineLearning.config.StorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class Application {
    
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}

