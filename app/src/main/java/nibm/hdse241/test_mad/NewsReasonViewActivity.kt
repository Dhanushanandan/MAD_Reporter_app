package nibm.hdse241.test_mad

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class NewsReasonViewActivity : AppCompatActivity() {

    private lateinit var tvNewsList: TextView
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_reasons)

        // Initialize UI component
        tvNewsList = findViewById(R.id.tv_news_list)

        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance("https://test-mad-af0eb-default-rtdb.asia-southeast1.firebasedatabase.app/").reference

        // Fetch and display news reasons
        fetchRejectedNewsReasons()
    }

    private fun fetchRejectedNewsReasons() {
        val rejectRef = database.child("RejectNewsReasons")
        val updateRef = database.child("UpdateNewsReasons")

        val newsList = StringBuilder()

        rejectRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    newsList.append("Rejected News:\n\n")
                    for (newsSnapshot in snapshot.children) {
                        val newsId = newsSnapshot.key ?: "Unknown ID"
                        val reason = newsSnapshot.value.toString()
                        newsList.append("News ID: $newsId\nReason: $reason\n\n")
                    }
                }
                fetchUpdatedNewsReasons(newsList)  // Fetch updated reasons next
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@NewsReasonViewActivity, "Failed to load rejected news", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchUpdatedNewsReasons(newsList: StringBuilder) {
        val updateRef = database.child("UpdateNewsReasons")

        updateRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    newsList.append("Updated News:\n\n")
                    for (newsSnapshot in snapshot.children) {
                        val newsId = newsSnapshot.key ?: "Unknown ID"
                        val reason = newsSnapshot.value.toString()
                        newsList.append("News ID: $newsId\nReason: $reason\n\n")
                    }
                }
                // Set final text to TextView
                tvNewsList.text = newsList.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@NewsReasonViewActivity, "Failed to load updated news", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
