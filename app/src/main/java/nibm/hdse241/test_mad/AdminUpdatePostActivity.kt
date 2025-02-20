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
import java.io.InputStream
import okhttp3.MediaType.Companion.toMediaTypeOrNull

class AdminUpdatePostActivity : AppCompatActivity() {

    private lateinit var imgPreview: ImageView
    private var imageUri: Uri? = null
    private val IMGUR_CLIENT_ID = "2357012bba6f9b4"  // Replace with your Imgur Client ID
    private val PICK_IMAGE_REQUEST = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_admin_post)

        val btnSubmitNews = findViewById<Button>(R.id.btn_submit_news)
        val btnSelectImage = findViewById<Button>(R.id.btn_upload_image)
        imgPreview = findViewById(R.id.iv_news_image)

        btnSelectImage.setOnClickListener { selectImageFromGallery() }
        btnSubmitNews.setOnClickListener { validateAndUploadNews() }
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
            newsLocation.isEmpty() || newsDateTime.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
        } else {
            if (imageUri != null) {
                uploadImageToImgur(newsId, newsTopic, newsContent, newsLocation, newsDateTime, newsCategory)
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uploadImageToImgur(newsId: String, newsTopic: String, newsContent: String,
                                   newsLocation: String, newsDateTime: String, newsCategory: String) {
        val inputStream: InputStream? = contentResolver.openInputStream(imageUri!!)
        val byteArray = inputStream!!.readBytes()
        inputStream.close()

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("image", "news.jpg",
                RequestBody.create("image/*".toMediaTypeOrNull(), byteArray))
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
                    val responseBody = response.body!!.string()
                    val imageUrl = JSONObject(responseBody).getJSONObject("data").getString("link")
                    runOnUiThread {
                        Toast.makeText(applicationContext, "Image uploaded successfully!", Toast.LENGTH_SHORT).show()
                        updateNewsToFirebase(newsId, newsTopic, newsContent, newsLocation, newsDateTime, newsCategory, imageUrl)
                    }
                }
            }
        })
    }

    private fun updateNewsToFirebase(newsId: String, newsTopic: String, newsContent: String,
                                     newsLocation: String, newsDateTime: String, newsCategory: String, newImageUrl: String?) {
        val databaseRef = FirebaseDatabase.getInstance("https://test-mad-af0eb-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference("NewsPost").child(newsId)

        databaseRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val existingImageUrl = snapshot.child("imageUrl").getValue(String::class.java)
                val updateMap = mapOf(
                    "newsId" to newsId,
                    "newsTopic" to newsTopic,
                    "newsContent" to newsContent,
                    "newsLocation" to newsLocation,
                    "newsDateTime" to newsDateTime,
                    "newsCategory" to newsCategory,
                    "imageUrl" to (newImageUrl ?: existingImageUrl) // Keep existing image if new one is not uploaded
                )
                databaseRef.updateChildren(updateMap).addOnCompleteListener { task ->
                    val message = if (task.isSuccessful) "News updated successfully" else "Failed to update news"
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    if (task.isSuccessful) clearFields()
                }
            } else {
                Toast.makeText(this, "News ID does not exist", Toast.LENGTH_SHORT).show()
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
}
