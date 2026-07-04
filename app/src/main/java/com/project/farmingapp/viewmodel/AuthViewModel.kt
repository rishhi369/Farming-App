package com.project.farmingapp.viewmodel

import android.content.Intent
import android.util.Log
import android.view.View
import android.util.Patterns
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.project.farmingapp.model.AuthRepository

class AuthViewModel : ViewModel() {

    var name: String? = null
    var mobNo: String? = null
    var email: String? = null
    var city: String? = null
    var password: String? = null
    var confPassword: String? = null
    var userType: String? = "normal"
    var authListener: AuthListener? = null

    //login
    var loginmail:String?=null
    var loginpwd:String?=null

    lateinit var authRepository: AuthRepository
    lateinit var googleSignInClient: GoogleSignInClient

    companion object {
        private const val TAG = "GoogleActivity"
        private const val RC_SIGN_IN = 9001
    }

    val userPosts = arrayListOf<String>()
    fun signupButtonClicked(view: View) {
        authListener!!.onStarted()
        val cleanName = name?.trim()
        val cleanMobile = mobNo?.trim()
        val cleanEmail = email?.trim()
        val cleanCity = city?.trim()
        val cleanPassword = password?.trim()
        val cleanConfirmPassword = confPassword?.trim()

        if (cleanName.isNullOrEmpty() ||
            cleanMobile.isNullOrEmpty() ||
            cleanEmail.isNullOrEmpty() ||
            cleanCity.isNullOrEmpty() ||
            cleanPassword.isNullOrEmpty() ||
            cleanConfirmPassword.isNullOrEmpty()
        ) {
            authListener!!.onFailure("Please fill all required fields.")
            return
        }

        if (cleanMobile.length != 10) {
            authListener!!.onFailure("Mobile number must be 10 digits.")
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(cleanEmail).matches()) {
            authListener!!.onFailure("Please enter a valid email address.")
            return
        }

        if (cleanPassword != cleanConfirmPassword) {
            authListener!!.onFailure("Password and Confirm Password do not match.")
            return
        }

        if (cleanPassword.length < 6) {
            authListener!!.onFailure("Password must be at least 6 characters.")
            return
        }

        var data = hashMapOf(
            "name" to cleanName,
            "mobNo" to cleanMobile,
            "email" to cleanEmail,
            "city" to cleanCity,
            "userType" to userType,
            "posts" to  userPosts,
            "profileImage" to ""
        )
        val authRepo = AuthRepository().signInWithEmail(cleanEmail, cleanPassword, data)
        authListener?.onSuccess(authRepo)
    }

    fun returnActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        authListener!!.onStarted()

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val exception = task.exception
            if (task.isSuccessful) {
                try {
                    val account = task.getResult(ApiException::class.java)!!
                    var data2 = hashMapOf(
                        "userType" to userType,
                        "posts" to userPosts,
                        "name" to account.displayName.toString(),
                        "profileImage" to account.photoUrl.toString()
                    )
                    authRepository = AuthRepository()
                    var returned = authRepository.signInToGoogle(
                        account.idToken!!,
                        account.email.toString(),
                        data2
                    )
                    Log.d("AuthView", returned.value.toString())
                    authListener?.onSuccess(returned)
                } catch (e: ApiException) {
                    authListener!!.onFailure(e.message.toString())
                }
            } else {
            }
        }
    }

    //login btn function
    fun loginButtonClicked(view: View) {
        authListener!!.onStarted()
        if (loginmail.isNullOrEmpty() || loginpwd.isNullOrEmpty()) {
            authListener!!.onFailure("Please enter email and password.")
            return
        }
        // Success

        val authRepo = AuthRepository().logInWithEmail(loginmail!!, loginpwd!!)
        authListener?.onSuccess(authRepo)
    }
}