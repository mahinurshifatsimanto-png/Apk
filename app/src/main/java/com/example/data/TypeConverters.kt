package com.example.data

import androidx.room.TypeConverter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class Converters {
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val listStringAdapter = moshi.adapter<List<String>>(
        Types.newParameterizedType(List::class.java, String::class.java)
    )

    @TypeConverter
    fun fromStringList(list: List<String>?): String {
        return list?.let { listStringAdapter.toJson(it) } ?: "[]"
    }

    @TypeConverter
    fun toStringList(data: String?): List<String> {
        if (data.isNullOrEmpty()) return emptyList()
        return try {
            listStringAdapter.fromJson(data) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}
