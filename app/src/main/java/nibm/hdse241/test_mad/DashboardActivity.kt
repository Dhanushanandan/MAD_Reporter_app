package nibm.hdse241.test_mad

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DashboardActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")

    private lateinit var database: DatabaseReference
    private lateinit var newsContainer: LinearLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var categoryRadioGroup: RadioGroup
    private lateinit var categoryRadioButton: RadioButton
    private lateinit var searchtxt: EditText
    private lateinit var searchbtn: Button

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


        // Initialize Firebase Database Reference
        database =
            FirebaseDatabase.getInstance("https://test-mad-af0eb-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("NewsPost")

        // Finding the layout where news will be displayed
        newsContainer = findViewById(R.id.view1)
        progressBar = findViewById(R.id.loading2)
        categoryRadioGroup = findViewById(R.id.rg_categories)


        // Set listener for category selection
        categoryRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            categoryRadioButton = findViewById(checkedId)
            val selectedCategory = categoryRadioButton.text.toString()
            fetchNews(selectedCategory) // Fetch news based on the selected category
        }

        searchtxt = findViewById(R.id.searchtxt)
        searchbtn = findViewById(R.id.searchbtn)

        searchbtn.setOnClickListener {
            val news = searchtxt.text.toString().trim()
            if (news.isNotEmpty()) {
                searchNews(news)
            } else {
                Toast.makeText(this, "Please enter newsID or News Title to search", Toast.LENGTH_SHORT).show()
            }
        }

        // Fetch and display news
        fetchNews("All")

        //fetch latest news
        latestfetchNews()


        // collect email and pass from login
        val email = intent.getStringExtra("email")
        val password = intent.getStringExtra("password")
        val userId = intent.getStringExtra("USER_ID")
        val type = intent.getStringExtra("Type")

        val Reporterbtn: ImageView = findViewById(R.id.Reporterbtn)
        val Aprovelbtn: ImageView = findViewById(R.id.Aprovelbtn)
        val Postbtn: ImageView = findViewById(R.id.postbtn)
        val Logoutbtn: ImageView = findViewById(R.id.Logoutbtn)

        if (type == "viewer") {
            Reporterbtn.isEnabled = false
            Aprovelbtn.isEnabled = false
            Postbtn.isEnabled = false


        } else if (type == "reporter") {
            Reporterbtn.isEnabled = false
//            Aprovelbtn.isEnabled = false
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
            intent.putExtra("Type", type)
            startActivity(intent)
        }

//        val Aprovelbtn: ImageView = findViewById(R.id.Aprovelbtn)

        Aprovelbtn.setOnClickListener {
            val intent = Intent(this, AdminAprovelPostActivity::class.java)
            Log.d("ActivityLaunch", "Launching Admin_reporterActivity")
            startActivity(intent)

            intent.putExtra("Type", type)

        }

    }

    private fun fetchNews(category: String) {
        progressBar.visibility = ProgressBar.VISIBLE

        database.orderByChild("newsCategory").equalTo(category).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                progressBar.visibility = ProgressBar.GONE
                newsContainer.removeAllViews() // Clear the previous news

                for (newsSnapshot in snapshot.children) {
                    val newsTitle = newsSnapshot.child("newsTopic").getValue(String::class.java) ?: "No Title"
                    val newsContent = newsSnapshot.child("newsContent").getValue(String::class.java) ?: "No Content"
                    val newsDate = newsSnapshot.child("newsDateTime").getValue(String::class.java) ?: "No Date"
                    val newsImage = newsSnapshot.child("imageUrl").getValue(String::class.java) ?: "" // Image URL

                    // Create a new TextView for news item
                    val newsTextView = TextView(this@DashboardActivity).apply {
                        text = "$newsTitle\n$newsDate"
                        textSize = 16f
                        setPadding(16, 16, 16, 16)
                        gravity = Gravity.START
                        setTextColor(Color.WHITE)
                    }

                    // Set click listener to open NewsDetailActivity
                    newsTextView.setOnClickListener {
                        val intent = Intent(this@DashboardActivity, NewsDetailActivity::class.java)
                        intent.putExtra("newsTitle", newsTitle)
                        intent.putExtra("newsContent", newsContent)
                        intent.putExtra("newsDate", newsDate)
                        intent.putExtra("newsImage", newsImage)
                        startActivity(intent)
                    }

                    // Add the TextView to the container
                    newsContainer.addView(newsTextView)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                progressBar.visibility = ProgressBar.GONE
                Log.e("FirebaseError", "Failed to fetch news: ${error.message}")
            }
        })
    }



    private fun latestfetchNews() {
        val latestNewsContainer = findViewById<LinearLayout>(R.id.view1) // Layout where latest news will be shown
        val progressBar: ProgressBar = findViewById(R.id.loading2) // Progress bar

        // Show progress while fetching data
        progressBar.visibility = ProgressBar.VISIBLE

        val newsRef = FirebaseDatabase.getInstance("https://test-mad-af0eb-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference("NewsPost")

        // Fetch news data and order by date
        newsRef.orderByChild("newsDateTime").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                progressBar.visibility = ProgressBar.GONE // Hide progress bar once data is fetched
                latestNewsContainer.removeAllViews() // Clear any existing views

                // List to store news details
                val newsList = mutableListOf<NewsItem>()

                for (newsSnapshot in snapshot.children) {
                    val newsTitle = newsSnapshot.child("newsTopic").getValue(String::class.java) ?: "No Title"
                    val newsDate = newsSnapshot.child("newsDateTime").getValue(String::class.java) ?: "No Date"
                    val newsContent = newsSnapshot.child("newsContent").getValue(String::class.java) ?: "No Content"
                    val newsImage = newsSnapshot.child("imageUrl").getValue(String::class.java) ?: ""

                    // Add data to list
                    newsList.add(NewsItem(newsTitle, newsDate, newsContent, newsImage))
                }

                // Sort the list by date (descending order)
                newsList.sortByDescending { it.newsDate }

                // Add sorted news titles to the layout
                for (news in newsList) {
                    val newsTextView = TextView(this@DashboardActivity).apply {
                        text = "${news.newsTitle}\n${news.newsDate}"
                        textSize = 16f
                        setPadding(16, 16, 16, 16)
                        gravity = Gravity.START
                        setTextColor(Color.WHITE)
                        setTypeface(null, Typeface.BOLD)

                        // Set click listener to open NewsDetailActivity with full details
                        setOnClickListener {
                            val intent = Intent(this@DashboardActivity, NewsDetailActivity::class.java).apply {
                                putExtra("newsTitle", news.newsTitle)
                                putExtra("newsDate", news.newsDate)
                                putExtra("newsContent", news.newsContent)
                                putExtra("newsImage", news.newsImage)
                            }
                            startActivity(intent)
                        }
                    }

                    // Add each TextView to the latest news container
                    latestNewsContainer.addView(newsTextView)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                progressBar.visibility = ProgressBar.GONE
                Toast.makeText(this@DashboardActivity, "Failed to load news", Toast.LENGTH_SHORT).show()
                Log.e("FirebaseError", "Error fetching data: ${error.message}")
            }
        })
    }



    private fun searchNews(news: String) {
        progressBar.visibility = ProgressBar.VISIBLE
        newsContainer.removeAllViews() // Clear previous results

        // searching by newsID or newsTopic
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                progressBar.visibility = ProgressBar.GONE

                var found = false
                for (newsSnapshot in snapshot.children) {
                    val newsID = newsSnapshot.child("newsId").getValue(String::class.java) ?: ""
                    val newsTopic = newsSnapshot.child("newsTopic").getValue(String::class.java) ?: ""

                    Log.d("DashboardActivity", "news: $news")
                    Log.d("DashboardActivity", "newsID: $newsID")
                    Log.d("DashboardActivity", "newsTopic: $newsTopic")

                    if (newsID.contains(news,ignoreCase = true) || newsTopic.contains(news,ignoreCase = true)) {
                        val newsTitle = newsSnapshot.child("newsTopic").getValue(String::class.java) ?: "No Topic"
                        val newsDate = newsSnapshot.child("newsDateTime").getValue(String::class.java) ?: "No Date"
                        val newsContent = newsSnapshot.child("newsContent").getValue(String::class.java) ?: "No Content"
                        val newsImage = newsSnapshot.child("imageUrl").getValue(String::class.java) ?: ""

                        Log.d("DashboardActivity", "date: $newsDate")
                        Log.d("DashboardActivity", "newsID: $newsID")
                        Log.d("DashboardActivity", "newsTopic: $newsTitle")

                        val newsTextView = TextView(this@DashboardActivity).apply {
                            text = "$newsTitle\n$newsDate"
                            textSize = 16f
                            setPadding(16, 16, 16, 16)
                            gravity = Gravity.START
                            setTextColor(Color.WHITE)
                            setTypeface(null, Typeface.BOLD)

                            setOnClickListener {
                                val intent = Intent(this@DashboardActivity, NewsDetailActivity::class.java).apply {
                                    putExtra("newsTitle", newsTitle)
                                    putExtra("newsDate", newsDate)
                                    putExtra("newsContent", newsContent)
                                    putExtra("newsImage", newsImage)
                                }
                                startActivity(intent)
                            }
                        }

                        // Add matching news to the layout
                        newsContainer.addView(newsTextView)
                        found = true
                    }
                }

                if (!found) {
                    Toast.makeText(this@DashboardActivity, "No news found matching the search", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                progressBar.visibility = ProgressBar.GONE
                Toast.makeText(this@DashboardActivity, "Failed to load news", Toast.LENGTH_SHORT).show()
                Log.e("FirebaseError", "Error fetching data: ${error.message}")
            }
        })
    }





    override fun onRestart() {
        super.onRestart()
        fetchNews("All")  // Refresh news feed
        latestfetchNews()  // Refresh latest news feed
    }

    // Data class to hold news items
    data class NewsItem(val newsTitle: String, val newsDate: String, val newsContent: String, val newsImage: String){

    }

}

