package org.izv.iabd.dialogflow.clients;

import org.izv.iabd.dialogflow.models.Saved;
import org.izv.iabd.dialogflow.models.Cita;
import org.izv.iabd.dialogflow.models.Coche;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface IzvServer {

    @GET("cita-libre/{fecha} ")
    Call<ArrayList<Cita>> get(@Path("fecha") String fecha);

    @GET("cita-libre-easy")
    Call<ArrayList<Cita>> getEasy();

    @GET("cambiar-cita/{fecha}/{hora}")
    Call<Saved> saveCita(@Path("fecha") String fecha, @Path("hora") String hora);

    @GET("is-reserved-cita/{fecha}/{hora}")
    Call<Integer> isFreeCita(@Path("fecha") String fecha, @Path("hora") String hora);

    @GET("prediction")
    Call<Coche> getPrices(@Query("km") String km, @Query("make") String make, @Query("year") String year,
                          @Query("transmissionType") String transmissionType, @Query("seller_type") String seller_type,
                          @Query("bodyType") String bodyType, @Query("cubicCapacity") String cubicCapacity,
                          @Query("hp") String hp, @Query("acceleration") String acceleration,
                          @Query("length") String length, @Query("width") String width);
}
