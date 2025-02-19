package nibm.hdse241.test_mad

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class DashboardActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dashboard)

        // Set up window insets listener for edge-to-edge layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // collect email and pass from login
        val email = intent.getStringExtra("email")
        val password = intent.getStringExtra("password")
        val userId = intent.getStringExtra("USER_ID")
        val type = intent.getStringExtra("Type")

        val Reporterbtn: ImageView = findViewById(R.id.Reporterbtn)
        val Aprovelbtn: ImageView = findViewById(R.id.Aprovelbtn)
        val Postbtn: ImageView = findViewById(R.id.postbtn)
        val Logoutbtn: ImageView = findViewById(R.id.Logoutbtn)

        if(type == "viewer"){
            Reporterbtn.isEnabled = false
            Aprovelbtn.isEnabled = false
            Postbtn.isEnabled = false


        }else if (type == "reporter"){
            Reporterbtn.isEnabled = false
            Aprovelbtn.isEnabled = false
        }




        val user_profile: ImageView = findViewById(R.id.user_profile)

        user_profile.setOnClickListener {
            val intent = Intent(this, User_profileActivity::class.java)
            intent.putExtra("email", email)
            intent.putExtra("password", password)
            intent.putExtra("USER_ID", userId)
            startActivity(intent)
            finish()
        }

//        val Logoutbtn: ImageView = findViewById(R.id.Logoutbtn)

        Logoutbtn.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        val Homebtn: ImageView = findViewById(R.id.Homebtn)

        Homebtn.setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            Log.d("ActivityLaunch", "Launching Admin_reporterActivity")
            startActivity(intent)

        }

//        val Reporterbtn: ImageView = findViewById(R.id.Reporterbtn)

        Reporterbtn.setOnClickListener {
            val intent = Intent(this, AdminReporterActivity::class.java)
            startActivity(intent)
        }


        val postbtn: ImageView = findViewById(R.id.postbtn)

        postbtn.setOnClickListener {
            val intent = Intent(this, AdminPostActivity::class.java)
            startActivity(intent)
        }

    }
}

