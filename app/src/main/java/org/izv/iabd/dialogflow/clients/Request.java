package org.izv.iabd.dialogflow.clients;

import android.util.Log;

import org.izv.iabd.dialogflow.dates.DateFormatter;
import org.izv.iabd.dialogflow.dialogflow.DialogFlowIntent;
import org.izv.iabd.dialogflow.MainActivity;
import org.izv.iabd.dialogflow.models.Saved;
import org.izv.iabd.dialogflow.models.Cita;
import org.izv.iabd.dialogflow.models.Coche;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Request {

    private static final String TAG = "DRG-Request";
    static public String url = "informatica.ieszaidinvergeles.org:10056/pia/practica3/piapp/public";
    static public String url_coches = "david1994.pythonanywhere.com/cars/";

    static public void getPrices(MainActivity mainActivity, DialogFlowIntent dfIntent) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://" + url_coches)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        IzvServer client = retrofit.create(IzvServer.class);

        Call<Coche> call = client.getPrices(dfIntent.km, Coche.makes.get(dfIntent.make.toUpperCase()), dfIntent.year,
                Coche.trans.get(dfIntent.transmissionType), "0", Coche.bodyType.get(dfIntent.bodyType),
                "2143", dfIntent.hp, dfIntent.acceleration, "4687", "1903");
        call.enqueue(new Callback<Coche>() {
            @Override
            public void onResponse(Call<Coche> call, Response<Coche> response) {
                Log.v(TAG, response.body().toString());
                Log.v(TAG, response.raw().request().url().toString());
                Coche coche = response.body();

                ArrayList<Double> prices = new ArrayList<>();
                prices.add(Double.parseDouble(coche.prediction_mlp.get("0_MLPRegressor")));
                for (String valorCoche : coche.prediction_models.values()) {
                    prices.add(Double.parseDouble(valorCoche));
                }

                String min = String.valueOf(prices.get(0));
                String max = String.valueOf(prices.get(0));

                for (Double valorCoche : prices) {
                    if (valorCoche > Double.parseDouble(max)) {
                        max = String.valueOf(valorCoche);
                    }
                    if (valorCoche < Double.parseDouble(min)) {
                        min = String.valueOf(valorCoche);
                    }
                }

                String respuestaUsuario = "Su precio segun nuestra IA es:";
                respuestaUsuario += "\n- " + coche.prediction_mlp.get("0_MLPRegressor");
                respuestaUsuario += "\n- Pero podria oscilar entre estos precios: ";
                respuestaUsuario += "Desde " + min + " hasta " + max + "\n";
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
                StringBuilder respuestaUsuario = new StringBuilder("Las proximas citas libres son:");
                for (Cita cita : citasLibres) {
                    respuestaUsuario.append("\n- ").append(cita.fecha).append(" a las ").append(cita.hora);
                }
                respuestaUsuario.append("\n¿Cual de ellas le interesa?\n");
                mainActivity.nuevaLinea(respuestaUsuario.toString());
                mainActivity.hablar(respuestaUsuario.toString());
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
