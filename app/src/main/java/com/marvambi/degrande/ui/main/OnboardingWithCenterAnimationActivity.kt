package com.marvambi.degrande.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.DecelerateInterpolator
import androidx.core.view.ViewPropertyAnimatorCompat
import android.view.ViewGroup
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.core.view.ViewCompat
import kotlinx.android.synthetic.main.activity_onboarding_with_center_animation.*


class OnboardingWithCenterAnimationActivity : AppCompatActivity() {

    private val animationStarted = false

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        setTheme(com.marvambi.degrande.R.style.AppTheme)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        super.onCreate(savedInstanceState)
        setContentView(com.marvambi.degrande.R.layout.activity_onboarding_with_center_animation)

        btn_guest.setOnClickListener {
            Toast.makeText(this, "Login in as Guest...", Toast.LENGTH_LONG).show()
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("title", "Guest")
            startActivity(intent)
        }

        btn_staff.setOnClickListener {
            Toast.makeText(this, "Login in as Staff...", Toast.LENGTH_LONG).show()
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("title", "Staff")
            startActivity(intent)
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {

        if (!hasFocus || animationStarted) {
            return
        }

        animate()

        super.onWindowFocusChanged(hasFocus)
    }

    private fun animate() {
        val logoImageView = findViewById(com.marvambi.degrande.R.id.img_logo) as ImageView
        val container = findViewById(com.marvambi.degrande.R.id.container) as ViewGroup

        ViewCompat.animate(logoImageView)
                .translationY((-250).toFloat())
                .setStartDelay(STARTUP_DELAY.toLong())
                .setDuration(ANIM_ITEM_DURATION.toLong()).setInterpolator(
                        DecelerateInterpolator(1.2f)).start()

        for (i in 0 until container.childCount) {
            val v = container.getChildAt(i)
            val viewAnimator: ViewPropertyAnimatorCompat

            if (v !is Button) {
                viewAnimator = ViewCompat.animate(v)
                        .translationY(50F).alpha(1F)
                        .setStartDelay((ITEM_DELAY * i + 500).toLong())
                        .setDuration(1000)
            } else {
                viewAnimator = ViewCompat.animate(v)
                        .scaleY(1F).scaleX(1F)
                        .setStartDelay((ITEM_DELAY * i + 500).toLong())
                        .setDuration(500)
            }

            viewAnimator.setInterpolator(DecelerateInterpolator()).start()
        }
    }

    companion object {
        val STARTUP_DELAY = 300
        val ANIM_ITEM_DURATION = 1000
        val ITEM_DELAY = 300
    }
}
