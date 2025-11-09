package com.abraham.personalfinancemanagementapp.data.local.converters

import androidx.room.TypeConverter

/**
 * Type converter for Date to Long and vice versa
 */
class DateConverter {

    @TypeConverter
    fun fromTimestamp(value: Long?): java.util.Date? {
        return value?.let { java.util.Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: java.util.Date?): Long? {
        return date?.time
    }
}
