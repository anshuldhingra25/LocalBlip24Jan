package com.ordiacreativeorg.localblip.api;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;

import java.util.concurrent.TimeUnit;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

/**
 * Created Sergey Mitrofanov (goretz.m@gmail.com) on 11/5/2015
 * <p>
 * Restfull Api singletone
 */
//http://dev2.localblip.com/admin/newblipsapicustom.php?email=deepa@bharti.com&apikey=14f8000409a34ac27e9991b70a0942ac
public class Api {

    private static volatile Api instance;

    public static Api getInstance() {
        Api localInstance = instance;
        if (localInstance == null) {
            synchronized (Api.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new Api();
                }
            }
        }
        return localInstance;
    }

    private final ApiMethodsInterface apiMethods;

    private Api() {
        // For logging
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        // set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.NONE);

        OkHttpClient httpClient = new OkHttpClient();
        // add your other interceptors …

        // add logging as last interceptor
        httpClient.interceptors().add(logging);
        httpClient.setConnectTimeout(15, TimeUnit.MINUTES); // connect timeout
        httpClient.setReadTimeout(15, TimeUnit.MINUTES);
        httpClient.setWriteTimeout(30, TimeUnit.MINUTES);
        Retrofit restAdapter = new Retrofit.Builder()
                .baseUrl("https://localblip.com/")
                .addConverterFactory(GsonConverterFactory.create())
                // For logging
                .client(httpClient)
                .build();
        apiMethods = restAdapter.create(ApiMethodsInterface.class);
    }

    public ApiMethodsInterface getMethods() {
        return apiMethods;
    }
}
