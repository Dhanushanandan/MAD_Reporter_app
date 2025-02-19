package nibm.hdse241.test_mad

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import nibm.hdse241.test_mad.databinding.ActivityAdminReporterBinding

class AdminReporterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminReporterBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)  // Initialize Firebase
        binding = ActivityAdminReporterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.Registerbtn.setOnClickListener {

            val email = binding.useremailtxt.text.toString().trim()
            val pass = binding.Passwordtxt.text.toString().trim()
            val username = binding.Reporternametxt.text.toString().trim()
            val mobile = binding.usermobiletxt.text.toString().trim()
            val reporteridtxt = binding.reporteridtxt.text.toString().trim()

            // Check if the fields are not empty
            if (email.isNotEmpty() && pass.isNotEmpty() && username.isNotEmpty() && mobile.isNotEmpty() && reporteridtxt.isNotEmpty()) {
                // Check password length
                if (pass.length < 6) {
                    Toast.makeText(this, "Password must be at least 6 characters!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                // Check mobile no length
                if (mobile.length < 10) {
                    Toast.makeText(this, "Mobile no must be 10 digits!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                // Create user with email and password
                auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        if (user != null) {
                            val userId = user.uid

                            // Store user details in Realtime Database
                            val userMap = hashMapOf(
                                "username" to username,
                                "email" to email,
                                "reporterid" to userId,
                                "mobile" to mobile,
                                "type" to "reporter"  // Default type
                            )

                            // Write data to Realtime Database
                            FirebaseDatabase.getInstance("https://test-mad-af0eb-default-rtdb.asia-southeast1.firebasedatabase.app")
                                .getReference("Users").child(userId)
                                .setValue(userMap).addOnCompleteListener { dbTask ->
                                    if (dbTask.isSuccessful) {
                                        Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show()

                                        // Clear the text fields
                                        binding.useremailtxt.text.clear()
                                        binding.Passwordtxt.text.clear()
                                        binding.Reporternametxt.text.clear()
                                        binding.usermobiletxt.text.clear()
                                        binding.reporteridtxt.text.clear()
                                    } else {
                                        // Log error if database write fails
                                        Log.e("RegisterActivity", "Database Error: ${dbTask.exception?.message}")
                                        Toast.makeText(this, "Database Error: ${dbTask.exception?.message}", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        } else {
                            Toast.makeText(this, "Authentication failed, user is null.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        // Log error if authentication fails
                        Log.e("RegisterActivity", "Authentication Failed: ${task.exception?.message}")
                        Toast.makeText(this, "Authentication Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Fill all the textfields!", Toast.LENGTH_SHORT).show()
            }
        }



        val RemoveReporterbtn: Button = findViewById(R.id.RemoveReporterbtn)

        RemoveReporterbtn.setOnClickListener {
            val reporterId = binding.reporteridtxt.text.toString().trim()


            if(reporterId.isNotEmpty()){

                FirebaseDatabase
                    .getInstance("https://test-mad-af0eb-default-rtdb.asia-southeast1.firebasedatabase.app")
                    .getReference("Users")
                    .child(reporterId).removeValue().addOnCompleteListener { dbTask ->
                        if (dbTask.isSuccessful) {
                            Toast.makeText(this, "Reporter Removed Successfully", Toast.LENGTH_SHORT).show()
                            binding.reporteridtxt.text.clear()
                        } else {
                            Toast.makeText(this, "Error Removing Reporter: ${dbTask.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            }else{
                Toast.makeText(this, "Enter the Reporter Id && Email", Toast.LENGTH_SHORT).show()
            }

        }


        val feedbackbtn: Button = findViewById(R.id.feedbackbtn)

        feedbackbtn.setOnClickListener {
            val intent = Intent(this, ReporterFeedbackActivity::class.java)
            startActivity(intent)
        }


        val ViewReporters: Button = findViewById(R.id.ViewReporters)

        ViewReporters.setOnClickListener {
            val intent = Intent(this, AdminReporterViewActivity::class.java)
            startActivity(intent)
        }


    }
}