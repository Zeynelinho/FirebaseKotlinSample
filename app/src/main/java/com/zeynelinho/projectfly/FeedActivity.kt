package com.zeynelinho.projectfly

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.zeynelinho.projectfly.databinding.ActivityFeedBinding
import com.zeynelinho.projectfly.databinding.ActivityMainBinding

class FeedActivity : AppCompatActivity() {

    private lateinit var binding : ActivityFeedBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore
    private lateinit var postAdapter : PostAdapter
    private lateinit var postList : ArrayList<Post>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = Firebase.auth
        db = Firebase.firestore

        postList = ArrayList<Post>()

        loadData()

        postAdapter = PostAdapter(postList)
        binding.recyclerView.adapter = postAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

    }

    private fun loadData() {

        db.collection("Posts").orderBy("date",Query.Direction.DESCENDING).addSnapshotListener { value, error ->

            if (error != null) {

            }else {
                if (value != null) {
                    if (!value.isEmpty) {

                        val documents = value.documents

                        postList.clear()

                        for (document in documents) {

                            val userEmail = document.get("userEmail") as String
                            val downloadUrl = document.get("downloadUrl") as String
                            val comment = document.get("comment") as String

                            val post = Post(userEmail,downloadUrl,comment)
                            postList.add(post)
                        }
                        postAdapter.notifyDataSetChanged()

                    }
                }
            }

        }


    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {


        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.insta_menu,menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {


        if (item.itemId == R.id.add_post) {

            val intent = Intent(this@FeedActivity,UploadActivity::class.java)
            startActivity(intent)


        }else if (item.itemId == R.id.sign_out) {

            auth.signOut()
            val intent = Intent(this@FeedActivity,MainActivity::class.java)
            startActivity(intent)
            finish()

        }

        return super.onOptionsItemSelected(item)
    }



}