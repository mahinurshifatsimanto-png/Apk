package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        RoutineEntry::class,
        NoteEntry::class,
        RecordingItem::class,
        ChatMessage::class,
        TestPaper::class,
        UserStats::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class CollegeNotesDatabase : RoomDatabase() {
    abstract fun routineDao(): RoutineDao
    abstract fun noteDao(): NoteDao
    abstract fun recordingDao(): RecordingDao
    abstract fun chatMessageDao(): ChatMessageDao
    abstract fun testPaperDao(): TestPaperDao
    abstract fun userStatsDao(): UserStatsDao

    companion object {
        @Volatile
        private var INSTANCE: CollegeNotesDatabase? = null

        fun getDatabase(context: Context): CollegeNotesDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CollegeNotesDatabase::class.java,
                    "my_college_notes_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
