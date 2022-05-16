package org.izv.iabd.dialogflow;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface IzvServer {

    @GET("cita-libre/{fecha} ")
    Call<ArrayList<Cita>> get(@Path("fecha") String fecha);
}
