package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RoutineDao {
    @Query("SELECT * FROM routine_entries ORDER BY id ASC")
    fun getAllRoutines(): Flow<List<RoutineEntry>>

    @Query("SELECT * FROM routine_entries WHERE dayOfWeek = :day ORDER BY startTime ASC")
    fun getRoutinesForDay(day: String): Flow<List<RoutineEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutine(entry: RoutineEntry): Long

    @Update
    suspend fun updateRoutine(entry: RoutineEntry)

    @Delete
    suspend fun deleteRoutine(entry: RoutineEntry)

    @Query("DELETE FROM routine_entries WHERE id = :id")
    suspend fun deleteRoutineById(id: Long)
}

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes ORDER BY createdAtTimestamp DESC")
    fun getAllNotes(): Flow<List<NoteEntry>>

    @Query("SELECT * FROM notes WHERE subject = :subject ORDER BY createdAtTimestamp DESC")
    fun getNotesBySubject(subject: String): Flow<List<NoteEntry>>

    @Query("SELECT * FROM notes WHERE id = :id")
    suspend fun getNoteById(id: Long): NoteEntry?

    @Query("SELECT * FROM notes WHERE title LIKE '%' || :query || '%' OR subject LIKE '%' || :query || '%' OR summaryMarkdown LIKE '%' || :query || '%'")
    fun searchNotes(query: String): Flow<List<NoteEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteEntry): Long

    @Delete
    suspend fun deleteNote(note: NoteEntry)
}

@Dao
interface RecordingDao {
    @Query("SELECT * FROM recordings ORDER BY timestamp DESC")
    fun getAllRecordings(): Flow<List<RecordingItem>>

    @Query("SELECT * FROM recordings WHERE isProcessed = 0")
    suspend fun getUnprocessedRecordings(): List<RecordingItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecording(recording: RecordingItem): Long

    @Update
    suspend fun updateRecording(recording: RecordingItem)
}

@Dao
interface ChatMessageDao {
    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getAllMessages(): Flow<List<ChatMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessage): Long

    @Query("DELETE FROM chat_messages")
    suspend fun clearHistory()
}

@Dao
interface TestPaperDao {
    @Query("SELECT * FROM test_papers ORDER BY id DESC")
    fun getAllTestPapers(): Flow<List<TestPaper>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTestPaper(testPaper: TestPaper): Long

    @Update
    suspend fun updateTestPaper(testPaper: TestPaper)
}

@Dao
interface UserStatsDao {
    @Query("SELECT * FROM user_stats WHERE id = 1")
    fun getUserStats(): Flow<UserStats?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateStats(stats: UserStats)
}
