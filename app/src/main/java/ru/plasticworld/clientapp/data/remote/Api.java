package ru.plasticworld.clientapp.data.remote;

import io.reactivex.Single;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface Api {

    @POST("measurements")
    Single<Response<Void>> sendMessage(@Body Measurement measurement);
}
