package org.izv.iabd.dialogflow;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private EditText escrito;
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
            String theStatus = String.valueOf(status);
            Log.d("DRG", "Error: El valor del estado para tts es: " + theStatus);
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
        conversacion = findViewById(R.id.conversacion);
        tts = new TextToSpeech(this, this);
        sttLauncher = getSttLauncher();
        sttIntent = getSttIntent();
        dialogFlow = new DialogFlow();
        dialogFlow.initialize(this);

        findViewById(R.id.microfono).setOnClickListener(view ->
                sttLauncher.launch(sttIntent)
        );

        findViewById(R.id.enviar).setOnClickListener(view -> {
            String text = escrito.getText().toString();
            nuevaLinea(text);
            dialogFlow.proccessResponse(dialogFlow.speakToDialogFlow(text), this);
        });
    }

    public void nuevaLinea(String nuevaFrase) {
        String texto = (String) conversacion.getText();
        texto = "\n- " + nuevaFrase + texto;
        conversacion.setText(texto);
        escrito.setText("");
    }

    public void hablar(String message) {
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

    private void procesarFraseUsuario(ActivityResult result) {
        String text = "Error";
        if (result.getResultCode() == Activity.RESULT_OK) {
            Intent data = result.getData();
            assert data != null;
            List<String> r = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            text = r.get(0);
        }
        nuevaLinea(text);
        dialogFlow.proccessResponse(dialogFlow.speakToDialogFlow(text), this);
    }

    public void saveInCalendar(String nombre, String fecha) {
        long begin = DateFormatter.getMiliseconds(fecha);
        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.Events.TITLE, nombre)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, begin);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Log.d("DRG", "Error: No tenemos el paquete");
            nuevaLinea("No tenemos el paquete");
        }
    }
}