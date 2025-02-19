package nibm.hdse241.test_mad

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ReporterUpdatePostActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_reporter_update_post)

        // Set up window insets listener for edge-to-edge layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val btnSubmitNews = findViewById<Button>(R.id.btn_submit_news)
        val etNewsId = findViewById<EditText>(R.id.et_news_id)
        val etNewsTopic = findViewById<EditText>(R.id.et_news_topic)
        val etNewsContent = findViewById<EditText>(R.id.et_news_content)
        val etNewsLocation = findViewById<EditText>(R.id.et_news_location)
        val tvNewsDateTime = findViewById<EditText>(R.id.Date)
        val spinnerNewsCategory = findViewById<Spinner>(R.id.spinner_news_category)
        val status = "pending"


        btnSubmitNews.setOnClickListener {
            // Get user input values
            val newsId = etNewsId.text.toString().trim()
            val newsTopic = etNewsTopic.text.toString().trim()
            val newsContent = etNewsContent.text.toString().trim()
            val newsLocation = etNewsLocation.text.toString().trim()
            val newsDateTime = tvNewsDateTime.text.toString().trim()
            val newsCategory = spinnerNewsCategory.selectedItem.toString()

            // Ensure all required fields are filled
            if (newsId.isEmpty() || newsTopic.isEmpty() || newsContent.isEmpty() ||
                newsLocation.isEmpty() || newsDateTime.isEmpty()
            ) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                // Store the data in Firebase
                updateNewsToFirebase(
                    newsId,
                    newsTopic,
                    newsContent,
                    newsLocation,
                    newsDateTime,
                    newsCategory,
                    status
                )
            }
        }

    }


    private fun updateNewsToFirebase(newsId: String, newsTopic: String, newsContent: String,
                                     newsLocation: String, newsDateTime: String, newsCategory: String, status: String) {
        val database = FirebaseDatabase.getInstance("https://test-mad-af0eb-default-rtdb.asia-southeast1.firebasedatabase.app/")
        val newsRef: DatabaseReference = database.getReference("ReporterNewsPost")

        newsRef.child(newsId).get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val updateMap = mapOf(
                    "newsId" to newsId,
                    "newsTopic" to newsTopic,
                    "newsContent" to newsContent,
                    "newsLocation" to newsLocation,
                    "newsDateTime" to newsDateTime,
                    "newsCategory" to newsCategory,
                    "status" to status
                )

                newsRef.child(newsId).updateChildren(updateMap)
                    .addOnSuccessListener {
                        Toast.makeText(this, "News updated successfully!", Toast.LENGTH_SHORT).show()

                        val btnSubmitNews = findViewById<Button>(R.id.btn_submit_news)
                        val etNewsId = findViewById<EditText>(R.id.et_news_id)
                        val etNewsTopic = findViewById<EditText>(R.id.et_news_topic)
                        val etNewsContent = findViewById<EditText>(R.id.et_news_content)
                        val etNewsLocation = findViewById<EditText>(R.id.et_news_location)
                        val tvNewsDateTime = findViewById<EditText>(R.id.Date)
                        val spinnerNewsCategory = findViewById<Spinner>(R.id.spinner_news_category)

                        etNewsId.text.clear()
                        etNewsTopic.text.clear()
                        etNewsContent.text.clear()
                        etNewsLocation.text.clear()
                        tvNewsDateTime.text.clear()


                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(this, "Update failed: ${exception.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "News ID does not exist", Toast.LENGTH_SHORT).show()
            }
        }
    }

    data class NewsPost(
        val newsId: String,
        val newsTopic: String,
        val newsContent: String,
        val newsLocation: String,
        val newsDateTime: String,
        val newsCategory: String,
        val status: String
    )
}
