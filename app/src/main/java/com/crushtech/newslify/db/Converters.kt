package com.crushtech.newslify.db

import androidx.room.TypeConverter
import com.crushtech.newslify.models.Source

class Converters {

    /**
     * since over room database doesn't understand custom objects
     * we need to interpret it, for that we need a converter class
     * this class converters our source class
     * @see Source
     */

    @TypeConverter
    fun fromSource(source: Source): String {
        return source.name
    }

    @TypeConverter
    fun toSource(name: String): Source {
        return Source(name, name)
    }
}