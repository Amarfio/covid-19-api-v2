package com.work.covid19apiv2;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.FileInputStream;
import java.io.IOException;

@SpringBootApplication
public class Covid19ApiV2Application {



	public static void main(String[] args) throws IOException {

		//Service account settings for the firebase database
		FileInputStream serviceAccount = new FileInputStream("./src/main/resources/serviceAccountKey.json");

		FirebaseOptions options = new FirebaseOptions.Builder()
				.setCredentials(GoogleCredentials.fromStream(serviceAccount))
				.build();
		FirebaseApp.initializeApp(options);

		SpringApplication.run(Covid19ApiV2Application.class, args);
	}

}
