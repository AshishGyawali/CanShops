package com.example.apitesting;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class ApiClient {
    private static final String BASE_URL = "https://api.api-ninjas.com/";
    private static Retrofit retrofit;

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            OkHttpClient client = new OkHttpClient.Builder().addInterceptor(chain -> {
                Request request = chain.request().newBuilder()
                        .addHeader("X-Api-Key", "TVYZB6PKQ9GWRPQ+VYKjSg==JvoWKAHLV40vccF7")
                        .build();
                return chain.proceed(request);
            }).build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}

