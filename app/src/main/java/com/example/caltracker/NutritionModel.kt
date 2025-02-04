/*
EMMA THADINE WIJAYA
21022687 - MAD ASSN 2
Class containing the Nutrition Details from API
 */

package com.example.caltracker

import com.google.gson.annotations.SerializedName

// declare the data classes for API
data class NutritionResponse(
    @SerializedName("items")
    val items: List<NutritionItem>
)

// item for API json parse
data class NutritionItem(
    @SerializedName("name")
    val name: String,
    @SerializedName("calories")
    val calories: Double,
    @SerializedName("protein_g")
    val protein: Double,
    @SerializedName("fat_total_g")
    val fat: Double,
    @SerializedName("carbohydrates_total_g")
    val carbohydrates: Double,
    @SerializedName("serving_size_g")
    val servingsize: Double
)