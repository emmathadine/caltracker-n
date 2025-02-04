/*
EMMA THADINE WIJAYA
21022687 - MAD ASSN 2
RecyclerView Adapter to Display Meals in a specific Date in the View Activity
 */

package com.example.caltracker

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.caltracker.databinding.FoodCardBinding
import com.example.caltracker.databinding.MealCardBinding


class DailyMealDetailAdapter(private val meals: List<Meal>): RecyclerView.Adapter<DailyMealDetailAdapter.MealViewHolder>() {


    // create view holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealViewHolder {
        val binding = com.example.caltracker.databinding.MealCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MealViewHolder(binding)
    }

    // bind view holder
    override fun onBindViewHolder(holder: MealViewHolder, position: Int) {
        holder.bind(meals[position])
        // bind and set on click listener for cards
        holder.itemView.setOnClickListener {
            var intent = Intent(holder.itemView.context,MealDetail::class.java)
            intent.putExtra("id",meals[position].id)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return meals.size
    }


    // view holder class
    class MealViewHolder(private val binding: MealCardBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(meal: Meal){

            // set the view data to show meal details
            val requestOptions = RequestOptions().transform(RoundedCorners(30))

            var type =""
            when (meal.type) {
                1 -> type = "B.FAST"
                2 -> type = "LUNCH"
                3 -> type = "DINNER"
                4 -> type = "SNACK"
            }
            val data = type + " " + meal.calories.toString() + " CAL"
            binding.foodname.text = meal.name
            binding.fooddata.text = data
            Glide.with(binding.foodimg.context)
                .load(meal.imgurl)
                .apply(requestOptions)
                .into(binding.foodimg)

        }
    }
}