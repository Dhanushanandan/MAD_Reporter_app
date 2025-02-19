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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class User_profileActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    private lateinit var userid_txt: TextView
    private lateinit var usernametxt: TextView
    private lateinit var useremailtxt: TextView
    private lateinit var usermobiletxt: TextView
    private lateinit var current_Passwordtxt1: TextView
    private lateinit var new_Passwordtxt1: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_user_profile)

        // Set up window insets listener for edge-to-edge layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize Firebase Authentication
        auth = FirebaseAuth.getInstance()

        // Collect email and pass from login
        val email = intent.getStringExtra("email")
        val password = intent.getStringExtra("password")
        val userId = intent.getStringExtra("USER_ID")

        if (userId != null) {
            // Fetch user data from Firebase Realtime Database
            FirebaseDatabase.getInstance("https://test-mad-af0eb-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Users")
                .child(userId)
                .get()
                .addOnSuccessListener { dataSnapshot ->

                    if (dataSnapshot.exists()) {
                        val name = dataSnapshot.child("username").getValue(String::class.java)
                        val email = dataSnapshot.child("email").getValue(String::class.java)
                        val mobile = dataSnapshot.child("mobile").getValue(String::class.java)
                        val userId = dataSnapshot.child("userId").getValue(String::class.java)


                        userid_txt = findViewById(R.id.userid_txt)
                        usernametxt = findViewById(R.id.usernametxt)
                        useremailtxt = findViewById(R.id.useremailtxt)
                        usermobiletxt = findViewById(R.id.usermobiletxt)
                        current_Passwordtxt1 = findViewById(R.id.current_Passwordtxt)
                        new_Passwordtxt1 = findViewById(R.id.new_Passwordtxt)


                        userid_txt.text = userId
                        usernametxt.text = name
                        useremailtxt.text = email
                        usermobiletxt.text = mobile
                        current_Passwordtxt1.text = ""
                        new_Passwordtxt1.text = ""

                        val Updatebtn = findViewById<Button>(R.id.Updatebtn)

                        Updatebtn.setOnClickListener {
                            val userid_txt1 = userid_txt.text.toString().trim()
                            val usernametxt1 = usernametxt.text.toString().trim()
                            val useremailtxt1 = useremailtxt.text.toString().trim()
                            val usermobiletxt1 = usermobiletxt.text.toString().trim()
                            val current_Passwordtxt = current_Passwordtxt1.text.toString().trim()
                            val new_Passwordtxt = new_Passwordtxt1.text.toString().trim()

                            // Check if all fields are filled
                            if (userid_txt1.isNotEmpty() && usernametxt1.isNotEmpty() && useremailtxt1.isNotEmpty() && usermobiletxt1.isNotEmpty() && current_Passwordtxt.isNotEmpty() && new_Passwordtxt.isNotEmpty()) {

                                // Check if User Id and Email match
                                if (userid_txt1 == userId && useremailtxt1 == email) {

                                    // Validate password length
                                    if (current_Passwordtxt.length < 6 || new_Passwordtxt.length < 6) {
                                        Toast.makeText(this, "Password must be at least 6 characters!", Toast.LENGTH_SHORT).show()
                                        return@setOnClickListener
                                    }

                                    // Validate mobile number length
                                    if (usermobiletxt1.length < 10) {
                                        Toast.makeText(this, "Mobile No must be at least 10 digits!", Toast.LENGTH_SHORT).show()
                                        return@setOnClickListener
                                    }


                                    if (current_Passwordtxt == password) {

                                        // Update password in Firebase Authentication
                                        auth.currentUser?.updatePassword(new_Passwordtxt)?.addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                Toast.makeText(this, "New Password updated successfully!", Toast.LENGTH_SHORT).show()
                                            } else {
                                                Toast.makeText(this, "Password Not Updated!", Toast.LENGTH_SHORT).show()
                                            }
                                        }

                                        // Update user data in Firebase Realtime Database
                                        val userUpdates = mapOf(
                                            "username" to usernametxt1,
                                            "email" to useremailtxt1,
                                            "mobile" to usermobiletxt1
                                        )

                                        FirebaseDatabase.getInstance("https://test-mad-af0eb-default-rtdb.asia-southeast1.firebasedatabase.app/")
                                            .getReference("Users")
                                            .child(userId)
                                            .updateChildren(userUpdates)
                                            .addOnSuccessListener {
                                                Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                                                // Redirect to Login page
                                                startActivity(Intent(this, LoginActivity::class.java))
                                                finish()

                                            }
                                            .addOnFailureListener { exception ->
                                                Toast.makeText(this, "Update failed: ${exception.message}", Toast.LENGTH_SHORT).show()
                                            }

                                    } else {
                                        Toast.makeText(this, "Current Password is incorrect!", Toast.LENGTH_SHORT).show()
                                    }

                                } else {
                                    Toast.makeText(this, "User Id or Email cannot be changed!", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Toast.makeText(this, "Please fill all the fields!", Toast.LENGTH_SHORT).show()
                            }
                        }

                    } else {
                        Toast.makeText(applicationContext, "User details not found", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Failed to fetch user data: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show()
        }
    }
}
