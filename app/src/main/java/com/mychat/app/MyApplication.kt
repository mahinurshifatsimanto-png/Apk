package com.mychat.app

import android.app.Application
import com.google.firebase.database.FirebaseDatabase

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Enable disk persistence for offline support
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }
}
