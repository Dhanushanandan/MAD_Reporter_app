package nibm.hdse241.test_mad

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import nibm.hdse241.test_mad.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)  // Initialize Firebase
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.Registerbtn.setOnClickListener {
            val email = binding.useremailtxt.text.toString().trim()
            val pass = binding.Passwordtxt.text.toString().trim()
            val username = binding.usernametxt.text.toString().trim()
            val mobile = binding.usermobiletxt.text.toString().trim()

            // Check if the fields are not empty
            if (email.isNotEmpty() && pass.isNotEmpty() && username.isNotEmpty() && mobile.isNotEmpty()) {
                // Check password length
                if (pass.length < 6) {
                    Toast.makeText(this, "Password must be at least 6 characters!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                // Check mobile no length
                if (mobile.length < 10) {
                    Toast.makeText(this, "Mobile no must be 10 digit!", Toast.LENGTH_SHORT).show()
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
                                "userId" to userId,
                                "username" to username,
                                "email" to email,
                                "mobile" to mobile,
                                "type" to "viewer"  // Default type
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
                                        binding.usernametxt.text.clear()
                                        binding.usermobiletxt.text.clear()

                                        // Redirect to Login page
                                        startActivity(Intent(this, LoginActivity::class.java))
                                        finish()
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
                Toast.makeText(this, "Fields cannot be empty!", Toast.LENGTH_SHORT).show()
            }
        }

        // Go to Login Page
        binding.loginTxt.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}
