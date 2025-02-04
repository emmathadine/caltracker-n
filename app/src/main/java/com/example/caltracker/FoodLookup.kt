/*
EMMA THADINE WIJAYA
21022687 - MAD ASSN 2
Activity that Displays Nutrition Result from API and Saves to Database
 */

package com.example.caltracker

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import android.provider.MediaStore
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.caltracker.AppDatabase.DatabaseProvider
import com.example.caltracker.databinding.ActivityFoodLookupBinding
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID

class FoodLookup : AppCompatActivity() {

    // initialize variables
    private lateinit var binding: ActivityFoodLookupBinding
    private lateinit var cameraLauncher: ActivityResultLauncher<Void?>
    private var shouldFinish = 0
    private var seen = 0


    // oncreate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFoodLookupBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        if (savedInstanceState != null) {
            seen = savedInstanceState.getInt("seen")?: 0
        }

        // if finished operation, exit
        shouldFinish = savedInstanceState?.getInt("finished")?: 0
        if (shouldFinish==1)
        {
            finish()
        }

        // init db
        val db = DatabaseProvider.getDatabase(this)
        val mealDao = db.mealDao()

        // launcher for camera
        cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap: Bitmap? ->
            if (bitmap != null) {
                // if taken image successfully, set imgview to new pic
                binding.foodimg.setImageBitmap(bitmap)
            }
            else
            {
                // alert user
                Toast.makeText(this, "No picture was taken.", Toast.LENGTH_SHORT).show()
            }
        }


        // launcher for gallery
        val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val selectedImageUri: Uri? = result.data?.data
                selectedImageUri?.let {
                    // let user pick image from gallery
                    binding.foodimg.setImageURI(it)
                }
            }
            else
            {
                // alert user
                Toast.makeText(this, "No picture was selected.", Toast.LENGTH_SHORT).show()
            }
        }

        // obtain data from intent of calling activity
        val name = intent.getStringExtra("name")
        val servingsize = intent.getDoubleExtra("servingsize", 0.0)
        val mealtype = intent.getStringExtra("mealtype")

        var type = -1

        // check if a meal was found from API
        val success = intent.getIntExtra("success",-1)
        if (success == 1)
        {
            // if yes, then get details from the intent and set to views
            val calories = intent.getDoubleExtra("calories", 0.0)
            val protein = intent.getDoubleExtra("protein", 0.0)
            val fat = intent.getDoubleExtra("fat", 0.0)
            val carbohydrates = intent.getDoubleExtra("carbohydrates", 0.0)

            // disable user from inputting data
            binding.calinput.isEnabled = false
            binding.calinput.setText(calories.toString())
            binding.psizeinput.isEnabled = false
            binding.psizeinput.setText(servingsize.toString())
            binding.carbinput.isEnabled = false
            binding.carbinput.setText(carbohydrates.toString())
            binding.protinput.isEnabled = false
            binding.protinput.setText(protein.toString())
            binding.fatinput.isEnabled = false
            binding.fatinput.setText(fat.toString())


        }
        else
        {
            if (seen == 0)
            {
                // if not found, alert user for manual input and let edit texts be enabled
                SweetAlertDialog(this@FoodLookup)
                    .setTitleText("Oops!")
                    .setContentText("We were unable to find your meal, please enter meal nutrition manually!")
                    .show()
                seen = 1
                if (savedInstanceState != null) {
                    savedInstanceState.putInt("seen", 1)
                }
            }

        }

        // set views to data
        binding.dailytitle.setText(name)

        // check correct radio button according to passed data
        when (mealtype) {
            "B.FAST" ->{
                binding.typesel.check(R.id.bfast)
                type = 1
            }
            "LUNCH" -> {
                binding.typesel.check(R.id.lunch)
                type = 2
            }
            "DINNER" -> {
                binding.typesel.check(R.id.dinner)
                type = 3
            }
            "SNACK" -> {
                binding.typesel.check(R.id.snack)
                type = 4
            }
            else -> binding.typesel.clearCheck()
        }

        // disable all radio buttons
        for (i in 0 until binding.typesel.childCount) {
            val radioButton = binding.typesel.getChildAt(i) as? RadioButton
            radioButton?.isEnabled = false
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // pick from gallery
        fun chooseFromGallery() {
            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            galleryLauncher.launch(galleryIntent)
        }

        // take photo
        fun takePhoto() {

            // check permissions first
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                 cameraLauncher.launch()
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 1001)
            }

        }


            // set click listener for image
            binding.foodimg.setOnClickListener{
            val options = arrayOf("Take a Photo!", "Choose Photo from Gallery!")
                // ask user to pick from gallery of camera
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Add a photo of the meal...")

            builder.setItems(options) { dialog, which ->
                when (which) {
                    0 -> takePhoto()
                    1 -> chooseFromGallery()
                }
            }

            builder.show()
        }

        // save button
        binding.save.setOnClickListener {
            shouldFinish = 1

            // if any empty, alert user
            if (binding.calinput.text.isEmpty() || binding.psizeinput.text.isEmpty() || binding.carbinput.text.isEmpty() || binding.protinput.text.isEmpty() || binding.fatinput.text.isEmpty())
            {
                SweetAlertDialog(this@FoodLookup)
                    .setTitleText("Oops!")
                    .setContentText("Please fill in all nutrition information!")
                    .show()
            }
            else
            {
                // get picture in img view
                val isDefault : Drawable? = binding.foodimg.drawable

                // check if all edit text contains floats
                if (binding.calinput.text.toString().toFloatOrNull() == null || binding.psizeinput.text.toString().toFloatOrNull() == null || binding.carbinput.text.toString().toFloatOrNull() == null || binding.protinput.text.toString().toFloatOrNull() == null || binding.fatinput.text.toString().toFloatOrNull() == null)
                {
                    // if not alert user
                    SweetAlertDialog(this@FoodLookup)
                        .setTitleText("Oops!")
                        .setContentText("Please enter valid numbers for nutrition information!")
                        .show()
                } // if yes then check if image has been selected or not
                else if (isDefault == null || isDefault.constantState == resources.getDrawable(R.drawable.pickimagebg).constantState)
                {
                    // if not alert user
                    SweetAlertDialog(this@FoodLookup)
                        .setTitleText("Oops!")
                        .setContentText("Please select an image for your meal!")
                        .show()
                }
                else {
                    // if yes then proceed with uploading image to Firebase Cloud Storage
                    shouldFinish = 1
                    var imageurl = ""

                    // get instance of storage
                    val storageRef = FirebaseStorage.getInstance().reference.child("images/${UUID.randomUUID()}.jpg")

                    // get image from image view
                    binding.foodimg.isDrawingCacheEnabled = true
                    binding.foodimg.buildDrawingCache()
                    val bitmap = (binding.foodimg.drawable as BitmapDrawable).bitmap
                    val baos = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos)
                    val data = baos.toByteArray()

                    // async operation, upload image to Firebase
                    val uploadTask = storageRef.putBytes(data)
                    uploadTask.addOnSuccessListener {
                        // when successful, get download url
                        storageRef.downloadUrl.addOnSuccessListener { uri ->
                            imageurl = uri.toString()

                            val currentDate = LocalDate.now()
                            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                            val systime =  currentDate.format(formatter)

                            val calories = binding.calinput.text.toString().toFloat()
                            val portion = binding.psizeinput.text.toString().toFloat()
                            val protein = binding.protinput.text.toString().toFloat()
                            val carb = binding.carbinput.text.toString().toFloat()
                            val fat = binding.fatinput.text.toString().toFloat()

                            // get all meal info to make a meal
                            val meal = name?.let { it1 -> Meal(name = it1, portionsize = portion, type = type, calories = calories, proteins = protein, carbohydrates = carb, fats = fat, date = systime, imgurl = imageurl) }


                            // if meal was made
                            if (meal != null) {
                                // save to db and finish
                                mealDao.insertMeal(meal)
                                SweetAlertDialog(this,SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("Image Successfully Uploaded!")
                                    .show()
                                finish()
                            }



                        }.addOnFailureListener {
                            // if upload failed, show alert
                            Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT)
                                .show()

                        }
                    }


                    // outside of async task, display loading screen fragment to user
                    showLoading(LoadingScreen())




                }
            }


        }






    }





    // makes current frame layout menu invisible and replaces it with loading fragment
    private fun showLoading(fragment: Fragment)
    {
        binding.lookupresult.visibility = View.INVISIBLE
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.menuframe, fragment)
        fragmentTransaction.commit()
    }

    // handles permission results for user camera
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode)
        {
            1001 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    cameraLauncher.launch()
                }
                else
                {
                    SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Oops!")
                        .setContentText("Please allow camera use to take photos!")
                        .show()
                }
            }
        }
    }

    // save instance state to handle image persistence and task completion upon rotate
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        val drawable = binding.foodimg.drawable as? BitmapDrawable
        drawable?.bitmap?.let { bitmap ->
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            outState.putByteArray("bitmap", stream.toByteArray())
        }

        outState.putInt("seen", seen)
        binding.calinput.text.toString().toFloatOrNull()?.let { outState.putFloat("cal", it) }
        binding.psizeinput.text.toString().toFloatOrNull()?.let { outState.putFloat("psize", it) }
        binding.carbinput.text.toString().toFloatOrNull()?.let { outState.putFloat("carb", it) }
        binding.protinput.text.toString().toFloatOrNull()?.let { outState.putFloat("prot", it) }
        binding.fatinput.text.toString().toFloatOrNull()?.let { outState.putFloat("fat", it) }

        outState.putInt("finished", shouldFinish)
    }

    // restore instance state to handle ^^^
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        val byteArray = savedInstanceState.getByteArray("bitmap")
        byteArray?.let {
            val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
            binding.foodimg.setImageBitmap(bitmap)
        }

        binding.calinput.setText(savedInstanceState.getFloat("cal").toString())
        binding.psizeinput.setText(savedInstanceState.getFloat("psize").toString())
        binding.carbinput.setText(savedInstanceState.getFloat("carb").toString())
        binding.protinput.setText(savedInstanceState.getFloat("prot").toString())
        binding.fatinput.setText(savedInstanceState.getFloat("fat").toString())
        seen = savedInstanceState.getInt("seen")

        //shouldFinish = savedInstanceState.getInt("finished",0)
    }





}


