package com.example.icourse.repository

import android.content.Context
import com.example.icourse.model.Content
import com.example.icourse.model.Material
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException

object Repository {
    fun getMaterials(context: Context): MutableList<Material>?{
        val json: String
        try {
            val inputStream = context?.assets.open("json/materials.json")
            json = inputStream?.bufferedReader().use { it?.readText().toString() }
        } catch (e: IOException){
            e.printStackTrace()
            return null
        }
        val type = object : TypeToken<MutableList<Material>>() {}.type
        val materials = Gson().fromJson<MutableList<Material>>(json, type)
        materials?.let { return it }

        return null
    }

    fun getContents(context: Context): MutableList<Content>?{
        val json: String
        try {
            val inputStream = context?.assets.open("json/contents.json")
            json = inputStream?.bufferedReader().use { it?.readText().toString() }
        } catch (e: IOException){
            e.printStackTrace()
            return null
        }
        val type = object : TypeToken<MutableList<Content>>() {}.type
        val materials = Gson().fromJson<MutableList<Content>>(json, type)
        materials?.let { return it }

        return null
    }
}