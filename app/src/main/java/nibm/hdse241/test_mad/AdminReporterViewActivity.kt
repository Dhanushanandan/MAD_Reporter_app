package nibm.hdse241.test_mad

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*

class AdminReporterViewActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var databaseReference: DatabaseReference
    private lateinit var reporterList: MutableList<Reporter>
    private lateinit var adapter: ReporterAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reporter_list)

        // Initialize Firebase
        FirebaseApp.initializeApp(this)
        val database = FirebaseDatabase.getInstance("https://test-mad-af0eb-default-rtdb.asia-southeast1.firebasedatabase.app/")
        databaseReference = database.reference.child("Reporters")

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        reporterList = mutableListOf()
        adapter = ReporterAdapter(reporterList)
        recyclerView.adapter = adapter

        fetchReporters()
    }

    private fun fetchReporters() {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                reporterList.clear()
                for (data in snapshot.children) {
                    val reporter = data.getValue(Reporter::class.java)
                    if (reporter != null) {
                        reporterList.add(reporter)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", "Error fetching data", error.toException())
                Toast.makeText(this@AdminReporterViewActivity, "Failed to load data", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

// Data class for Reporter
data class Reporter(val reporterId: String = "", val name: String = "", val email: String = "")

// Adapter for RecyclerView
class ReporterAdapter(private val reporterList: List<Reporter>) : RecyclerView.Adapter<ReporterAdapter.ReporterViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReporterViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_item_reporter, parent, false)
        return ReporterViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReporterViewHolder, position: Int) {
        val reporter = reporterList[position]
        holder.bind(reporter)
    }

    override fun getItemCount(): Int {
        return reporterList.size
    }

    class ReporterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val reporterId: TextView = itemView.findViewById(R.id.reporterIdTxt)
        private val name: TextView = itemView.findViewById(R.id.nameTxt)
        private val email: TextView = itemView.findViewById(R.id.emailTxt)

        fun bind(reporter: Reporter) {
            reporterId.text = "ID: ${reporter.reporterId}"
            name.text = "Name: ${reporter.name}"
            email.text = "Email: ${reporter.email}"
        }
    }
}
