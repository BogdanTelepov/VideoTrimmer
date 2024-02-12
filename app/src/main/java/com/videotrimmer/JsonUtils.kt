package com.videotrimmer

import android.net.Uri
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type


/**
 * Parses json string
 * @param json string
 */

inline fun <reified T> fromJson(json: String?): T? = try {
    Gson().fromJson(json, T::class.java)
} catch (ex: Exception) {
    null
}

/**
 * Parses object to json string
 * @param obj
 */
inline fun <reified T> toJson(obj: T): String? = try {
    Gson().toJson(obj)
} catch (ex: Exception) {
    null
}

inline fun <reified T> parseArray(json: String, typeToken: Type): T {
    val gson = GsonBuilder().registerTypeAdapter(Uri::class.java, UriJsonAdapter()).create()
    return gson.fromJson<T>(json, typeToken)
}

class UriJsonAdapter : JsonSerializer<Uri>, JsonDeserializer<Uri> {
    override fun serialize(
        src: Uri,
        typeOfSrc: Type,
        context: JsonSerializationContext
    ): JsonElement {
        return JsonPrimitive(src.toString())
    }

    override fun deserialize(
        src: JsonElement,
        srcType: Type,
        context: JsonDeserializationContext
    ): Uri {
        return try {
            val url = src.asString
            if (url.isNullOrEmpty()) {
                Uri.EMPTY
            } else {
                Uri.parse(url)
            }
        } catch (e: UnsupportedOperationException) {
            Uri.EMPTY
        }
    }
}