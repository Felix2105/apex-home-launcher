package com.apex.launcher

import android.app.Activity
import android.app.AlertDialog
import android.app.SearchManager
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.content.Context
import android.widget.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs

class MainActivity : Activity() {
    private val gold = Color.rgb(224, 190, 105)
    private val bg = Color.rgb(7, 12, 22)
    private val panel = Color.rgb(18, 27, 43)
    private val white = Color.rgb(245, 246, 250)
    private lateinit var root: LinearLayout
    private lateinit var body: LinearLayout
    private lateinit var search: EditText
    private lateinit var pageLabel: TextView
    private var page = 0
    private var drawerOpen = false
    private var touchX = 0f
    private val pageNames = listOf("COMMAND", "BUSINESS HQ", "AI COMMAND", "DOCUMENTS", "COMMUNICATIONS", "HEALTH & FOCUS", "PERSONAL")
    private val pm by lazy { packageManager }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = bg
        window.navigationBarColor = bg
        window.decorView.systemUiVisibility = 0
        render()
    }

    private fun render() {
        root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(18), dp(12), dp(18), dp(12))
            setBackgroundColor(bg)
        }
        val top = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        val brand = TextView(this).apply {
            text = "APEX"
            textSize = 27f
            setTextColor(gold)
            typeface = Typeface.create("sans-serif", Typeface.BOLD)
            letterSpacing = .18f
        }
        top.addView(brand, LinearLayout.LayoutParams(0, -2, 1f))
        val menu = TextView(this).apply {
            text = "☰"
            textSize = 25f
            setTextColor(white)
            gravity = Gravity.CENTER
            setPadding(dp(12), dp(4), dp(4), dp(4))
            setOnClickListener { showMenu() }
        }
        top.addView(menu)
        root.addView(top)

        val subtitle = TextView(this).apply {
            text = "CEO COMMAND CENTRE"
            textSize = 11f
            letterSpacing = .18f
            setTextColor(Color.LTGRAY)
            setPadding(0, dp(2), 0, dp(10))
        }
        root.addView(subtitle)

        val clock = TextView(this).apply {
            text = SimpleDateFormat("EEEE, d MMMM  •  HH:mm", Locale.getDefault()).format(Date())
            textSize = 14f
            setTextColor(white)
            setPadding(dp(14), dp(12), dp(14), dp(12))
            background = rounded(panel, 14, gold)
        }
        root.addView(clock, LinearLayout.LayoutParams(-1, -2).apply { bottomMargin = dp(12) })

        search = EditText(this).apply {
            hint = "Search apps on this phone…"
            setHintTextColor(Color.LTGRAY)
            setTextColor(white)
            textSize = 15f
            setSingleLine(true)
            setPadding(dp(14), dp(8), dp(14), dp(8))
            background = rounded(panel, 12, Color.rgb(52, 65, 84))
            addTextChangedListener(SimpleWatcher { if (drawerOpen) showApps(it) })
            setOnFocusChangeListener { _, hasFocus -> if (hasFocus && !drawerOpen) openDrawer() }
        }
        root.addView(search, LinearLayout.LayoutParams(-1, dp(48)).apply { bottomMargin = dp(12) })

        pageLabel = TextView(this).apply {
            text = pageNames[page]
            textSize = 12f
            letterSpacing = .12f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(gold)
            setPadding(dp(2), dp(4), dp(2), dp(8))
        }
        root.addView(pageLabel)

        body = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        root.addView(body, LinearLayout.LayoutParams(-1, 0, 1f))

        val bottom = LinearLayout(this).apply {
            gravity = Gravity.CENTER
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, dp(8), 0, 0)
        }
        listOf("‹", "HOME", "APPS", "›").forEachIndexed { i, label ->
            val b = TextView(this).apply {
                text = label
                textSize = if (i == 1 || i == 2) 12f else 24f
                typeface = Typeface.DEFAULT_BOLD
                gravity = Gravity.CENTER
                setTextColor(if (i == 2 && drawerOpen) gold else white)
                background = rounded(panel, 12, Color.rgb(47, 59, 78))
                setOnClickListener {
                    when (i) {
                        0 -> changePage(-1)
                        1 -> { drawerOpen = false; page = 0; search.setText(""); renderContent() }
                        2 -> openDrawer()
                        3 -> changePage(1)
                    }
                }
            }
            val lp = LinearLayout.LayoutParams(0, dp(46), if (i == 1 || i == 2) 1.4f else 1f)
            lp.setMargins(dp(4), 0, dp(4), 0)
            bottom.addView(b, lp)
        }
        root.addView(bottom)
        setContentView(root)
        renderContent()
        root.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) touchX = event.x
            if (event.action == MotionEvent.ACTION_UP) {
                val dx = event.x - touchX
                if (abs(dx) > dp(80)) changePage(if (dx < 0) 1 else -1)
            }
            true
        }
    }

    private fun renderContent() {
        body.removeAllViews()
        pageLabel.text = if (drawerOpen) "APP DRAWER" else pageNames[page]
        if (drawerOpen) {
            showApps(search.text?.toString().orEmpty())
            return
        }
        when (page) {
            0 -> {
                addSection("Quick launch")
                addTileGrid(listOf(
                    Tile("PHONE", "Call and contacts") { launchIntent(Intent(Intent.ACTION_DIAL)) },
                    Tile("WHATSAPP", "Open WhatsApp") { launchPackage("com.whatsapp") },
                    Tile("BUSINESS HQ", "Clients, jobs and quotes") { openWeb("http://127.0.0.1:8766/") },
                    Tile("LINUX DESKTOP", "APEX Linux dashboard") { openWeb("http://127.0.0.1:8080/") },
                    Tile("AI COMMAND", "AI tools and assistants") { changePage(2) },
                    Tile("DOCUMENTS", "Files and cloud storage") { changePage(3) }
                ))
                addSection("Your workspace")
                addButton("Open all installed apps", gold) { openDrawer() }
                addButton("Choose default home app", panel) {
                    try { startActivity(Intent(Settings.ACTION_HOME_SETTINGS)) }
                    catch (_: Exception) { startActivity(Intent(Settings.ACTION_SETTINGS)) }
                }
                addNote("Swipe left or right to move between seven workspaces. Your existing APEX Linux services are not changed by this launcher.")
            }
            1 -> {
                addSection("Business HQ")
                addTileGrid(listOf(
                    Tile("APEX BUSINESS", "Local business dashboard") { openWeb("http://127.0.0.1:8766/") },
                    Tile("LINUX STATUS", "APEX Linux Mobile") { openWeb("http://127.0.0.1:8080/") },
                    Tile("GMAIL", "Email and enquiries") { launchPackage("com.google.android.gm") },
                    Tile("CALENDAR", "Schedule and meetings") { launchPackage("com.google.android.calendar") }
                ))
            }
            2 -> {
                addSection("AI Command")
                addTileGrid(listOf(
                    Tile("CHATGPT", "AI assistant") { launchPackage("com.openai.chatgpt", "https://chatgpt.com") },
                    Tile("GEMINI", "Google AI assistant") { launchPackage("com.google.android.apps.bard", "https://gemini.google.com") },
                    Tile("BROWSER", "Search and research") { launchIntent(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com"))) }
                ))
            }
            3 -> {
                addSection("Document Centre")
                addTileGrid(listOf(
                    Tile("FILES", "Phone files and downloads") { launchIntent(Intent(Intent.ACTION_GET_CONTENT).setType("*/*")) },
                    Tile("DRIVE", "Cloud documents") { launchPackage("com.google.android.apps.docs") },
                    Tile("DOCS", "Documents and proposals") { launchPackage("com.google.android.apps.docs.editors.docs") }
                ))
            }
            4 -> {
                addSection("Communications")
                addTileGrid(listOf(
                    Tile("WHATSAPP", "Messages and calls") { launchPackage("com.whatsapp") },
                    Tile("MESSAGES", "SMS messages") { launchIntent(Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_APP_MESSAGING)) },
                    Tile("PHONE", "Calls") { launchIntent(Intent(Intent.ACTION_DIAL)) },
                    Tile("GMAIL", "Email") { launchPackage("com.google.android.gm") }
                ))
            }
            5 -> {
                addSection("Health & Focus")
                addTileGrid(listOf(
                    Tile("CLOCK", "Alarms and timers") { launchIntent(Intent(android.provider.AlarmClock.ACTION_SHOW_ALARMS)) },
                    Tile("MUSIC", "Music player") { launchIntent(Intent(Intent.ACTION_VIEW, Uri.parse("https://open.spotify.com"))) },
                    Tile("SETTINGS", "Device settings") { launchIntent(Intent(Settings.ACTION_SETTINGS)) }
                ))
            }
            else -> {
                addSection("Personal & Social")
                addTileGrid(listOf(
                    Tile("WHATSAPP", "Family and friends") { launchPackage("com.whatsapp") },
                    Tile("GALLERY", "Photos and memories") { launchIntent(Intent(Intent.ACTION_VIEW).setType("image/*")) },
                    Tile("CONTACTS", "People") { launchIntent(Intent(Intent.ACTION_VIEW, Uri.parse("content://contacts/people/"))) }
                ))
            }
        }
        addSpacer()
        val pageDots = TextView(this).apply {
            text = "●  ○  ○  ○  ○  ○  ○".split(" ").mapIndexed { i, s -> if (i == page) "●" else "○" }.joinToString("   ")
            gravity = Gravity.CENTER
            textSize = 12f
            setTextColor(gold)
            setPadding(0, dp(8), 0, dp(4))
        }
        body.addView(pageDots)
    }

    private data class Tile(val title: String, val detail: String, val action: () -> Unit)

    private fun addTileGrid(tiles: List<Tile>) {
        val grid = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        tiles.chunked(2).forEach { pair ->
            val row = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
            pair.forEach { tile ->
                val card = LinearLayout(this).apply {
                    orientation = LinearLayout.VERTICAL
                    gravity = Gravity.CENTER_VERTICAL
                    setPadding(dp(13), dp(13), dp(10), dp(13))
                    background = rounded(panel, 14, Color.rgb(48, 60, 79))
                    isClickable = true
                    isFocusable = true
                    setOnClickListener { tile.action() }
                }
                val title = TextView(this).apply {
                    text = tile.title
                    textSize = 13f
                    typeface = Typeface.DEFAULT_BOLD
                    setTextColor(gold)
                }
                val detail = TextView(this).apply {
                    text = tile.detail
                    textSize = 11f
                    setTextColor(Color.LTGRAY)
                    setPadding(0, dp(5), 0, 0)
                }
                card.addView(title)
                card.addView(detail)
                val lp = LinearLayout.LayoutParams(0, dp(92), 1f)
                lp.setMargins(dp(4), dp(4), dp(4), dp(4))
                row.addView(card, lp)
            }
            if (pair.size == 1) row.addView(View(this), LinearLayout.LayoutParams(0, dp(92), 1f))
            grid.addView(row)
        }
        body.addView(grid)
    }

    private fun addSection(text: String) {
        body.addView(TextView(this).apply {
            this.text = text
            textSize = 17f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(white)
            setPadding(0, dp(4), 0, dp(8))
        })
    }

    private fun addButton(text: String, color: Int, action: () -> Unit) {
        val b = TextView(this).apply {
            this.text = text
            textSize = 14f
            gravity = Gravity.CENTER
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(if (color == gold) bg else white)
            background = rounded(color, 12, gold)
            setPadding(dp(12), dp(13), dp(12), dp(13))
            setOnClickListener { action() }
        }
        body.addView(b, LinearLayout.LayoutParams(-1, -2).apply {
            setMargins(dp(4), dp(4), dp(4), dp(4))
        })
    }

    private fun addNote(text: String) {
        body.addView(TextView(this).apply {
            this.text = text
            textSize = 11f
            setTextColor(Color.LTGRAY)
            setPadding(dp(8), dp(12), dp(8), dp(8))
        })
    }

    private fun addSpacer() { body.addView(View(this), LinearLayout.LayoutParams(1, 0, 1f)) }

    private fun openDrawer() {
        drawerOpen = true
        search.setText("")
        search.clearFocus()
        (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
            .hideSoftInputFromWindow(search.windowToken, 0)
        renderContent()
    }

    private fun showApps(query: String) {
        if (!::body.isInitialized || !drawerOpen) return
        body.removeAllViews()
        val scroller = ScrollView(this)
        val list = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        val apps = try {
            pm.queryIntentActivities(Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER), 0)
                .mapNotNull { it.activityInfo }
                .distinctBy { it.packageName }
                .filter { it.loadLabel(pm).toString().contains(query, true) }
                .sortedBy { it.loadLabel(pm).toString().lowercase(Locale.getDefault()) }
        } catch (_: Exception) { emptyList() }
        if (apps.isEmpty()) {
            list.addView(TextView(this).apply {
                text = "No matching apps found."
                setTextColor(white)
                setPadding(dp(10), dp(20), dp(10), dp(20))
            })
        }
        apps.forEach { info ->
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                setPadding(dp(10), dp(8), dp(10), dp(8))
                background = rounded(panel, 10, Color.rgb(40, 51, 69))
                isClickable = true
                setOnClickListener { launchPackage(info.packageName) }
            }
            val icon = ImageView(this).apply {
                setImageDrawable(info.loadIcon(pm))
                scaleType = ImageView.ScaleType.FIT_CENTER
            }
            row.addView(icon, LinearLayout.LayoutParams(dp(38), dp(38)))
            row.addView(TextView(this).apply {
                text = info.loadLabel(pm).toString()
                textSize = 14f
                setTextColor(white)
                setPadding(dp(12), 0, 0, 0)
            }, LinearLayout.LayoutParams(0, -2, 1f))
            list.addView(row, LinearLayout.LayoutParams(-1, dp(56)).apply {
                bottomMargin = dp(5)
            })
        }
        scroller.addView(list)
        body.addView(scroller, LinearLayout.LayoutParams(-1, -1))
    }

    private fun changePage(delta: Int) {
        drawerOpen = false
        page = (page + delta + pageNames.size) % pageNames.size
        if (::search.isInitialized) {
            search.setText("")
            search.clearFocus()
        }
        renderContent()
    }

    private fun showMenu() {
        val items = arrayOf("Open app drawer", "Set APEX Home as default", "Android settings", "APEX Linux dashboard", "Business HQ")
        AlertDialog.Builder(this).setTitle("APEX Home").setItems(items) { _, which ->
            when (which) {
                0 -> openDrawer()
                1 -> try { startActivity(Intent(Settings.ACTION_HOME_SETTINGS)) } catch (_: Exception) { startActivity(Intent(Settings.ACTION_SETTINGS)) }
                2 -> launchIntent(Intent(Settings.ACTION_SETTINGS))
                3 -> openWeb("http://127.0.0.1:8080/")
                4 -> openWeb("http://127.0.0.1:8766/")
            }
        }.show()
    }

    private fun launchPackage(packageName: String, fallbackUrl: String? = null) {
        try {
            val intent = pm.getLaunchIntentForPackage(packageName)
            if (intent != null) startActivity(intent)
            else if (fallbackUrl != null) openWeb(fallbackUrl)
            else Toast.makeText(this, "App not installed. Use the app drawer to find an alternative.", Toast.LENGTH_SHORT).show()
        } catch (_: Exception) {
            if (fallbackUrl != null) openWeb(fallbackUrl)
            else Toast.makeText(this, "Could not open this app.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun launchIntent(intent: Intent) {
        try { startActivity(intent) }
        catch (_: Exception) { Toast.makeText(this, "No compatible app is available for this action.", Toast.LENGTH_SHORT).show() }
    }

    private fun openWeb(url: String) {
        launchIntent(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }

    private fun rounded(color: Int, radius: Int, stroke: Int): GradientDrawable =
        GradientDrawable().apply {
            setColor(color)
            cornerRadius = dp(radius).toFloat()
            setStroke(dp(1), stroke)
        }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()

    override fun onBackPressed() {
        if (drawerOpen) {
            drawerOpen = false
            search.setText("")
            renderContent()
        } else if (page != 0) {
            page = 0
            renderContent()
        } else {
            super.onBackPressed()
        }
    }
}

private class SimpleWatcher(val onText: (String) -> Unit) : android.text.TextWatcher {
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { onText(s?.toString().orEmpty()) }
    override fun afterTextChanged(s: android.text.Editable?) {}
}
