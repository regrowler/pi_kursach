package utils

import org.joda.time.DateTime

import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import java.util.*


fun parseDateTime(string: String): DateTime {
    val formatter: DateTimeFormatter = DateTimeFormat.forPattern("dd-MM-yyyy HH:mm")
    val dt: DateTime = formatter.parseDateTime(string)
    return dt
}

fun parseDate(string: String): DateTime {
    val formatter: DateTimeFormatter = DateTimeFormat.forPattern("dd-MM-yyyy")
    val dt: DateTime = formatter.parseDateTime(string)
    return dt
}

fun parseTime(string: String): DateTime {
    val formatter: DateTimeFormatter = DateTimeFormat.forPattern("HH:mm")
    val dt: DateTime = formatter.parseDateTime(string)
    return dt
}

fun DateTime.printDateTime(): String {
    val formatter: DateTimeFormatter = DateTimeFormat.forPattern("dd-MM-yyyy HH:mm")
    return formatter.print(this)
}

fun DateTime.printDate(): String {
    val formatter: DateTimeFormatter = DateTimeFormat.forPattern("dd-MM-yyyy")
    return formatter.print(this)
}

fun DateTime.printTime(): String {
    val formatter: DateTimeFormatter = DateTimeFormat.forPattern("HH:mm")
    return formatter.print(this)
}