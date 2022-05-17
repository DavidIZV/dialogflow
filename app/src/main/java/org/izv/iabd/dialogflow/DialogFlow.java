package org.izv.iabd.dialogflow;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

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
    private final String uuid = UUID.randomUUID().toString();
    private final String TAG = "DRG-DialogFlow";
    private final String actionLabel = "Ya tiene su cita para el día ";
    private final String actionLabel_2 = "¿Para que día?";
    private final String actionLabel_3 = "¿A que hora?";

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

    public void proccessResponse(DetectIntentResponse respuestaDf, MainActivity mainActivity) {
        DialogFlowIntent response = new DialogFlowIntent();
        QueryResult queryResult = respuestaDf.getQueryResult();
        response.intent = queryResult.getIntent().getDisplayName();
        response.queryResponse = queryResult.getFulfillmentText();
        response.respuestaUsuario = queryResult.getFulfillmentText();
        response.fechaCorrecta = getRightDate(respuestaDf);

        Map<String, Value> fieldsMap = respuestaDf.getQueryResult().getParameters().getFieldsMap();
        response.dia = getValueOrDefault(fieldsMap, "dia");
        response.hora = getValueOrDefault(fieldsMap, "hora");

        if (DialogFlowIntent.intentCita.compareToIgnoreCase(response.intent) == 0) {
            doCitaIntent(response, mainActivity);
        } else if (DialogFlowIntent.intentLlama.compareToIgnoreCase(response.intent) == 0) {
            doLlamaIntent(response, mainActivity);
        } else if (DialogFlowIntent.intentBusca.compareToIgnoreCase(response.intent) == 0) {
            doBuscaIntent(response, mainActivity);
        } else {
            mainActivity.nuevaLinea(response.respuestaUsuario);
            mainActivity.hablar(response.respuestaUsuario);
        }
    }

    private void doCitaIntent(DialogFlowIntent response, MainActivity mainActivity) {
        if (response.queryResponse.contains(actionLabel)) {
            Request.saveCita(mainActivity, response);
        } else if (response.queryResponse.contains(actionLabel_2)) {
            Request.getCitasLibresEasy(mainActivity);
        } else if (response.queryResponse.contains(actionLabel_3)) {
            // Para controlar solo la hora
            mainActivity.nuevaLinea(response.respuestaUsuario);
            mainActivity.hablar(response.respuestaUsuario);
        } else {
            response.respuestaUsuario = response.queryResponse;
            mainActivity.nuevaLinea(response.respuestaUsuario);
            mainActivity.hablar(response.respuestaUsuario);
        }
    }

    private void doLlamaIntent(DialogFlowIntent response, MainActivity mainActivity) {

        mainActivity.nuevaLinea(response.respuestaUsuario);
        mainActivity.hablar(response.respuestaUsuario);
    }

    private void doBuscaIntent(DialogFlowIntent response, MainActivity mainActivity) {

        mainActivity.nuevaLinea(response.respuestaUsuario);
        mainActivity.hablar(response.respuestaUsuario);
    }

    public String getRightDate(DetectIntentResponse respuestaDf) {
        Map<String, Value> fieldsMap = respuestaDf.getQueryResult().getParameters().getFieldsMap();

        String dia = getValueOrDefault(fieldsMap, "dia");
        if (!dia.isEmpty()) {
            dia = dia.split("T")[0];
        }
        String hora = getValueOrDefault(fieldsMap, "hora");
        if (!hora.isEmpty()) {
            hora = hora.split("T")[1];
        }

        return dia + "T" + hora;
    }

    @NonNull
    private String getValueOrDefault(Map<String, Value> fieldsMap, String dayName) {
        String valor = "";
        Value value = fieldsMap.get(dayName);
        if (value != null && value.isInitialized()) {
            valor = value.getStringValue();
        }
        return valor;
    }
}
