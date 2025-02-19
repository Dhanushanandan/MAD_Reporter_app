package nibm.hdse241.test_mad

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class NewNewsPost : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_post)

        val btnSubmitNews = findViewById<Button>(R.id.btn_submit_news)
        val etNewsId = findViewById<EditText>(R.id.et_news_id)
        val etNewsTopic = findViewById<EditText>(R.id.et_news_topic)
        val etNewsContent = findViewById<EditText>(R.id.et_news_content)
        val etNewsLocation = findViewById<EditText>(R.id.et_news_location)
        val tvNewsDateTime = findViewById<EditText>(R.id.Date)
        val spinnerNewsCategory = findViewById<Spinner>(R.id.spinner_news_category)

//        val calendar = Calendar.getInstance()
//
//        // Show DatePicker when clicking on the TextView (Date)
//        tvNewsDateTime.setOnClickListener {
//            val datePicker = DatePickerDialog(this, { _, year, month, dayOfMonth ->
//                calendar.set(year, month, dayOfMonth)
//                showTimePicker(calendar)
//            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
//            datePicker.show()
//        }

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
                    newsCategory
                )
            }
        }
    }

//    // Function to show TimePicker
//    private fun showTimePicker(calendar: Calendar) {
//        val timePicker = TimePickerDialog(this, { _, hourOfDay, minute ->
//            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
//            calendar.set(Calendar.MINUTE, minute)
//
//            // Format and set Date & Time
//            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
//            val dateString = dateFormat.format(calendar.time)
//            findViewById<TextView>(R.id.tv_news_datetime).text = dateString
//        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false)
//        timePicker.show()
//    }

    private fun saveNewsToFirebase(
        newsId: String, newsTopic: String, newsContent: String,
        newsLocation: String, newsDateTime: String, newsCategory: String
    ) {
        // Firebase Realtime Database reference
        val database =
            FirebaseDatabase.getInstance("https://test-mad-af0eb-default-rtdb.asia-southeast1.firebasedatabase.app/")
        val newsRef: DatabaseReference = database.getReference("NewsPost")

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
                    newsCategory
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
        val newsCategory: String
    )
}
