package com.ahmadini.velojot.data.local.converter

import androidx.room.TypeConverter
import com.ahmadini.velojot.domain.model.ChecklistItem
import org.json.JSONArray
import org.json.JSONObject

class Converters {
    @TypeConverter
    fun fromChecklistJson(json: String?): List<ChecklistItem> {
        if (json.isNullOrBlank()) return emptyList()
        val list = mutableListOf<ChecklistItem>()
        return try {
            val jsonArray = JSONArray(json)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(
                    ChecklistItem(
                        id = obj.optString("id", System.currentTimeMillis().toString() + "_" + i),
                        text = obj.optString("text", ""),
                        isDone = obj.optBoolean("isDone", false),
                        order = obj.optInt("order", i)
                    )
                )
            }
            list
        } catch (e: Exception) {
            emptyList()
        }
    }

    @TypeConverter
    fun toChecklistJson(items: List<ChecklistItem>?): String {
        if (items.isNullOrEmpty()) return "[]"
        val jsonArray = JSONArray()
        for (item in items) {
            val obj = JSONObject()
            obj.put("id", item.id)
            obj.put("text", item.text)
            obj.put("isDone", item.isDone)
            obj.put("order", item.order)
            jsonArray.put(obj)
        }
        return jsonArray.toString()
    }
}
