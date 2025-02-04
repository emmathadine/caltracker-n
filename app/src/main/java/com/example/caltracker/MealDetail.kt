/*
EMMA THADINE WIJAYA
21022687 - MAD ASSN 2
Activity that Displays the Details of a Meal
 */

package com.example.caltracker

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.caltracker.AppDatabase.DatabaseProvider
import com.example.caltracker.databinding.ActivityMealDetailBinding

class MealDetail : AppCompatActivity() {

    private lateinit var binding: ActivityMealDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMealDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // initialize db
        val db = DatabaseProvider.getDatabase(this)
        val mealDao = db.mealDao()

        val intent = intent

        // get meal id from intent
        val mealId = intent.getIntExtra("id", -1)
        if (mealId != -1)
        {

            // get meal from db
            val meal = mealDao.getMealById(mealId)

            // concat the nutrients
            val nutrients ="Calories: "+meal.calories.toString()+" cal\nPortion Size: "+meal.portionsize.toString() + " g\nProteins: " + meal.proteins.toString() + "g\nFats: " + meal.fats.toString() + "g\nCarbs:" + meal.carbohydrates.toString() + "g"

            // set all the info to the views
            binding.title.text = meal.name
            binding.nutritioninfo.text = nutrients
            binding.radbut.isEnabled = false
            binding.bfast.isEnabled = false
            binding.lunch.isEnabled = false
            binding.dinner.isEnabled = false
            binding.snack.isEnabled = false
            when (meal.type)
            {
                1 -> binding.bfast.isChecked = true
                2 -> binding.lunch.isChecked = true
                3 -> binding.dinner.isChecked = true
                4 -> binding.snack.isChecked = true
            }

            // get the image and set it to img view with glide
            val requestOptions = RequestOptions().transform(CenterCrop(),RoundedCorners(30))

            Glide.with(binding.foodimg.context)
                .load(meal.imgurl)
                .apply(requestOptions)
                .into(binding.foodimg)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // exit button
        binding.exit.setOnClickListener {
            finish()
        }
    }
}