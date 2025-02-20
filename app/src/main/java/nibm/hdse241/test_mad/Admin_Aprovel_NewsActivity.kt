package nibm.hdse241.test_mad

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.database.FirebaseDatabase
import java.util.UUID

class Admin_Aprovel_NewsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_aprovel_news_deatils)

        val btn_approve2: Button = findViewById(R.id.btn_approve2)
        val btn_reject2: Button = findViewById(R.id.btn_reject2)
        val btn_update2: Button = findViewById(R.id.btn_update2)

        val type = intent.getStringExtra("Type")
        Log.d("Admin_Aprovel_NewsActivity", "type: $type")

        if (type == "reporter" || type == "viewer") {

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
        val newsTitle = intent.getStringExtra("newsTitle").toString()
        val newsContent = intent.getStringExtra("newsContent").toString()
        val newsDate = intent.getStringExtra("newsDate").toString()
        val newsImage = intent.getStringExtra("newsImage").toString()
        val newsId = intent.getStringExtra("newsId").toString()
        val newsLocation = intent.getStringExtra("newsLocation").toString()
        val newsCategory = intent.getStringExtra("newsCategory").toString()

        // Log received data for debugging
        Log.d("Admin_Aprovel_NewsActivity", "Title: $newsTitle")
//        Log.d("Admin_Aprovel_NewsActivity", "Content: $newsContent")
        Log.d("Admin_Aprovel_NewsActivity", "Date: $newsDate")
        Log.d("Admin_Aprovel_NewsActivity", "Image URL: $newsImage")

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

        btn_approve2.setOnClickListener {
            saveNewsToFirebase(newsTitle,newsContent,newsLocation,newsDate,newsCategory,newsImage)
            updateNewsToFirebase(newsId,newsTitle,newsContent,newsLocation,newsDate,newsCategory,newsImage)
        }

        btn_reject2.setOnClickListener {
            updatestatusToFirebase(newsId,newsTitle,newsContent,newsLocation,newsDate,newsCategory,newsImage)
        }

        btn_update2.setOnClickListener {
            updatestatusToFirebase2(newsId,newsTitle,newsContent,newsLocation,newsDate,newsCategory,newsImage)
        }

    }


    private fun saveNewsToFirebase(
//        newsId: String,
        newsTopic: String,
        newsContent: String,
        newsLocation: String,
        newsDateTime: String,
        newsCategory: String,
        imageUrl: String
    ) {

        val newsId = UUID.randomUUID().toString()

        val databaseRef =
            FirebaseDatabase.getInstance("https://test-mad-af0eb-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("NewsPost").child(newsId)

        databaseRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                Toast.makeText(this, "News ID already exists", Toast.LENGTH_SHORT).show()
            } else {
                databaseRef.setValue(
                    NewsPost(
                        newsId,
                        newsTopic,
                        newsContent,
                        newsLocation,
                        newsDateTime,
                        newsCategory,
                        imageUrl
                    )
                )
                    .addOnCompleteListener { task ->
                        val message =
                            if (task.isSuccessful) "News posted successfully" else "Failed to post news"
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    private fun updateNewsToFirebase(newsId: String, newsTopic: String, newsContent: String,
                                     newsLocation: String, newsDateTime: String, newsCategory: String, newImageUrl: String?) {
        val databaseRef = FirebaseDatabase.getInstance("https://test-mad-af0eb-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference("ReporterNewsPost").child(newsId)

        databaseRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val existingImageUrl = snapshot.child("imageUrl").getValue(String::class.java)
                val updateMap = mapOf(
                    "newsId" to newsId,
                    "newsTopic" to newsTopic,
                    "newsContent" to newsContent,
                    "newsLocation" to newsLocation,
                    "newsDateTime" to newsDateTime,
                    "newsCategory" to newsCategory,
                    "imageUrl" to (newImageUrl ?: existingImageUrl), // Keep existing image if new one is not uploaded
                    "status" to "Aproved"
                )
                databaseRef.updateChildren(updateMap).addOnCompleteListener { task ->
                    val message = if (task.isSuccessful) "News updated successfully" else "Failed to update news"
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "News ID does not exist", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun updatestatusToFirebase(newsId: String, newsTopic: String, newsContent: String,
                                     newsLocation: String, newsDateTime: String, newsCategory: String, newImageUrl: String?) {
        val databaseRef = FirebaseDatabase.getInstance("https://test-mad-af0eb-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference("ReporterNewsPost").child(newsId)

        databaseRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val existingImageUrl = snapshot.child("imageUrl").getValue(String::class.java)
                val updateMap = mapOf(
                    "newsId" to newsId,
                    "newsTopic" to newsTopic,
                    "newsContent" to newsContent,
                    "newsLocation" to newsLocation,
                    "newsDateTime" to newsDateTime,
                    "newsCategory" to newsCategory,
                    "imageUrl" to (newImageUrl ?: existingImageUrl), // Keep existing image if new one is not uploaded
                    "status" to "Rejected"
                )
                databaseRef.updateChildren(updateMap).addOnCompleteListener { task ->
                    val message = if (task.isSuccessful) "News updated successfully" else "Failed to update news"
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "News ID does not exist", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updatestatusToFirebase2(newsId: String, newsTopic: String, newsContent: String,
                                       newsLocation: String, newsDateTime: String, newsCategory: String, newImageUrl: String?) {
        val databaseRef = FirebaseDatabase.getInstance("https://test-mad-af0eb-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference("ReporterNewsPost").child(newsId)

        databaseRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val existingImageUrl = snapshot.child("imageUrl").getValue(String::class.java)
                val updateMap = mapOf(
                    "newsId" to newsId,
                    "newsTopic" to newsTopic,
                    "newsContent" to newsContent,
                    "newsLocation" to newsLocation,
                    "newsDateTime" to newsDateTime,
                    "newsCategory" to newsCategory,
                    "imageUrl" to (newImageUrl ?: existingImageUrl), // Keep existing image if new one is not uploaded
                    "status" to "Update"
                )
                databaseRef.updateChildren(updateMap).addOnCompleteListener { task ->
                    val message = if (task.isSuccessful) "News updated successfully" else "Failed to update news"
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
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
        val imageUrl: String
    )



}
