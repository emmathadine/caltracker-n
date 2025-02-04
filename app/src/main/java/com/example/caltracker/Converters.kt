/*
EMMA THADINE WIJAYA
21022687 - MAD ASSN 2
Type Converters for Long to Date ROOM DAO
 */

package com.example.caltracker

import androidx.room.TypeConverter
import java.util.Date

class Converters {

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}