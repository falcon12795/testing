package com.udacity.project4.authentication

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.firebase.ui.auth.AuthMethodPickerLayout
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.udacity.project4.R
import com.udacity.project4.databinding.ActivityAuthenticationBinding
import com.udacity.project4.locationreminders.RemindersActivity
import org.koin.androidx.viewmodel.ext.android.viewModel


/**
 * This class should be the starting point of the app, It asks the users to sign in / register, and redirects the
 * signed in users to the RemindersActivity.
 */
class AuthenticationActivity : AppCompatActivity() {

    companion object {
        const val TAG = "AuthenticationActivity"
    }

    private lateinit var binding: ActivityAuthenticationBinding
    private lateinit var mActivityResultLauncher: ActivityResultLauncher<Intent>
    private val _viewModel : AuthenticationViewModel by viewModel()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAuthenticationBinding.inflate(layoutInflater)
        mActivityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                handleActivityResult(it.resultCode, it.data)
            }

        setContentView(binding.root)
        // TODO: Implement the create account and sign in using FirebaseUI,
        //  use sign in using email and sign in using Google

        binding.btnLogin.setOnClickListener { launchSignInFlow() }

        // TODO: If the user was authenticated, send him to RemindersActivity
        observeAuthenticationState()

        // TODO: a bonus is to customize the sign in flow to look nice using :
        //https://github.com/firebase/FirebaseUI-Android/blob/master/auth/README.md#custom-layout
    }

    private fun launchSignInFlow() {

        val customLayout = AuthMethodPickerLayout.Builder(R.layout.fragment_sign_in)
            .setGoogleButtonId(R.id.btn_google)
            .setEmailButtonId(R.id.email_button) // ...
            .build()

        // Give users the option to sign in / register with their email or Google account. If users
        // choose to register with their email, they will need to create a password as well.
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(), AuthUI.IdpConfig.GoogleBuilder().build()
        )

        // Create and launch sign-in intent. We listen to the response of this activity with the
        // SIGN_IN_RESULT_CODE code.
        mActivityResultLauncher.launch(
            AuthUI.getInstance().createSignInIntentBuilder()
                .setTheme(R.style.AppTheme)
//                .setLogo(R.drawable.map)
                .setAuthMethodPickerLayout(customLayout)
                .setAvailableProviders(providers)
                .build()
        )
    }

    private fun handleActivityResult(resultCode: Int, data: Intent?) {
        val response = IdpResponse.fromResultIntent(data)
        if (resultCode == Activity.RESULT_OK) {
            // Successfully signed in user.
            Log.i(
                TAG,
                "Successfully signed in user " +
                        "${FirebaseAuth.getInstance().currentUser?.displayName}!"

            )

        } else {
            // Sign in failed. If response is null the user canceled the sign-in flow using
            // the back button. Otherwise check response.getError().getErrorCode() and handle
            // the error.
            Log.i(TAG, "Sign in unsuccessful ${response?.error?.errorCode}")
        }
    }

    @SuppressLint("ShowToast")
    private fun observeAuthenticationState() {
        _viewModel.authenticationState.observe(this, Observer { authenticationState ->
            when (authenticationState) {
                AuthenticationViewModel.AuthenticationState.AUTHENTICATED -> {
                    val intent = Intent(this, RemindersActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                }
                else -> {

                }
            }
        })
    }


}