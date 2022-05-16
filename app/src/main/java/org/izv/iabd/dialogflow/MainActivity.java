package org.izv.iabd.dialogflow;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.cloud.dialogflow.v2.DetectIntentResponse;

import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private EditText escrito;
    private Button microfono;
    private Button enviar;
    private TextView conversacion;
    private TextToSpeech tts;
    private boolean ttsReady = false;
    private ActivityResultLauncher<Intent> sttLauncher;
    private Intent sttIntent;
    private DialogFlow dialogFlow;

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            ttsReady = true;
            tts.setLanguage(new Locale("spa", "ES"));
        } else {
            Log.d("DRG", "Error: El valor del estado para tts es: " + String.valueOf(status));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialize();
    }

    private void initialize() {
        escrito = findViewById(R.id.escrito);
        microfono = findViewById(R.id.microfono);
        enviar = findViewById(R.id.enviar);
        conversacion = findViewById(R.id.conversacion);
        tts = new TextToSpeech(this, this);
        sttLauncher = getSttLauncher();
        sttIntent = getSttIntent();
        dialogFlow = new DialogFlow();
        dialogFlow.initialize(this);

        microfono.setOnClickListener(view -> {
            sttLauncher.launch(sttIntent);
        });

        enviar.setOnClickListener(view -> {
            String text = escrito.getText().toString();
            nuevaLinea(text);
            procesarRespuestaDialog(dialogFlow.speakToDialogFlow(text));
        });
    }

    public void nuevaLinea(String nuevaFrase) {
        String texto = (String) conversacion.getText();
        texto = "\n- " + nuevaFrase + texto;
        conversacion.setText(texto);
        escrito.setText("");
    }

    private void hablar(String message) {
        if (ttsReady) {
            tts.speak(message, TextToSpeech.QUEUE_ADD, null, null);
        } else {
            Log.d("DRG", "Error: No puedo hablar porque ttsReady es falso");
        }
    }

    private ActivityResultLauncher<Intent> getSttLauncher() {
        return registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                this::procesarFraseUsuario
        );
    }

    private Intent getSttIntent() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, new Locale("spa", "ES"));
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Te escucho");
        return intent;
    }

    public void saveInCalendar(String nombre, String fecha) {
        long begin = DateFormatter.getMiliseconds(fecha);
        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.Events.TITLE, nombre)
                //.putExtra(CalendarContract.Events.EVENT_LOCATION, location)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, begin);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Log.d("DRG", "Error: No tenemos el paquete");
            nuevaLinea("No tenemos el paquete");
        }
    }

    private void procesarFraseUsuario(ActivityResult result) {
        String text = "Error";
        if (result.getResultCode() == Activity.RESULT_OK) {
            List<String> r = result.getData().getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            text = r.get(0);
        }
        nuevaLinea(text);
        procesarRespuestaDialog(dialogFlow.speakToDialogFlow(text));
    }

    private void procesarRespuestaDialog(DetectIntentResponse respuestaDf) {
        String botReply = dialogFlow.getResponse(respuestaDf);
        nuevaLinea(botReply);
        hablar(botReply);

        if (botReply.contains(dialogFlow.actionLabel)) {
            saveInCalendar(dialogFlow.getNombre(respuestaDf), dialogFlow.getRightDate(respuestaDf));
        }
    }
}