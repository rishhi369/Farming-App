package com.project.farmingapp.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.project.farmingapp.R
import com.project.farmingapp.databinding.ActivitySplashBinding
import com.project.farmingapp.view.auth.LoginActivity
import com.project.farmingapp.view.dashboard.DashboardActivity
import com.project.farmingapp.view.introscreen.IntroActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Add subtle animations
        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_transition)
        binding.splashLogo.startAnimation(fadeIn)
        binding.splashText.startAnimation(fadeIn)
        binding.splashSubtitle.startAnimation(fadeIn)

        Handler(Looper.getMainLooper()).postDelayed({
            checkAuthAndNavigate()
        }, 2000)
    }

    private fun checkAuthAndNavigate() {
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val firstTime = sharedPreferences.getBoolean("firstTime", true)
        val currentUser = FirebaseAuth.getInstance().currentUser

        when {
            firstTime -> {
                startActivity(Intent(this, IntroActivity::class.java))
            }
            currentUser == null -> {
                startActivity(Intent(this, LoginActivity::class.java))
            }
            else -> {
                startActivity(Intent(this, DashboardActivity::class.java))
            }
        }
        finish()
    }
}
