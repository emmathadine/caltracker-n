/*
EMMA THADINE WIJAYA
21022687 - MAD ASSN 2
Activity that Queries User for Meal Details and Launches Nutrition Lookup API
 */

package com.example.caltracker

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.caltracker.AppDatabase.DatabaseProvider
import com.example.caltracker.databinding.ActivityLogMealBinding
import kotlinx.coroutines.launch

class LogMeal : AppCompatActivity() {

    // init vars
    private lateinit var binding: ActivityLogMealBinding
    private lateinit var detailLauncher :ActivityResultLauncher<Intent>
    private val viewModel: NutritionViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {

        // handles result of FoodLookup activity launcher
        detailLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result -> finish()

        }


        super.onCreate(savedInstanceState)
        binding = ActivityLogMealBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // search button
        binding.search.setOnClickListener {
            // get name and portion size
            val name = binding.namecheck.text.toString()
            val psize = binding.portioncheck.text.toString()

            // ensure no null and a meal type has been chosen
            if (name.isEmpty() || psize.isEmpty() || binding.typesel.checkedRadioButtonId == -1) {
                SweetAlertDialog(this@LogMeal, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Slow Down!")
                    .setContentText("Please enter all meal details first!")
                    .show()
            }
            else
            {
                // query api with meal
                val meal = psize + " " + name
                viewModel.fetchNutrition("tGGB80BQKGVyzaUgrQUzrw==fNkMsgPjPQVd270b", meal)
                showLoading(LoadingScreen())
            }

        }

        // use lifecycleScope async task handler to watch result of API query
        lifecycleScope.launch {
            viewModel.nutritionData.collect { nutritionResponse ->
                nutritionResponse?.let {
                    // if theres a result, send info to next activity
                    displayNutritionInfo(it.items)
                }
            }
        }

        // same as ^^^ for errors
        lifecycleScope.launch {
            viewModel.errorMessage.collect { errorMessage ->
                errorMessage?.let {
                    // no meals found, send that info to next
                    mealnotfound()

                }
            }
        }
    }

    // function to format nutrition info
    private fun displayNutritionInfo(items: List<NutritionItem>) {

        // check buttons for which type
        val mealtype = when (binding.typesel.checkedRadioButtonId) {
            R.id.bfast -> "B.FAST"
            R.id.lunch -> "LUNCH"
            R.id.dinner -> "DINNER"
            R.id.snack -> "SNACK"
            else -> "NULL"
        }
        if (!items.isEmpty())
        {
            // put data into intent
            intent = Intent(this, FoodLookup::class.java)
                .putExtra("success",1)
                .putExtra("name", items[0].name)
                .putExtra("mealtype",mealtype)
                .putExtra("calories", items[0].calories)
                .putExtra("protein", items[0].protein)
                .putExtra("fat", items[0].fat)
                .putExtra("carbohydrates", items[0].carbohydrates)
                .putExtra("servingsize", items[0].servingsize)

            // launch
            detailLauncher.launch(intent)
        }
        else
        {
            // if no items, call fun
            mealnotfound()
        }

    }

    // handles not found
    private fun mealnotfound()
    {
        // get meal type
        val mealtype = when (binding.typesel.checkedRadioButtonId) {
            R.id.bfast -> "B.FAST"
            R.id.lunch -> "LUNCH"
            R.id.dinner -> "DINNER"
            R.id.snack -> "SNACK"
            else -> "NULL"
        }

        // pass as unsuccessful query
        intent = Intent(this, FoodLookup::class.java)
            .putExtra("success",0)
            .putExtra("name", binding.namecheck.text.toString())
            .putExtra("mealtype",mealtype)
            .putExtra("servingsize", binding.portioncheck.text.toString())

        // launch next activity
        detailLauncher.launch(intent)
    }

    // show loading fragment
    private fun showLoading(fragment: Fragment)
    {
        // hide search frame and replace with fragment
        binding.searchmenu.visibility = View.INVISIBLE
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.searchframe, fragment)
        fragmentTransaction.commit()
    }

}