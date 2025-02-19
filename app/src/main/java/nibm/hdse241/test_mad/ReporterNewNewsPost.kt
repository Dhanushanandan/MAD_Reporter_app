package nibm.hdse241.test_mad

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ReporterNewNewsPost : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reporter_new_news)

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
                saveNewsToFirebase(
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


    private fun saveNewsToFirebase(
        newsId: String, newsTopic: String, newsContent: String,
        newsLocation: String, newsDateTime: String, newsCategory: String, status: String
    ) {
        // Firebase Realtime Database reference
        val database =
            FirebaseDatabase.getInstance("https://test-mad-af0eb-default-rtdb.asia-southeast1.firebasedatabase.app/")
        val newsRef: DatabaseReference = database.getReference("ReporterNewsPost")

        // Check if the newsId already exists in Firebase
        newsRef.child(newsId).get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                Toast.makeText(this, "News ID already exists", Toast.LENGTH_SHORT).show()
            } else {
                // Create a news post object
                val newsPost = NewsPost(
                    newsId,
                    newsTopic,
                    newsContent,
                    newsLocation,
                    newsDateTime,
                    newsCategory,
                    status
                )

                // Save news post to Firebase under the node "NewsPost"
                newsRef.child(newsId).setValue(newsPost).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(this, "News posted successfully", Toast.LENGTH_SHORT).show()
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

                    } else {
                        Toast.makeText(this, "Failed to post news", Toast.LENGTH_SHORT).show()
                    }
                }
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
