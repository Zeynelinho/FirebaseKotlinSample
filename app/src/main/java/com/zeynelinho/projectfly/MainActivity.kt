package com.zeynelinho.projectfly

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.zeynelinho.projectfly.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private lateinit var auth : FirebaseAuth



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = Firebase.auth


        if (auth.currentUser != null) {
            val intent = Intent(this,FeedActivity::class.java)
            startActivity(intent)
            finish()
        }



    }


    fun signUp(view : View) {

        val email = binding.emailText.text.toString()
        val password = binding.passwordText.text.toString()

        if (email == "" || password == "") {
            Toast.makeText(this@MainActivity, "Enter email and password!", Toast.LENGTH_SHORT).show()
        }else {
            auth.createUserWithEmailAndPassword(email,password).addOnFailureListener {
                Toast.makeText(this, it.localizedMessage, Toast.LENGTH_SHORT).show()
            }.addOnSuccessListener {

                val intent = Intent(this@MainActivity,FeedActivity::class.java)
                startActivity(intent)
                finish()

            }
        }


    }


    fun signIn(view : View) {

        val email = binding.emailText.text.toString()
        val password = binding.passwordText.text.toString()

        if (email == "" || password == "") {
            Toast.makeText(this, "Enter email and password!", Toast.LENGTH_SHORT).show()
        }else {
            auth.signInWithEmailAndPassword(email,password).addOnFailureListener {
                Toast.makeText(this, it.localizedMessage, Toast.LENGTH_SHORT).show()
            }.addOnSuccessListener {

                val intent = Intent(this,FeedActivity::class.java)
                startActivity(intent)
                finish()

            }
        }

    }




}