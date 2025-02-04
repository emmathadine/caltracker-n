/*
EMMA THADINE WIJAYA
21022687 - MAD ASSN 2
Activity that Displays Meals on a Specific Date
 */

package com.example.caltracker

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.caltracker.AppDatabase.DatabaseProvider
import com.example.caltracker.databinding.ActivityDailyMealDetailBinding


class DayMealDetail : AppCompatActivity() {

    // use viewbinding
    private lateinit var binding: ActivityDailyMealDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityDailyMealDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // initialize db
        val db = DatabaseProvider.getDatabase(this)
        val mealDao = db.mealDao()

        val count = mealDao.getCount()

        // check if any meals logged
        if (count<=0)
        {
            // if no, no need to show activity, display alert and exit
            SweetAlertDialog(this@DayMealDetail, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("No Meals Found!")
                .setContentText("Log meals in the main menu first!")
                .setConfirmClickListener {
                    it.dismissWithAnimation()

                    finish()
                }
                .show()
        }

        // if meals, continue

        val intent = intent
        binding.mealview.layoutManager = LinearLayoutManager(this,
            LinearLayoutManager.VERTICAL, false)

        // get passed date from intent caller activity
        val date = intent.getStringExtra("date")
        if (date != null)
        {
            if (date=="ALL")
            {
                // if all, then show all logged meals
                binding.title.text = "All Meals"
                binding.mealview.adapter = DailyMealDetailAdapter(mealDao.getAllMealsDesc())

            }
            else
            {
                // if actual date, query db and display all logged meals on that date
                binding.title.text = "Meals On "+date
                binding.mealview.adapter = DailyMealDetailAdapter(mealDao.getMealsByDate(date))

            }
        }

        // if exit button clicked, finish
        binding.exit.setOnClickListener{
            finish()
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}