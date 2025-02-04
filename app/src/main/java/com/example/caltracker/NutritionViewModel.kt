/*
EMMA THADINE WIJAYA
21022687 - MAD ASSN 2
ViewModel to Handle the Async Tasks of the Nutrition API
 */

package com.example.caltracker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NutritionViewModel : ViewModel() {

    // mutable data for api result
    private val _nutritionData = MutableStateFlow<NutritionResponse?>(null)
    val nutritionData: StateFlow<NutritionResponse?> get() = _nutritionData

    // mutable data for api error
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> get() = _errorMessage

    private val apiService = RetrofitInstance.api

    // get nutrition
    fun fetchNutrition(apiKey: String, query: String) {
        // async through view model
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    apiService.getNutrition(query, apiKey) // query api
                }
                _nutritionData.value = response
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Error fetching nutrition data: ${e.message}"
                _nutritionData.value = null
            }
        }
    }
}