package org.izv.iabd.dialogflow;

import android.content.Context;
import android.util.Log;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.dialogflow.v2.DetectIntentRequest;
import com.google.cloud.dialogflow.v2.DetectIntentResponse;
import com.google.cloud.dialogflow.v2.QueryInput;
import com.google.cloud.dialogflow.v2.QueryResult;
import com.google.cloud.dialogflow.v2.SessionName;
import com.google.cloud.dialogflow.v2.SessionsClient;
import com.google.cloud.dialogflow.v2.SessionsSettings;
import com.google.cloud.dialogflow.v2.TextInput;
import com.google.common.collect.Lists;
import com.google.protobuf.Value;

import java.io.InputStream;
import java.util.Map;
import java.util.UUID;

public class DialogFlow {

    private SessionsClient sessionsClient;
    private SessionName sessionName;
    private String uuid = UUID.randomUUID().toString();
    private String TAG = "DRG-DialogFlow";
    public String actionLabel = "Ya tiene su cita para el día ";

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

    public String getResponse(DetectIntentResponse respuestaDf) {
        String response = "";
        QueryResult queryResult = respuestaDf.getQueryResult();
        String queryResponse = queryResult.getFulfillmentText();
        if (queryResponse.contains(actionLabel)) {
            String responseAux = actionLabel;

            Map<String, Value> fieldsMap = respuestaDf.getQueryResult().getParameters().getFieldsMap();
            String day = fieldsMap.get("dia").getStringValue();
            String onlyDay = DateFormatter.getDateFormated(day, DateFormatter.getDayFormat());
            String onlyMonth = DateFormatter.getDateFormated(day, DateFormatter.getDayFormat());
            responseAux += onlyDay + " de " + onlyMonth + ", a las ";

            String hour = fieldsMap.get("hora").getStringValue();
            hour = DateFormatter.getDateFormated(hour, DateFormatter.getHourFormat());
            responseAux += hour;

            responseAux += queryResponse.substring(55);
            response = responseAux;
        } else {
            response = queryResponse;
        }
        return response;
    }

    public String getDay(DetectIntentResponse respuestaDf) {
        String queryResponse = respuestaDf.getQueryResult().getFulfillmentText();
        Map<String, Value> fieldsMap = respuestaDf.getQueryResult().getParameters().getFieldsMap();
        String day = fieldsMap.get("dia").getStringValue();
        return DateFormatter.getDateFormated(day, DateFormatter.getDayFormat());
    }

    public String getHour(DetectIntentResponse respuestaDf) {
        String queryResponse = respuestaDf.getQueryResult().getFulfillmentText();
        Map<String, Value> fieldsMap = respuestaDf.getQueryResult().getParameters().getFieldsMap();
        String hour = fieldsMap.get("hora").getStringValue();
        return DateFormatter.getDateFormated(hour, DateFormatter.getHourFormat());
    }

    public String getRightDate(DetectIntentResponse respuestaDf) {
        String queryResponse = respuestaDf.getQueryResult().getFulfillmentText();
        Map<String, Value> fieldsMap = respuestaDf.getQueryResult().getParameters().getFieldsMap();

        Value diaResponse = fieldsMap.get("dia");
        String dia = String.valueOf(diaResponse.getStringValue()).split("T")[0];
        Value horaResponse = fieldsMap.get("hora");
        String hora = String.valueOf(horaResponse.getStringValue()).split("T")[1];

        return dia + "T" + hora;
    }

    public String getNombre(DetectIntentResponse respuestaDf) {
        String queryResponse = respuestaDf.getQueryResult().getFulfillmentText();
        Map<String, Value> fieldsMap = respuestaDf.getQueryResult().getParameters().getFieldsMap();
        return fieldsMap.get("nombre").getStringValue();
    }
}
