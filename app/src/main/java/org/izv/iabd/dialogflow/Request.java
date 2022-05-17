package org.izv.iabd.dialogflow;

import android.util.Log;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Request {

    static public String TAG = "DRG-Request";
    static public String url = "informatica.ieszaidinvergeles.org:10056/pia/practica3/piapp/public";
    static public String url_coches = "david1994.pythonanywhere.com/cars/";

    static public void getPrices(MainActivity mainActivity, DialogFlowIntent dfIntent) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://" + url_coches)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        IzvServer client = retrofit.create(IzvServer.class);

        Call<Coche> call = client.getPrices(dfIntent.km, Coche.makes.get(dfIntent.make.toUpperCase()), dfIntent.year, "0", "0",
                "500", "2143", "160", "8.8", "4687", "1903");
        call.enqueue(new Callback<Coche>() {
            @Override
            public void onResponse(Call<Coche> call, Response<Coche> response) {
                Log.v(TAG, response.body().toString());
                Coche coche = response.body();
                String respuestaUsuario = "Su precio segun nuestra IA es:";
                respuestaUsuario += "\n- " + coche.prediction_mlp.get("0_MLPRegressor") + "\n";
                mainActivity.nuevaLinea(respuestaUsuario);
                mainActivity.hablar(respuestaUsuario);
            }

            @Override
            public void onFailure(Call<Coche> call, Throwable t) {
                Log.v(TAG, t.getLocalizedMessage());
            }
        });
    }

    static public void getCitasLibresEasy(MainActivity mainActivity) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://" + url + "/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        IzvServer client = retrofit.create(IzvServer.class);

        Call<ArrayList<Cita>> call = client.getEasy();
        call.enqueue(new Callback<ArrayList<Cita>>() {
            @Override
            public void onResponse(Call<ArrayList<Cita>> call, Response<ArrayList<Cita>> response) {
                Log.v(TAG, response.body().toString());
                ArrayList<Cita> citasLibres = response.body();
                String respuestaUsuario = "Las proximas citas libres son:";
                for (Cita cita : citasLibres) {
                    respuestaUsuario += "\n- " + cita.fecha + " a las " + cita.hora;
                }
                respuestaUsuario += "\n¿Cual de ellas le interesa?\n";
                mainActivity.nuevaLinea(respuestaUsuario);
                mainActivity.hablar(respuestaUsuario);
            }

            @Override
            public void onFailure(Call<ArrayList<Cita>> call, Throwable t) {
                Log.v(TAG, t.getLocalizedMessage());
            }
        });
    }

    static public void saveCita(MainActivity mainActivity, DialogFlowIntent dfIntent) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://" + url + "/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        IzvServer client = retrofit.create(IzvServer.class);

        String diaToSave = DateFormatter.getDateFormated(dfIntent.dia, DateFormatter.getDateFormat());
        String horaToSave = DateFormatter.getDateFormated(dfIntent.hora, DateFormatter.getTimeFormat());
        Call<Integer> call = client.isFreeCita(diaToSave, horaToSave);
        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                Log.v(TAG, response.body().toString());
                Integer body = response.body();
                if (body == 0) {
                    Call<Saved> callSave = client.saveCita(diaToSave, horaToSave);
                    callSave.enqueue(new Callback<Saved>() {
                        @Override
                        public void onResponse(Call<Saved> call, Response<Saved> response) {
                            Log.v(TAG, response.body().toString());
                            Saved body = response.body();
                            if (body.save) {
                                String responseAux = "Ya tiene su cita para el día ";

                                String onlyDay = DateFormatter.getDateFormated(dfIntent.dia, DateFormatter.getDayFormat());
                                String onlyMonth = DateFormatter.getDateFormated(dfIntent.dia, DateFormatter.getMonthFormat());
                                responseAux += onlyDay + " de " + onlyMonth + ", a las ";

                                String hour = DateFormatter.getDateFormated(dfIntent.hora, DateFormatter.getHourFormat());
                                responseAux += hour + " ";

                                responseAux += dfIntent.queryResponse.substring(56);
                                dfIntent.respuestaUsuario = responseAux;

                                mainActivity.saveInCalendar(dfIntent.nombre, dfIntent.fechaCorrecta);
                            } else {
                                dfIntent.respuestaUsuario = "Hubo un error, por favor, vuelva a intentarlo";
                            }
                            mainActivity.nuevaLinea(dfIntent.respuestaUsuario);
                            mainActivity.hablar(dfIntent.respuestaUsuario);
                        }

                        @Override
                        public void onFailure(Call<Saved> call, Throwable t) {
                            Log.v(TAG, t.getLocalizedMessage());
                            mainActivity.nuevaLinea("Hubo algun problema al reservar");
                            mainActivity.hablar("Hubo algun problema al reservar");
                        }
                    });
                } else {
                    dfIntent.respuestaUsuario = "La fecha ya estaba reservada, lo sentimos.";
                    mainActivity.nuevaLinea(dfIntent.respuestaUsuario);
                    mainActivity.hablar(dfIntent.respuestaUsuario);
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Log.v(TAG, t.getLocalizedMessage());
                mainActivity.nuevaLinea("Hubo algun problema al reservar");
                mainActivity.hablar("Hubo algun problema al reservar");
            }
        });
    }
}
