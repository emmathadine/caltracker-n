/*
EMMA THADINE WIJAYA
21022687 - MAD ASSN 2
Main Activity that displays meals and goal progress
 */

package com.example.caltracker

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.caltracker.databinding.ActivityMainBinding
import com.example.caltracker.AppDatabase.DatabaseProvider
import com.google.firebase.FirebaseApp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import kotlin.math.abs

class MainActivity : AppCompatActivity() {

    // init binding
    private lateinit var binding: ActivityMainBinding

    // oncreate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        // get shared pref for goal
        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)

        // get the current date
        val systime = getDate()

        // get db
        val db = DatabaseProvider.getDatabase(this)
        val mealDao = db.mealDao()

        // update goal
        updateGoal(sharedPreferences, binding, mealDao, systime)

        // decide whether to display the blank screen or the one with recycler views
        val count = mealDao.getCount()

        if (count==0)
        {
            // show fragment
            showBlank(EmptyMenu())
        }
        else
        {
            // discard fragment and show real menu
            tossFragment()
            binding.menu.visibility =View.VISIBLE
        }


        FirebaseApp.initializeApp(this)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        //  click listeners for navigation buttons
        binding.add.setOnClickListener {
            startActivity(Intent(this, LogMeal::class.java))
        }

        binding.view.setOnClickListener{
            startActivity(Intent(this, DayMealDetail::class.java).putExtra("date","ALL"))

        }
        binding.user.setOnClickListener {
            startActivity(Intent(this, UserProfile::class.java))
        }


        // set manager and adapter for recycler views
        binding.dailyview.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false)
        binding.dailyview.adapter = DailyMealAdapter(mealDao.getMealsByDate(systime))

        binding.recentview.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false)
        binding.recentview.adapter = RecentMealAdapter(mealDao.getUniqueMealsByDate(),mealDao.getRecentTotalCalories())



    }

    // show blank fragment
    private fun showBlank(fragment: Fragment) {
        binding.menu.visibility = View.INVISIBLE
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.menuframe, fragment)
        fragmentTransaction.commit()
    }


    // on resume activity update the goal
    override fun onResume() {
        super.onResume()

        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)

        val systime =  getDate()

        val db = DatabaseProvider.getDatabase(this)
        val mealDao = db.mealDao()

        val count = mealDao.getCount()
        if (count==0)
        {
            showBlank(EmptyMenu())
        }
        else
        {
            tossFragment()

            binding.menu.visibility =View.VISIBLE
        }

        binding.dailyview.adapter = DailyMealAdapter(mealDao.getMealsByDate(systime))
        binding.recentview.adapter = RecentMealAdapter(mealDao.getUniqueMealsByDate(),mealDao.getRecentTotalCalories())

        updateGoal(sharedPreferences, binding, mealDao, systime)
    }

    // discard fragment in frame
    private fun tossFragment() {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        val fragment = fragmentManager.findFragmentById(R.id.menuframe)
        if (fragment != null) {
            fragmentTransaction.remove(fragment).commit()
        }
    }

    // function to get date
    private fun getDate():String
    {
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return currentDate.format(formatter)
    }

    // update the goal
    private fun updateGoal(sharedPreferences: SharedPreferences, binding: ActivityMainBinding, mealDao: MealDao, systime: String)
    {
        // get from saved var
        val goal = sharedPreferences.getFloat("goal",0f)
        val progress = mealDao.getCaloriesByDate(systime)
        val percentgoal = (progress/goal)*100
        // update progress bar
        binding.progressBar.progress = percentgoal.toInt()

        // change title and subheading to match current progress
        val left = goal - progress
        if (left<0)
        {
            binding.greeting.text = "%.2f".format(abs(left)) + " cal over daily goal"
        }
        else if (left>0)
        {
            binding.greeting.text = "%.2f".format(goal-progress) + " cal left to daily goal"

        }
        else
        {
            binding.greeting.text = "reached daily goal!"
        }

        binding.data.text = "%.2f".format(progress) + " cal today of " + "%.2f".format(goal) + " cal goal"

    }




}