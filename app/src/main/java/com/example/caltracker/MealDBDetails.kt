/*
EMMA THADINE WIJAYA
21022687 - MAD ASSN 2
Class containing the Meal Database and DAO
 */

package com.example.caltracker

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import java.util.Date

// declare entity with
@Entity(tableName = "meals")
data class Meal(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val calories: Float,
    val portionsize: Float,
    val proteins: Float,
    val carbohydrates: Float,
    val fats: Float,
    val imgurl: String,
    val type: Int,
    val date: String
)

// declare dao
@Dao
interface MealDao {
    // all queries
    @Query("SELECT * FROM meals")
    fun getAllMeals(): List<Meal>
    @Query("SELECT * FROM meals ORDER BY id DESC")
    fun getAllMealsDesc(): List<Meal>
    @Query("SELECT COUNT(*) FROM meals")
    fun getCount(): Int
    @Insert
    fun insertMeal(meal: Meal)
    @Query("SELECT * FROM meals WHERE id = :id")
    fun getMealById(id: Int): Meal
    @Query("SELECT * FROM meals WHERE date = :date ORDER BY id DESC")
    fun getMealsByDate(date: String): List<Meal>
    @Query("SELECT * FROM meals WHERE id IN (SELECT MAX(id) FROM meals GROUP BY DATE(date)) ORDER BY date DESC")
    fun getUniqueMealsByDate(): List<Meal>
    @Query("SELECT SUM(calories) AS totalCalories FROM meals GROUP BY date ORDER BY date DESC")
    fun getRecentTotalCalories(): List<Float>
    @Query("SELECT SUM(calories) AS totalCalories FROM meals WHERE date = :date")
    fun getCaloriesByDate(date: String): Float
    @Delete
    fun deleteMeal(meal: Meal)
    @Query("DELETE FROM meals")
    fun deleteAll()
}

@Database(entities = [Meal::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun mealDao(): MealDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "meal_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }

    object DatabaseProvider {
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "meal-database"
                ).allowMainThreadQueries().build()
                INSTANCE = instance
                instance
            }
        }

    }
}
