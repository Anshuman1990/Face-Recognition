package com.dh.project.Face_Recognition;

import com.dh.project.Face_Recognition.config.FileStorageProperties;
import nu.pattern.OpenCV;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@EnableConfigurationProperties({
		FileStorageProperties.class
})
public class FaceRecognitionApplication {

	public static void main(String[] args) {
		SpringApplicationBuilder builder = new SpringApplicationBuilder(FaceRecognitionApplication.class);
		builder.headless(false);
		ConfigurableApplicationContext context = builder.run(args);
		OpenCV.loadShared();
	}

}
