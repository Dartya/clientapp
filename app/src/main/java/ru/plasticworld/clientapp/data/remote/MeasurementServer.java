package ru.plasticworld.clientapp.data.remote;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.reactivex.Single;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class MeasurementServer {

    private static final String BASE_URL = "http://10.30.23.84:8080/";
    private final Api api;

    public MeasurementServer() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(BASE_URL)
                .build();

        api = retrofit.create(Api.class);
    }

    public Single<Response<Void>> sendMessage(Measurement message) {
        return api.sendMessage(message);
    }
}
