package nibm.hdse241.test_mad

import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.FirebaseDatabase
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream

class DraftNewsActivity : AppCompatActivity() {

    private lateinit var listViewDrafts: ListView
    private lateinit var btnSubmitAllDrafts: Button
    private lateinit var draftsList: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_draft_news_view)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        listViewDrafts = findViewById(R.id.listViewDrafts)
        btnSubmitAllDrafts = findViewById(R.id.btnSubmitDraft)

        loadDraftFiles()

        btnSubmitAllDrafts.setOnClickListener {
            if (draftsList.isNotEmpty()) {
                uploadAllDraftsToFirebase()
            } else {
                Toast.makeText(this, "No drafts available to upload", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadDraftFiles() {
        val filesDir = filesDir
        val jsonFiles = filesDir.list { _, name -> name.endsWith(".json") } ?: arrayOf()
        draftsList = ArrayList(jsonFiles.toList())

        if (draftsList.isEmpty()) {
            Toast.makeText(this, "No saved drafts found", Toast.LENGTH_SHORT).show()
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, draftsList)
        listViewDrafts.adapter = adapter
    }

    private fun uploadAllDraftsToFirebase() {
        val databaseRef = FirebaseDatabase.getInstance("https://test-mad-af0eb-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference("NewsPost")

        var successCount = 0
        var failureCount = 0

        draftsList.forEach { fileName ->
            try {
                val file = File(filesDir, fileName)
                val fis = FileInputStream(file)
                val content = fis.bufferedReader().use { it.readText() }
                fis.close()

                val jsonObject = JSONObject(content)

                val newsId = jsonObject.getString("newsId")
                val newsTopic = jsonObject.getString("newsTopic")
                val newsContent = jsonObject.getString("newsContent")
                val newsLocation = jsonObject.getString("newsLocation")
                val newsDate = jsonObject.getString("newsDate")
                val newsCategory = jsonObject.getString("newsCategory")
                val imageUri = jsonObject.getString("imageUri")

                val newsPost = NewsPost(newsId, newsTopic, newsContent, newsLocation, newsDate, newsCategory, imageUri)

                databaseRef.child(newsId).setValue(newsPost).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        successCount++
                        file.delete() // Delete the file after successful upload
                    } else {
                        failureCount++
                    }

                    // Check if all files are processed
                    if (successCount + failureCount == draftsList.size) {
                        loadDraftFiles() // Refresh list
                        Toast.makeText(this, "Uploaded: $successCount, Failed: $failureCount", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                failureCount++
            }
        }
    }

    data class NewsPost(
        val newsId: String,
        val newsTopic: String,
        val newsContent: String,
        val newsLocation: String,
        val newsDate: String,
        val newsCategory: String,
        val imageUrl: String
    )
}
