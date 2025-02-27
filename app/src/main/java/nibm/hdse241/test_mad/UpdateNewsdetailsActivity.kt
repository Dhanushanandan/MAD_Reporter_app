package nibm.hdse241.test_mad

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class UpdateNewsdetailsActivity  : AppCompatActivity() {
    private lateinit var etRejectReason: EditText
    private lateinit var btnSubmitReason: Button
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_update_news_details)

        // Initialize UI components
        etRejectReason = findViewById(R.id.et_reject_reason)
        btnSubmitReason = findViewById(R.id.btn_submit_reason)

        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance("https://test-mad-af0eb-default-rtdb.asia-southeast1.firebasedatabase.app/").reference

        // Get News ID from Intent
        val newsId = intent.getStringExtra("newsId")

        btnSubmitReason.setOnClickListener {
            val reason = etRejectReason.text.toString().trim()

            if (reason.isEmpty()) {
                Toast.makeText(this, "Please enter a reason", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Save to Firebase
            if (newsId != null) {
                val rejectRef = database.child("UpdateNewsReasons").child(newsId)
                rejectRef.setValue(reason)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Reason submitted successfully", Toast.LENGTH_SHORT).show()
                        finish() // Close activity after submission
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Failed to submit reason", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "News ID not found", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
