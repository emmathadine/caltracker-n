/*
EMMA THADINE WIJAYA
21022687 - MAD ASSN 2
RecyclerView Adapter
 */

package com.example.caltracker

import android.content.Intent
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.caltracker.databinding.FoodCardBinding


class RecentMealAdapter(private val meals: List<Meal>,
                        private val cals: List<Float>):
    RecyclerView.Adapter<RecentMealAdapter.MealViewHolder>() {

        // create vh
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealViewHolder {
        val binding = com.example.caltracker.databinding.FoodCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MealViewHolder(binding)
    }


    // bind to views
    override fun onBindViewHolder(holder: MealViewHolder, position: Int) {
        holder.bind(meals[position],cals[position])
        holder.itemView.setOnClickListener {
            var intent = Intent(holder.itemView.context,DayMealDetail::class.java)
            intent.putExtra("date",meals[position].date)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return meals.size
    }


    // set info properly
    class MealViewHolder(private val binding: FoodCardBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(meal: Meal, cals: Float){
            val requestOptions = RequestOptions().transform(CenterCrop(),RoundedCorners(30))
            val data = "$cals CAL"
            binding.foodname.text = meal.date
            binding.foodname.gravity = Gravity.CENTER
            binding.fooddata.text = data
            binding.fooddata.gravity = Gravity.CENTER
            Glide.with(binding.foodimg.context)
                .load(meal.imgurl)
                .apply(requestOptions)
                .into(binding.foodimg)

        }
    }
}