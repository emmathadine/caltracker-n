/*
EMMA THADINE WIJAYA
21022687 - MAD ASSN 2
Class that Queries the Nutrition API
 */

package com.example.caltracker

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

// interface for query
interface NutritionService {
    @GET("v1/nutrition")
    suspend fun getNutrition(
        @Query("query") query: String,
        @Header("X-Api-Key") apiKey: String
    ): NutritionResponse
}