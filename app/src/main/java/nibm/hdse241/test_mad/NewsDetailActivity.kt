package nibm.hdse241.test_mad

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class NewsDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_detail)


        val titleTextView: TextView = findViewById(R.id.news_detail_title)
        val contentTextView: TextView = findViewById(R.id.news_detail_content)
        val dateTextView: TextView = findViewById(R.id.news_detail_date)
        val imageView: ImageView = findViewById(R.id.news_detail_image)

        // Retrieve passed data
        val newsTitle = intent.getStringExtra("newsTitle")
        val newsContent = intent.getStringExtra("newsContent")
        val newsDate = intent.getStringExtra("newsDate")
        val newsImage = intent.getStringExtra("newsImage")

        // Log received data for debugging
        Log.d("NewsDetailActivity", "Title: $newsTitle")
        Log.d("NewsDetailActivity", "Content: $newsContent")
        Log.d("NewsDetailActivity", "Date: $newsDate")
        Log.d("NewsDetailActivity", "Image URL: $newsImage")

        // Set data in views
        titleTextView.text = newsTitle
        contentTextView.text = newsContent
        dateTextView.text = newsDate

        // Load image using Glide
        if (!newsImage.isNullOrEmpty()) {
            Glide.with(this)
                .load(newsImage)
                .error(R.drawable.reporter_logo) // Display error image if loading fails
                .into(imageView)
        } else {
            Log.e("NewsDetailActivity", "Image URL is empty or invalid.")
        }
    }
}
