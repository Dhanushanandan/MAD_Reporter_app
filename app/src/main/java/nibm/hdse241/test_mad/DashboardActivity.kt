package nibm.hdse241.test_mad

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class DashboardActivity : AppCompatActivity() {
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
    }
}

//class DashboardActivity : AppCompatActivity() {
//
//    private lateinit var binding: ActivityDashboardBinding
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityDashboardBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        val userType = intent.getStringExtra("userType")
//
//        when (userType) {
//            "admin" -> {
//                // Show all buttons for admin
//                binding.postButton.visibility = View.VISIBLE
//                binding.approveButton.visibility = View.VISIBLE
//                binding.reporterButton.visibility = View.VISIBLE
//            }
//            "reporter" -> {
//                // Hide reporter and approval buttons for reporter
//                binding.postButton.visibility = View.VISIBLE
//                binding.approveButton.visibility = View.GONE
//                binding.reporterButton.visibility = View.GONE
//            }
//            "viewer" -> {
//                // Hide all buttons for viewer
//                binding.postButton.visibility = View.GONE
//                binding.approveButton.visibility = View.GONE
//                binding.reporterButton.visibility = View.GONE
//            }
//        }
//    }
//}
