package nibm.hdse241.test_mad

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import nibm.hdse241.test_mad.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.loginbtn2.setOnClickListener {
            val email = binding.logintxt.text.toString().trim()
            val password = binding.loginPass.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                // Sign in with email and password
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        val userId = user!!.uid

                        // Fetch user data from Firebase Realtime Database
                        FirebaseDatabase.getInstance("https://test-mad-af0eb-default-rtdb.asia-southeast1.firebasedatabase.app/")
                            .getReference("Users")
                            .child(userId)
                            .get()
                            .addOnSuccessListener { dataSnapshot ->

                                Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show()
                                val type = dataSnapshot.child("type").getValue(String::class.java)

                                // Redirect to DashboardActivity
                                val intent = Intent(this, DashboardActivity::class.java)
                                intent.putExtra("email", email)
                                intent.putExtra("password", password)
                                intent.putExtra("USER_ID", userId)
                                intent.putExtra("Type", type)
                                startActivity(intent)
                                finish()
                            }
                            .addOnFailureListener { exception ->
                                Toast.makeText(this, "Failed to fetch user data: ${exception.message}", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        Toast.makeText(this, "Login Failed: Incorrect Password or email Id", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Please fill in both fields", Toast.LENGTH_SHORT).show()
            }
        }

        val Registertxt: TextView = findViewById(R.id.Registertxt)

        Registertxt.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}
