package com.udacity.project4.authentication

import android.app.Application
import androidx.lifecycle.map
import com.udacity.project4.authentication.firebase.FirebaseUserLiveData
import com.udacity.project4.base.BaseViewModel

class AuthenticationViewModel(app: Application) : BaseViewModel(app) {

    enum class AuthenticationState {
        AUTHENTICATED, UNAUTHENTICATED, INVALID_AUTHENTICATION
    }

    val authenticationState = FirebaseUserLiveData().map { user ->
        if (user != null) {
            AuthenticationState.AUTHENTICATED
        } else {
            AuthenticationState.UNAUTHENTICATED
        }
    }

}