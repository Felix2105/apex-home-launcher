package com.apex.launcher

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView

class MainActivity : Activity() {
    private val navy = Color.rgb(10, 18, 35)
    private val gold = Color.rgb(222, 177, 75)
    private val white = Color.rgb(245, 245, 245)

    private fun text(value: String, size: Float, color: Int) = TextView(this).apply {
        text = value
        textSize = size
        setTextColor(color)
        gravity = Gravity.CENTER
    }

    private fun button(label: String, action: () -> Unit) = Button(this).apply {
        text = label
        textSize = 16f
        setTextColor(gold)
        setBackgroundColor(Color.rgb(27, 39, 60))
        isAllCaps = false
        setOnClickListener { action() }
    }

    private fun openWeb(url: String) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }

    private fun launchApps() {
        val apps = packageManager.getInstalledApplications(0)
            .filter { packageManager.getLaunchIntentForPackage(it.packageName) != null }
            .sortedBy { packageManager.getApplicationLabel(it).toString().lowercase() }

        val labels = apps.map {
            packageManager.getApplicationLabel(it).toString()
        }.toTypedArray()

        AlertDialog.Builder(this)
            .setTitle("APEX App Drawer")
            .setItems(labels) { _, which ->
                packageManager.getLaunchIntentForPackage(apps[which].packageName)
                    ?.let { startActivity(it) }
            }
            .setNegativeButton("Close", null)
            .show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = navy
        window.navigationBarColor = navy
        window.decorView.setBackgroundColor(navy)

        val page = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(24, 30, 24, 24)
            setBackgroundColor(navy)
        }

        page.addView(text("A P E X", 34f, gold))
        page.addView(text("EXECUTIVE COMMAND CENTRE", 13f, white))
        page.addView(text("Precision • Focus • Control", 14f, gold))

        val scroll = ScrollView(this)
        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(0, 24, 0, 10)
        }

        content.addView(text("YOUR DIGITAL WORKSPACE", 14f, gold))
        content.addView(button("Open APEX Dashboard") {
            openWeb("http://127.0.0.1:8080/")
        }, match())
        content.addView(button("Open Business HQ") {
            openWeb("http://127.0.0.1:8080/business-hq.html")
        }, match())
        content.addView(button("Open App Drawer") {
            launchApps()
        }, match())
        content.addView(button("Android Home Settings") {
            startActivity(Intent("android.settings.HOME_SETTINGS"))
        }, match())
        content.addView(button("Family & Personal") {
            AlertDialog.Builder(this)
                .setTitle("Family comes first")
                .setMessage("Make time for Lynnie and the children. Keep business out of family time.")
                .setPositiveButton("Close", null)
                .show()
        }, match())
        content.addView(text(
            "APEX LINUX MOBILE\nDashboard: 127.0.0.1:8080",
            12f, white
        ).apply { setPadding(0, 22, 0, 12) })

        scroll.addView(content)
        page.addView(scroll, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f
        ))
        setContentView(page)
    }

    private fun match() = LinearLayout.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    ).apply {
        setMargins(0, 7, 0, 7)
    }
}
