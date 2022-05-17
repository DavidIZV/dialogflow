package org.izv.iabd.dialogflow;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface IzvServer {

    @GET("cita-libre/{fecha} ")
    Call<ArrayList<Cita>> get(@Path("fecha") String fecha);

    @GET("cita-libre-easy")
    Call<ArrayList<Cita>> getEasy();

    @GET("cambiar-cita/{fecha}/{hora}")
    Call<Saved> saveCita(@Path("fecha") String fecha, @Path("hora") String hora);

    @GET("is-reserved-cita/{fecha}/{hora}")
    Call<Integer> isFreeCita(@Path("fecha") String fecha, @Path("hora") String hora);
}
