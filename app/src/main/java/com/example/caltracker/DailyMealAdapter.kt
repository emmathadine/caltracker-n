/*
EMMA THADINE WIJAYA
21022687 - MAD ASSN 2
RecyclerView Adapter to Display Meals in a specific Date in the Main Activity
 */

package com.example.caltracker

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.caltracker.databinding.FoodCardBinding


class DailyMealAdapter(private val meals: List<Meal>): RecyclerView.Adapter<DailyMealAdapter.MealViewHolder>() {



    // create view holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealViewHolder {
        val binding = com.example.caltracker.databinding.FoodCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MealViewHolder(binding)
    }

    // bind view holder
    override fun onBindViewHolder(holder: MealViewHolder, position: Int) {
        // bind each meal to a food card and give the card a listener
        holder.bind(meals[position])
        holder.itemView.setOnClickListener {
            // listener takes the id of the clicked meal and opens the meal detail activity
            var intent = Intent(holder.itemView.context,MealDetail::class.java)
            intent.putExtra("id",meals[position].id)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return meals.size
    }

    // view holder class

    class MealViewHolder(private val binding: FoodCardBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(meal: Meal){
            // for glide image options
            val requestOptions = RequestOptions().transform(CenterCrop(),RoundedCorners(30))

            // get data from the meal object
            var type =""
            when (meal.type) {
                1 -> type = "B.FAST"
                2 -> type = "LUNCH"
                3 -> type = "DINNER"
                4 -> type = "SNACK"
            }
            val data = type + "\n" + meal.calories.toString() + " CAL"

            // set food card to display the meal details
            binding.foodname.text = meal.name
            binding.fooddata.text = data

            // glide dependency attaches the correct image from link
            Glide.with(binding.foodimg.context)
                .load(meal.imgurl)
                .apply(requestOptions)
                .into(binding.foodimg)

        }
    }
}