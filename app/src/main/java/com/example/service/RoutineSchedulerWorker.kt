package com.example.service

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.data.CollegeNotesDatabase
import kotlinx.coroutines.flow.firstOrNull
import java.text.SimpleDateFormat
import java.util.*

class RoutineSchedulerWorker(
    private val appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val db = CollegeNotesDatabase.getDatabase(appContext)
        val routineDao = db.routineDao()

        val calendar = Calendar.getInstance()
        val currentDay = SimpleDateFormat("EEEE", Locale.getDefault()).format(calendar.time)
        val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(calendar.time)

        val todayRoutines = routineDao.getRoutinesForDay(currentDay).firstOrNull() ?: emptyList()

        todayRoutines.forEach { routine ->
            if (routine.isAutoRecordEnabled) {
                if (routine.startTime == currentTime) {
                    AutoRecordForegroundService.startAutoRecord(appContext, routine.subject)
                } else if (routine.endTime == currentTime) {
                    AutoRecordForegroundService.stopAutoRecord(appContext)
                }
            }
        }

        return Result.success()
    }
}
