package com.standford.ligthhouse.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.github.ybq.android.spinkit.sprite.Sprite
import com.github.ybq.android.spinkit.style.DoubleBounce
import com.standford.ligthhouse.R


class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val progressBar = findViewById<View>(R.id.spin_kit) as ProgressBar
        val doubleBounce: Sprite = DoubleBounce()
        progressBar.indeterminateDrawable = doubleBounce

        Handler().postDelayed({ nextActivity() }, 5000.toLong())
    }

    private fun nextActivity() {
        finish()
        val intent = Intent(this, DashboardActivity::class.java)
        startActivity(intent)
    }
}