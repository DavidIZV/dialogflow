package org.izv.iabd.dialogflow;

import android.content.Context;
import android.util.Log;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.dialogflow.v2.DetectIntentRequest;
import com.google.cloud.dialogflow.v2.DetectIntentResponse;
import com.google.cloud.dialogflow.v2.QueryInput;
import com.google.cloud.dialogflow.v2.SessionName;
import com.google.cloud.dialogflow.v2.SessionsClient;
import com.google.cloud.dialogflow.v2.SessionsSettings;
import com.google.cloud.dialogflow.v2.TextInput;
import com.google.common.collect.Lists;

import java.io.InputStream;
import java.util.UUID;

public class DialogFlow {

    private SessionsClient sessionsClient;
    private SessionName sessionName;
    private String uuid = UUID.randomUUID().toString();
    private String TAG = "DRG-DialogFlow";

    public void initialize(Context context) {
        try {
            InputStream stream = context.getResources().openRawResource(R.raw.test2);
            GoogleCredentials credentials = GoogleCredentials.fromStream(stream)
                    .createScoped(Lists.newArrayList("https://www.googleapis.com/auth/cloud-platform"));
            String projectId = ((ServiceAccountCredentials) credentials).getProjectId();

            SessionsSettings.Builder settingsBuilder = SessionsSettings.newBuilder();
            SessionsSettings sessionsSettings = settingsBuilder.setCredentialsProvider(
                    FixedCredentialsProvider.create(credentials)).build();
            sessionsClient = SessionsClient.create(sessionsSettings);
            sessionName = SessionName.of(projectId, uuid);

            Log.d(TAG, "projectId : " + projectId);
        } catch (Exception e) {
            Log.d(TAG, "setUpBot: " + e.getMessage());
        }
    }

    public DetectIntentResponse speakToDialogFlow(String userMessage) {
        QueryInput queryInput = QueryInput.newBuilder()
                .setText(TextInput.newBuilder().setText(userMessage).setLanguageCode("spa-ES")).build();

        DetectIntentRequest detectIntentRequest =
                DetectIntentRequest.newBuilder()
                        .setSession(sessionName.toString())
                        .setQueryInput(queryInput)
                        .build();

        return sessionsClient.detectIntent(detectIntentRequest);
    }
}
