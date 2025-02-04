/*
EMMA THADINE WIJAYA
21022687 - MAD ASSN 2
Class to Use Retrofit to Query the Calorie Ninjas API
 */

package com.example.caltracker

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// retrofit instance
object RetrofitInstance {
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.calorieninjas.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: NutritionService by lazy {
        retrofit.create(NutritionService::class.java)
    }
}