package nibm.hdse241.test_mad

import android.os.Bundle
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

        val btn_approve2: Button = findViewById(R.id.btn_approve2)
        val btn_reject2: Button = findViewById(R.id.btn_reject2)
        val btn_update2: Button = findViewById(R.id.btn_update2)

        val type = intent.getStringExtra("Type")
        if(type == "reporter" || type == "viewer" ){
            btn_approve2.isEnabled = false
            btn_reject2.isEnabled = false
            btn_update2.isEnabled = false

            btn_approve2.visibility = View.GONE
            btn_reject2.visibility = View.GONE
            btn_update2.visibility = View.GONE

        }

        val titleTextView: TextView = findViewById(R.id.news_detail_title)
        val contentTextView: TextView = findViewById(R.id.news_detail_content)
        val dateTextView: TextView = findViewById(R.id.news_detail_date)
        val imageView: ImageView = findViewById(R.id.news_detail_image)

        // Retrieve passed data
        val newsTitle = intent.getStringExtra("newsTitle")
        val newsContent = intent.getStringExtra("newsContent")
        val newsDate = intent.getStringExtra("newsDate")
        val newsImage = intent.getStringExtra("newsImage")

        // Set data in views
        titleTextView.text = newsTitle
        contentTextView.text = newsContent
        dateTextView.text = newsDate

        // Load image using Glide
        if (!newsImage.isNullOrEmpty()) {
            Glide.with(this).load(newsImage).into(imageView)
        }
    }
}
