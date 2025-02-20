package nibm.hdse241.test_mad

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdminAprovelPostActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var newsContainer: LinearLayout
    private lateinit var progressBar: ProgressBar
    var type : String? = null





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin_aprovel)

        // Set up window insets listener for edge-to-edge layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val type = intent.getStringExtra("Type")
        Log.d("AdminAprovelPostActivity", "type: $type")

        // Initialize Firebase Database Reference
        database = FirebaseDatabase.getInstance("https://test-mad-af0eb-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference("ReporterNewsPost")

        // Finding the layout where news will be displayed
        newsContainer = findViewById(R.id.view2)
        progressBar = findViewById(R.id.loading2)

        // Fetch and display news
        fetchNews(type)


    }

    private fun fetchNews(type : String?) {
        progressBar.visibility = ProgressBar.VISIBLE

        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                progressBar.visibility = ProgressBar.GONE
                newsContainer.removeAllViews() // Clear previous views

                for (newsSnapshot in snapshot.children) {
                    val newsTitle = newsSnapshot.child("newsTopic").getValue(String::class.java) ?: "No Title"
                    val newsContent = newsSnapshot.child("newsContent").getValue(String::class.java) ?: "No Content"
                    val newsDate = newsSnapshot.child("newsDateTime").getValue(String::class.java) ?: "No Date"
                    val newsImage = newsSnapshot.child("imageUrl").getValue(String::class.java) ?: "" // Image URL
                    val status = newsSnapshot.child("status").getValue(String::class.java) ?: "No Status" // Image URL
                    val newsId = newsSnapshot.child("newsId").getValue(String::class.java) ?: "" // Image URL
                    val newsLocation = newsSnapshot.child("newsLocation").getValue(String::class.java) ?: "" // Image URL
                    val newsCategory = newsSnapshot.child("newsCategory").getValue(String::class.java) ?: "" // Image URL

                    // Create a new TextView for news item
                    val newsTextView = TextView(this@AdminAprovelPostActivity).apply {
                        text = "$newsTitle\n$newsDate\n$status"
                        textSize = 16f
                        setPadding(16, 16, 16, 16)
                        gravity = Gravity.START
                        setTextColor(Color.WHITE)
                    }

                    // Set click listener to open NewsDetailActivity
                    newsTextView.setOnClickListener {
                        val intent = Intent(this@AdminAprovelPostActivity, Admin_Aprovel_NewsActivity::class.java)
                        intent.putExtra("newsTitle", newsTitle)
                        intent.putExtra("newsContent", newsContent)
                        intent.putExtra("newsDate", newsDate)
                        intent.putExtra("newsImage", newsImage)
                        intent.putExtra("Status", status)
                        intent.putExtra("Type", type)
                        intent.putExtra("newsId", newsId)
                        intent.putExtra("newsLocation", newsLocation)
                        intent.putExtra("newsCategory", newsCategory)
                        startActivity(intent)
                    }

                    // Add the TextView to the container
                    newsContainer.addView(newsTextView)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                progressBar.visibility = ProgressBar.GONE
                Log.e("FirebaseError", "Failed to fetch news: ${error.message}")
            }
        })
    }

    override fun onRestart() {
        super.onRestart()
        fetchNews(type)  // Refresh news feed
    }
}