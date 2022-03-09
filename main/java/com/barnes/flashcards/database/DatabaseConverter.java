package com.barnes.flashcards.database;

import androidx.room.TypeConverter;

import com.barnes.flashcards.model.Flashcard;

import java.time.LocalDateTime;

/**
 * Converts data types for the SQLite database.
 *
 * <p> SQLite can only store null, integer, real, text, and blob in the database. This class is used
 * to convert between complex objects and these data types.</p>
 */
public class DatabaseConverter {
    @TypeConverter
    public static String fromStatus(Flashcard.Status status) {
        return status == null ? null : status.name();
    }

    @TypeConverter
    public static Flashcard.Status toStatus(String name) {
        return name == null ? null : Flashcard.Status.valueOf(name);
    }

    @TypeConverter
    public static String dateTimeToString(LocalDateTime date) {
        return date == null ? null : date.toString();
    }

    @TypeConverter
    public static LocalDateTime stringToDateTime(String dateString) {
        return dateString == null ? null : LocalDateTime.parse(dateString);
    }
}
