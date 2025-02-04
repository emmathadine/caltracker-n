/*
EMMA THADINE WIJAYA
21022687 - MAD ASSN 2
Activity for User Settings
 */

package com.example.caltracker

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.caltracker.AppDatabase.DatabaseProvider
import com.example.caltracker.databinding.ActivityUserProfileBinding

class UserProfile : AppCompatActivity() {

    private lateinit var binding: ActivityUserProfileBinding

    // on create
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityUserProfileBinding.inflate(layoutInflater)

        setContentView(binding.root)

        // get db
        val db = DatabaseProvider.getDatabase(this)
        val mealDao = db.mealDao()
        // get goal
        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val goal = sharedPreferences.getFloat("goal",0f)

        // change goal
        binding.goal.setText(goal.toString())

        // update goal button
        binding.updategoal.setOnClickListener {
            val newgoal = binding.goal.text.toString().toFloat()
            with(sharedPreferences.edit())
            {
                putFloat("goal", newgoal)
                apply()
            }
            SweetAlertDialog(this,SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Updated!")
                .setContentText("Exit back to the main menu by clicking the return button!")
                .show()
        }

        // reset db button
        binding.wipedata.setOnClickListener{
            val sweetAlertDialog = SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Delete all user data?")
                .setContentText("You won't be able to revert this!")
                .setConfirmText("Yes, delete!")
                .setCancelText("No, cancel!")
                .setConfirmClickListener { sDialog ->

                    mealDao.deleteAll()
                    with(sharedPreferences.edit())
                    {
                        putFloat("goal", 0f)
                        apply()
                    }
                    binding.goal.setText("0")
                    sDialog
                        .setTitleText("Deleted!")
                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE)
                    sDialog.dismissWithAnimation()

                }
                .setCancelClickListener { sDialog ->
                    sDialog
                        .setTitleText("Cancelled")
                        .changeAlertType(SweetAlertDialog.ERROR_TYPE)
                    sDialog.dismissWithAnimation()
                }

            sweetAlertDialog.show()
        }

        // exit
        binding.exit.setOnClickListener {
            finish()
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}