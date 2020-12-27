package utils

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.InputStream

val gson: Gson = GsonBuilder().disableHtmlEscaping().create()

//fun <T> Gson.fromJson(inputStream: InputStream, objectClass: Class<T>): T {
//    val reader = inputStream.bufferedReader(Charsets.UTF_8)
//
//}