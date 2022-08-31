package kr.co.tbell.echeck.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EcheckClient {

    private static final String BASE_URL = "http://119.196.70.185:7744/meter/";
    private static Retrofit retrofit;

    public static EcheckAPI getOcrService() {
        return getInstance().create(EcheckAPI.class);
    }

    private static Retrofit getInstance() {
        Gson gson = new GsonBuilder().setLenient().create();

        if(retrofit == null) {
            Retrofit.Builder builder = new Retrofit.Builder();
            builder.baseUrl(BASE_URL);
            builder.addConverterFactory(GsonConverterFactory.create(gson));

            retrofit = builder.build();
        }

        return retrofit;
    }

}
