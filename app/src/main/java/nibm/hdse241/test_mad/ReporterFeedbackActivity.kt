package nibm.hdse241.test_mad

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ReporterFeedbackActivity : AppCompatActivity() {

    private lateinit var reporteridtxt: EditText
    private lateinit var feedbacktxt: EditText
    private lateinit var ratingtxt: EditText
    private lateinit var submitButton: Button
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reporter_feedback)

        // Initialize Firebase
        FirebaseApp.initializeApp(this)
        val database = FirebaseDatabase.getInstance("https://test-mad-af0eb-default-rtdb.asia-southeast1.firebasedatabase.app/")
        databaseReference = database.reference.child("Feedback")

        reporteridtxt = findViewById(R.id.reporteridtxt)
        feedbacktxt = findViewById(R.id.usermobiletxt)
        ratingtxt = findViewById(R.id.reporter_rateing_txt)
        submitButton = findViewById(R.id.feedbackbtn)

        submitButton.setOnClickListener {
            submitFeedback()
        }
    }

    private fun submitFeedback() {
        val reporterId = reporteridtxt.text.toString().trim()
        val feedback = feedbacktxt.text.toString().trim()
        val rating = ratingtxt.text.toString().trim()

        if (TextUtils.isEmpty(reporterId) || TextUtils.isEmpty(feedback) || TextUtils.isEmpty(rating)) {
            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show()
            return
        }

        val feedbackId = databaseReference.push().key ?: return
        val feedbackData = Feedback(reporterId, feedback, rating)

        databaseReference.child(feedbackId).setValue(feedbackData)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Feedback Submitted", Toast.LENGTH_SHORT).show()
                    clearFields()
                } else {
                    Log.e("FirebaseError", "Error submitting feedback", task.exception)
                    Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun clearFields() {
        reporteridtxt.text.clear()
        feedbacktxt.text.clear()
        ratingtxt.text.clear()
    }
}

// Data class for Feedback
data class Feedback(val reporterId: String, val feedback: String, val rating: String)
