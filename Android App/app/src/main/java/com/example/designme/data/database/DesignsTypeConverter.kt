package com.example.designme.data.database

import androidx.room.TypeConverter
import com.example.designme.models.designs
import com.example.designme.models.designsItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class DesignsTypeConverter {

    var gson = Gson()

    @TypeConverter
    fun foodRecipeToString(design: designs): String {
        return gson.toJson(design)
    }

    @TypeConverter
    fun stringToFoodRecipe(data: String): designs {
        val listType = object : TypeToken<designs>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun designitemtoString(foodRecipe: designsItem): String {
        return gson.toJson(foodRecipe)
    }

    @TypeConverter
    fun stringTodesignitem(data: String): designsItem {
        val listType = object : TypeToken<designsItem>() {}.type
        return gson.fromJson(data, listType)
    }

}
