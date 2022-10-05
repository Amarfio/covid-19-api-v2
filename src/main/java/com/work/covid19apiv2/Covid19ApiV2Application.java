package com.work.covid19apiv2;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.FileInputStream;
import java.io.IOException;

@SpringBootApplication
public class Covid19ApiV2Application {



	public static void main(String[] args) throws IOException {

		//Service account settings for the firebase database
		FileInputStream serviceAccount = new FileInputStream("./src/main/resources/covid-db-41994-firebase-adminsdk-dcurz-3b971f1715.json");

		FirebaseOptions options = new FirebaseOptions.Builder()
				.setCredentials(GoogleCredentials.fromStream(serviceAccount))
				.build();
		FirebaseApp.initializeApp(options);

		SpringApplication.run(Covid19ApiV2Application.class, args);
	}

	@Bean
	public OpenAPI openApiConfig(){
		return new OpenAPI().info(apiInfo());
	}

	public Info apiInfo(){
		Info info = new Info();

		info	.title("Covid API v2")
				.description("Covid Tester Swagger Open API that uses user's initially captured temperate to determine their basic covid status.")
				.version("v2.0.0");
		return info;
	}

}
