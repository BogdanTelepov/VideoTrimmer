package com.videotrimmer

import com.google.gson.Gson


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
inline fun <reified T> toJson(obj : T): String? = try {
    Gson().toJson(obj)
} catch (ex: Exception) {
    null
}
