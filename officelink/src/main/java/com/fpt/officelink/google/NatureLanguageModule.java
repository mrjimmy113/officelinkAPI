package com.fpt.officelink.google;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.google.api.gax.core.CredentialsProvider;
import com.google.auth.Credentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.Document.Type;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.LanguageServiceSettings;
import com.google.cloud.language.v1.Sentiment;

public class NatureLanguageModule {
	private static CredentialsProvider credentialsProvider = new CredentialsProvider() {

		@Override
		public Credentials getCredentials() throws IOException {
			ClassLoader classLoader = getClass().getClassLoader();
			File file = new File(classLoader.getResource("key.json").getFile());
			return ServiceAccountCredentials
					.fromStream(new FileInputStream(file));
		}
	};

	public static float analyzeSentiment(String text) throws IOException {
		LanguageServiceSettings languageServiceSettings = LanguageServiceSettings.newBuilder()
				.setCredentialsProvider(credentialsProvider).build();

		LanguageServiceClient language = LanguageServiceClient.create(languageServiceSettings);
		Document doc = Document.newBuilder().setContent(text).setType(Type.PLAIN_TEXT).build();

		// Detects the sentiment of the text
		Sentiment sentiment = language.analyzeSentiment(doc).getDocumentSentiment();
		language.shutdown();
		return (sentiment.getScore() * 10 + 10) / 2 ;
	}

}
