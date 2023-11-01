package com.example.firebaseauthentication.data.remote

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FirebaseViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    suspend fun registerUser(
        name: String,
        email: String,
        phone: String,
        password: String
    ): MutableLiveData<Boolean> = withContext(Dispatchers.IO) {
        val myLiveData = MutableLiveData<Boolean>()

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    viewModelScope.launch {
                        myLiveData.value = true
                    }
                } else {
                    Log.d("helloAmException", "registerUser: ${task.exception!!.message}")
                    myLiveData.value = false
                }
            }
        return@withContext myLiveData
    }


    suspend fun loginUser(
        email: String,
        password: String
    ): MutableLiveData<Boolean> = withContext(Dispatchers.IO) {
        val myLiveData = MutableLiveData<Boolean>()

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    viewModelScope.launch {
                        myLiveData.value = true
                    }
                } else {
                    Log.d("helloAmException", "registerUser: ${task.exception!!.message}")
                    myLiveData.value = false
                }
            }
        return@withContext myLiveData
    }


    fun signInWithGoogle(token: String, function: () -> Unit) {
        val credential = GoogleAuthProvider.getCredential(token, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    function.invoke()
                    Log.d("TAG", "signInWithGoogle: isSuccessful")
                } else {
                    Log.d("TAG", "signInWithGoogle: ${task.exception!!.message}")
                }
            }
    }

    fun sendPasswordResetEmail(email: String) = auth.sendPasswordResetEmail(email)


}