package utils

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import java.sql.Timestamp

fun Timestamp.toDateTime(): DateTime {
    return DateTime(this.time)
}