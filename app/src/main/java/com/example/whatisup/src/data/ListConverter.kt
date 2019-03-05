package com.example.whatisup.src.data

import android.arch.persistence.room.TypeConverter
import com.example.whatisup.src.data.model.PhysicalActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ListConverter {

    @TypeConverter
    fun toJson(list: List<PhysicalActivity>): String {
        val gson = Gson()
        val json = gson.toJson(list)
        return json
    }

    @TypeConverter
    fun fromJson(json: String): List<PhysicalActivity> {
        val type = object: TypeToken<List<PhysicalActivity>>() {}.type
        return Gson().fromJson(json, type)
    }
}