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
import com.google.firebase.database.FirebaseDatabase

class AdminPostActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin_post)

        // Set up window insets listener for edge-to-edge layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val newnews: Button = findViewById(R.id.newnews)

        newnews.setOnClickListener {
            val intent = Intent(this, NewNewsPost::class.java)
            startActivity(intent)
        }

        val update_news: Button = findViewById(R.id.update_news)

        update_news.setOnClickListener {
            val intent = Intent(this, AdminUpdatePostActivity::class.java)
            startActivity(intent)
        }

        val approvel_news: Button = findViewById(R.id.approvel_news)

        approvel_news.setOnClickListener {
            val intent = Intent(this, AdminAprovelPostActivity::class.java)
            startActivity(intent)
        }


        val btn_remove_news: Button = findViewById(R.id.btn_remove_news)
        val etNewsId = findViewById<EditText>(R.id.et_news_id)

        btn_remove_news.setOnClickListener {
            val newsId = etNewsId.text.toString().trim()

            if (newsId.isNotEmpty()) {
                FirebaseDatabase
                    .getInstance("https://test-mad-af0eb-default-rtdb.asia-southeast1.firebasedatabase.app")
                    .getReference("NewsPost")
                    .child(newsId) // Use the correct variable
                    .removeValue()
                    .addOnCompleteListener { dbTask ->
                        if (dbTask.isSuccessful) {
                            Toast.makeText(this, "Post Removed Successfully", Toast.LENGTH_SHORT).show()
                            etNewsId.text.clear() // Correct way to clear the EditText
                        } else {
                            Toast.makeText(this, "Error Removing News Post: ${dbTask.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Enter the News ID", Toast.LENGTH_SHORT).show()
            }
        }


    }
}
