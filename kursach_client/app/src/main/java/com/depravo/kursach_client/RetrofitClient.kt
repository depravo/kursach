package com.example.myclient.Retrofit

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit


object RetrofitClient {
    private var retrofitClient:Retrofit?=null

    val client:Retrofit
        get() {
            if (retrofitClient == null){
                val okHttpClient = OkHttpClient.Builder()
                    .connectTimeout(1, TimeUnit.MINUTES)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(15, TimeUnit.SECONDS)
                    .build()
                retrofitClient = Retrofit.Builder()
                    .baseUrl("http://192.168.100.10:8080")
                    .client(okHttpClient)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build()

            }
            return retrofitClient!!
        }
}