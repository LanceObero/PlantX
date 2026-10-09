package com.example.plantx.features

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import com.example.plantx.R
import com.example.plantx.api.PlantHistoryItem
import com.example.plantx.api.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class HistoryActivity : AppCompatActivity() {

    private lateinit var cardEmptyHistory: CardView
    private lateinit var progressHistory: ProgressBar
    private lateinit var tvHistoryError: TextView
    private lateinit var scrollHistory: ScrollView
    private lateinit var historyContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        cardEmptyHistory = findViewById(R.id.cardEmptyHistory)
        progressHistory = findViewById(R.id.progressHistory)
        tvHistoryError = findViewById(R.id.tvHistoryError)
        scrollHistory = findViewById(R.id.scrollHistory)
        historyContainer = findViewById(R.id.historyContainer)

        findViewById<TextView>(R.id.tvBack).setOnClickListener {
            finish()
        }

        findViewById<TextView>(R.id.tvIdentifyPlant).setOnClickListener {
            startActivity(
                Intent(this, IdentifyActivity::class.java)
            )
        }
    }

    override fun onResume() {
        super.onResume()

        if (::historyContainer.isInitialized && !isFinishing) {
            loadHistory()
        }
    }

    private fun loadHistory() {
        progressHistory.visibility = View.VISIBLE
        cardEmptyHistory.visibility = View.GONE
        scrollHistory.visibility = View.GONE
        tvHistoryError.visibility = View.GONE

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.plantApi.getPlantHistory()

                progressHistory.visibility = View.GONE

                if (response.isSuccessful) {
                    val records = response.body()?.history.orEmpty()

                    if (records.isEmpty()) {
                        cardEmptyHistory.visibility = View.VISIBLE
                    } else {
                        historyContainer.removeAllViews()

                        records.forEach { record ->
                            addHistoryCard(record)
                        }

                        scrollHistory.visibility = View.VISIBLE
                    }
                } else {
                    tvHistoryError.text = when (response.code()) {
                        401, 403 ->
                            "Your session may have expired. Please log in again."

                        else ->
                            "Unable to load history (HTTP ${response.code()}). Please try again."
                    }

                    tvHistoryError.visibility = View.VISIBLE
                }
            } catch (e: Exception) {
                progressHistory.visibility = View.GONE
                tvHistoryError.text =
                    "Could not connect to the server. Check your connection and try again."
                tvHistoryError.visibility = View.VISIBLE
            }
        }
    }

    private fun addHistoryCard(record: PlantHistoryItem) {
        val card = CardView(this).apply {
            radius = dp(18).toFloat()
            cardElevation = dp(3).toFloat()
            setCardBackgroundColor(Color.WHITE)

            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dp(16)
            }
        }

        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(16), dp(16), dp(16))
        }

        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        val plantImage = ImageView(this).apply {
            scaleType = ImageView.ScaleType.CENTER_CROP
            background = roundedBackground("#E8F5E9", 12)
            clipToOutline = true
            contentDescription = "${record.commonName} image"
        }

        row.addView(
            plantImage,
            LinearLayout.LayoutParams(dp(100), dp(100))
        )

        val details = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(14), 0, 0, 0)
        }

        details.addView(
            makeText(
                record.commonName.ifBlank { "Unknown plant" },
                18,
                "#26332A",
                true
            )
        )

        details.addView(
            makeText(
                record.scientificName.ifBlank {
                    "Scientific name unavailable"
                },
                14,
                "#66736A",
                false
            ).apply {
                setPadding(0, dp(5), 0, 0)
            }
        )

        details.addView(
            makeText(
                "Confidence: ${record.confidence.ifBlank { "Unknown" }}",
                13,
                "#2E7D32",
                true
            ).apply {
                setPadding(0, dp(8), 0, 0)
            }
        )

        row.addView(
            details,
            LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        )

        content.addView(row)

        if (!record.description.isNullOrBlank()) {
            content.addView(
                makeText(
                    record.description,
                    14,
                    "#505A52",
                    false
                ).apply {
                    setPadding(0, dp(14), 0, 0)
                }
            )
        }

        content.addView(
            makeText(
                formatDate(record.identifiedAt),
                12,
                "#7A827C",
                false
            ).apply {
                setPadding(0, dp(12), 0, 0)
            }
        )

        card.addView(content)
        historyContainer.addView(card)

        val imageUrl = record.imageUrl

        if (!imageUrl.isNullOrBlank()) {
            lifecycleScope.launch {
                val bitmap = withContext(Dispatchers.IO) {
                    try {
                        URL(imageUrl).openConnection().apply {
                            connectTimeout = 10000
                            readTimeout = 10000
                        }.getInputStream().use { stream ->
                            BitmapFactory.decodeStream(stream)
                        }
                    } catch (e: Exception) {
                        null
                    }
                }

                if (bitmap != null && !isFinishing) {
                    plantImage.setImageBitmap(bitmap)
                }
            }
        }
    }

    private fun makeText(
        value: String,
        size: Int,
        color: String,
        bold: Boolean
    ): TextView {
        return TextView(this).apply {
            text = value
            textSize = size.toFloat()
            setTextColor(Color.parseColor(color))

            if (bold) {
                setTypeface(null, Typeface.BOLD)
            }
        }
    }

    private fun roundedBackground(
        color: String,
        radius: Int
    ): GradientDrawable {
        return GradientDrawable().apply {
            setColor(Color.parseColor(color))
            cornerRadius = dp(radius).toFloat()
        }
    }

    private fun formatDate(value: String): String {
        return try {
            val inputFormats = listOf(
                "yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX",
                "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
                "yyyy-MM-dd'T'HH:mm:ssXXX"
            )

            var parsedDate: java.util.Date? = null

            for (pattern in inputFormats) {
                try {
                    val parser = SimpleDateFormat(
                        pattern,
                        Locale.US
                    ).apply {
                        isLenient = false
                        timeZone = TimeZone.getTimeZone("UTC")
                    }

                    parsedDate = parser.parse(value)
                    if (parsedDate != null) break
                } catch (_: Exception) {
                    continue
                }
            }

            if (parsedDate == null) {
                return value
            }

            SimpleDateFormat(
                "MMM dd, yyyy • hh:mm a",
                Locale.getDefault()
            ).apply {
                timeZone = TimeZone.getDefault()
            }.format(parsedDate)

        } catch (e: Exception) {
            value
        }
    }

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }
}

