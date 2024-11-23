package com.example.myapplication;

import android.content.Context;
import android.util.Log;

import java.io.IOException;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit;
    private static final String BASE_URL = "https://192.168.43.224:8081/";

    public static Retrofit getClient(Context context) throws Exception{
        try {


            if (retrofit == null) {
                retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .client(SSLHelper.createClientWithSelfSignedCert(context))
                        .addConverterFactory(ScalarsConverterFactory.create())
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
            }
        }
        catch (IOException e){
            Log.e("error",e.toString());
        }
        return retrofit;
    }


}

