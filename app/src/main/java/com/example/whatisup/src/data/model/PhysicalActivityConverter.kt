package com.example.whatisup.src.data.model

import androidx.room.TypeConverter
import android.util.Log
import com.google.gson.Gson

class PhysicalActivityConverter() {

    @TypeConverter
    fun fomActivity(activity: PhysicalActivity): String{
        val gson = Gson()
        val json = gson.toJson(activity)

        return json
    }

    @TypeConverter
    fun toPrimitives(activity:PhysicalActivity) {

    }
}