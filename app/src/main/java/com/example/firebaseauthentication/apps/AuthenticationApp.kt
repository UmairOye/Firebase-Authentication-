package com.example.firebaseauthentication.apps

import android.app.Application
import com.google.firebase.FirebaseApp

class AuthenticationApp: Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}