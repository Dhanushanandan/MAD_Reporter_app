package nibm.hdse241.test_mad

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import android.content.Context
import java.io.File
import java.io.FileOutputStream


class NewNewsPost : AppCompatActivity() {

    private lateinit var imgPreview: ImageView
    private var imageUri: Uri? = null
    private val IMGUR_CLIENT_ID = "2357012bba6f9b4"  // Replace with your Imgur Client ID
    private val PICK_IMAGE_REQUEST = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_post)

        val btnSubmitNews = findViewById<Button>(R.id.btn_submit_news)
        val btnSelectImage = findViewById<Button>(R.id.btn_upload_image)
        val Draft_news = findViewById<Button>(R.id.Draft_news)
        imgPreview = findViewById(R.id.iv_news_image)

        btnSelectImage.setOnClickListener { selectImageFromGallery() }
        btnSubmitNews.setOnClickListener { validateAndUploadNews() }
        Draft_news.setOnClickListener { saveNewsAsFile() }
    }

    private fun selectImageFromGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.data
            imgPreview.setImageURI(imageUri)
        }
    }

    private fun validateAndUploadNews() {
        val newsId = findViewById<EditText>(R.id.et_news_id).text.toString().trim()
        val newsTopic = findViewById<EditText>(R.id.et_news_topic).text.toString().trim()
        val newsContent = findViewById<EditText>(R.id.et_news_content).text.toString().trim()
        val newsLocation = findViewById<EditText>(R.id.et_news_location).text.toString().trim()
        val newsDateTime = findViewById<EditText>(R.id.Date).text.toString().trim()
        val newsCategory = findViewById<Spinner>(R.id.spinner_news_category).selectedItem.toString()

        if (newsId.isEmpty() || newsTopic.isEmpty() || newsContent.isEmpty() ||
            newsLocation.isEmpty() || newsDateTime.isEmpty() || imageUri == null) {
            Toast.makeText(this, "Please fill all fields and select an image", Toast.LENGTH_SHORT).show()
        } else {
            uploadImageToImgur(newsId, newsTopic, newsContent, newsLocation, newsDateTime, newsCategory)
        }
    }

    private fun uploadImageToImgur(newsId: String, newsTopic: String, newsContent: String,
                                   newsLocation: String, newsDateTime: String, newsCategory: String) {
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("image", "news.jpg",
                RequestBody.create("image/*".toMediaTypeOrNull(), contentResolver.openInputStream(imageUri!!)!!.readBytes()))
            .build()

        val request = Request.Builder()
            .url("https://api.imgur.com/3/image")
            .addHeader("Authorization", "Client-ID $IMGUR_CLIENT_ID")
            .post(requestBody)
            .build()

        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread { Toast.makeText(applicationContext, "Image upload failed", Toast.LENGTH_SHORT).show() }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    val imageUrl = JSONObject(response.body!!.string()).getJSONObject("data").getString("link")
                    runOnUiThread {
                        Toast.makeText(applicationContext, "Image uploaded", Toast.LENGTH_SHORT).show()
                        saveNewsToFirebase(newsId, newsTopic, newsContent, newsLocation, newsDateTime, newsCategory, imageUrl)
                    }
                }
            }
        })
    }

    private fun saveNewsToFirebase(newsId: String, newsTopic: String, newsContent: String,
                                   newsLocation: String, newsDateTime: String, newsCategory: String, imageUrl: String) {
        val databaseRef = FirebaseDatabase.getInstance("https://test-mad-af0eb-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference("NewsPost").child(newsId)

        databaseRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                Toast.makeText(this, "News ID already exists", Toast.LENGTH_SHORT).show()
            } else {
                databaseRef.setValue(NewsPost(newsId, newsTopic, newsContent, newsLocation, newsDateTime, newsCategory, imageUrl))
                    .addOnCompleteListener { task ->
                        val message = if (task.isSuccessful) "News posted successfully" else "Failed to post news"
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                        if (task.isSuccessful) clearFields()
                    }
            }
        }
    }

    private fun clearFields() {
        findViewById<EditText>(R.id.et_news_id).text.clear()
        findViewById<EditText>(R.id.et_news_topic).text.clear()
        findViewById<EditText>(R.id.et_news_content).text.clear()
        findViewById<EditText>(R.id.et_news_location).text.clear()
        findViewById<EditText>(R.id.Date).text.clear()
        imgPreview.setImageResource(0) // Remove preview image
    }

    private fun saveNewsAsFile() {
        try {
            // Retrieve values directly from input fields
            val newsId = findViewById<EditText>(R.id.et_news_id).text.toString().trim()
            val newsTopic = findViewById<EditText>(R.id.et_news_topic).text.toString().trim()
            val newsContent = findViewById<EditText>(R.id.et_news_content).text.toString().trim()
            val newsLocation = findViewById<EditText>(R.id.et_news_location).text.toString().trim()
            val newsDate = findViewById<EditText>(R.id.Date).text.toString().trim()
            val newsCategory = findViewById<Spinner>(R.id.spinner_news_category).selectedItem.toString()
            val imageUriString = imageUri?.toString() ?: ""

            // Validate required fields
            if (newsId.isEmpty() || newsTopic.isEmpty() || newsContent.isEmpty() ||
                newsLocation.isEmpty() || newsDate.isEmpty()) {
                Toast.makeText(this, "Please fill all fields before saving", Toast.LENGTH_SHORT).show()
                return
            }

            // Create a JSON object
            val newsJson = JSONObject().apply {
                put("newsId", newsId)
                put("newsTopic", newsTopic)
                put("newsContent", newsContent)
                put("newsLocation", newsLocation)
                put("newsDate", newsDate)
                put("newsCategory", newsCategory)
                put("imageUri", imageUriString)
            }

            // Convert JSON object to string
            val newsString = newsJson.toString()

            // Use newsId as the file name
            val fileName = "$newsId.json"

            // Save the file in internal storage
            val fos: FileOutputStream = openFileOutput(fileName, Context.MODE_PRIVATE)
            fos.write(newsString.toByteArray())
            fos.close()

            Toast.makeText(this, "Draft saved successfully as $fileName!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error saving draft", Toast.LENGTH_SHORT).show()
        }
    }



    data class NewsPost(
        val newsId: String,
        val newsTopic: String,
        val newsContent: String,
        val newsLocation: String,
        val newsDateTime: String,
        val newsCategory: String,
        val imageUrl: String
    )
}
