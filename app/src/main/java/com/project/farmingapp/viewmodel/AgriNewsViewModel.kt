package com.project.farmingapp.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.farmingapp.model.data.AgriNewsItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Locale

class AgriNewsViewModel : ViewModel() {

    val newsItems = MutableLiveData<List<AgriNewsItem>>()
    val newsStatus = MutableLiveData<String>()
    private var hasLoaded = false

    fun loadAgriNews(forceRefresh: Boolean = false) {
        if (hasLoaded && !forceRefresh && !newsItems.value.isNullOrEmpty()) return

        newsStatus.value = "Loading agriculture news..."

        viewModelScope.launch {
            val items = withContext(Dispatchers.IO) {
                try {
                    fetchGoogleNewsRss()
                } catch (e: Exception) {
                    Log.d("AgriNewsViewModel", e.message ?: "Failed to load agri news")
                    emptyList()
                }
            }

            if (items.isNullOrEmpty()) {
                newsItems.value = fallbackNews()
                newsStatus.value = "Showing saved agriculture headlines"
            } else {
                newsItems.value = items
                newsStatus.value = ""
            }

            hasLoaded = true
        }
    }

    private fun fetchGoogleNewsRss(): List<AgriNewsItem> {
        val feedUrl =
            "https://news.google.com/rss/search?q=agriculture%20OR%20farming%20OR%20farmer%20OR%20crop%20India&hl=en-IN&gl=IN&ceid=IN:en"
        val connection = URL(feedUrl).openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.connectTimeout = 10000
        connection.readTimeout = 10000
        connection.connect()

        return connection.inputStream.use { inputStream ->
            parseRssFeed(inputStream)
        }
    }

    private fun parseRssFeed(inputStream: InputStream): List<AgriNewsItem> {
        val parserFactory = XmlPullParserFactory.newInstance()
        val parser = parserFactory.newPullParser()
        parser.setInput(inputStream, null)

        val items = mutableListOf<AgriNewsItem>()
        var eventType = parser.eventType
        var insideItem = false
        var title = ""
        var link = ""
        var publishedAt = ""

        while (eventType != XmlPullParser.END_DOCUMENT && items.size < 8) {
            when (eventType) {
                XmlPullParser.START_TAG -> {
                    when (parser.name) {
                        "item" -> {
                            insideItem = true
                            title = ""
                            link = ""
                            publishedAt = ""
                        }
                        "title" -> if (insideItem) {
                            title = parser.nextText().trim()
                        }
                        "link" -> if (insideItem) {
                            link = parser.nextText().trim()
                        }
                        "pubDate" -> if (insideItem) {
                            publishedAt = formatDate(parser.nextText().trim())
                        }
                    }
                }

                XmlPullParser.END_TAG -> {
                    if (parser.name == "item" && insideItem && title.isNotBlank() && link.isNotBlank()) {
                        val source = extractSource(title)
                        val cleanTitle = extractHeadline(title)
                        items.add(
                            AgriNewsItem(
                                title = cleanTitle,
                                source = source,
                                link = link,
                                publishedAt = publishedAt.ifBlank { "Latest" }
                            )
                        )
                        insideItem = false
                    }
                }
            }
            eventType = parser.next()
        }

        return items
    }

    private fun extractHeadline(fullTitle: String): String {
        val lastDash = fullTitle.lastIndexOf(" - ")
        return if (lastDash > 0) fullTitle.substring(0, lastDash).trim() else fullTitle
    }

    private fun extractSource(fullTitle: String): String {
        val lastDash = fullTitle.lastIndexOf(" - ")
        return if (lastDash > 0) fullTitle.substring(lastDash + 3).trim() else "Google News"
    }

    private fun formatDate(rawDate: String): String {
        return try {
            val inputFormat = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US)
            val outputFormat = SimpleDateFormat("dd MMM", Locale.getDefault())
            val parsedDate = inputFormat.parse(rawDate)
            if (parsedDate != null) outputFormat.format(parsedDate) else rawDate
        } catch (e: Exception) {
            rawDate
        }
    }

    private fun fallbackNews(): List<AgriNewsItem> {
        return listOf(
            AgriNewsItem(
                title = "Weather-driven crop planning is becoming more important for Indian farmers",
                source = "Agri India",
                link = "https://news.google.com/search?q=weather%20crop%20planning%20india",
                publishedAt = "Saved"
            ),
            AgriNewsItem(
                title = "Market price updates help farmers decide the right mandi to sell produce",
                source = "Agri India",
                link = "https://news.google.com/search?q=mandi%20market%20price%20farmers%20india",
                publishedAt = "Saved"
            ),
            AgriNewsItem(
                title = "Drip irrigation and micronutrient management remain key topics for vineyards",
                source = "Agri India",
                link = "https://news.google.com/search?q=grape%20farming%20india%20drip%20irrigation",
                publishedAt = "Saved"
            ),
            AgriNewsItem(
                title = "Government schemes and digital advisories continue to support farm decisions",
                source = "Agri India",
                link = "https://news.google.com/search?q=farmer%20scheme%20india%20agriculture",
                publishedAt = "Saved"
            )
        )
    }
}
