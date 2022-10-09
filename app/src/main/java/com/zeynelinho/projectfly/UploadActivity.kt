package com.zeynelinho.projectfly

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.zeynelinho.projectfly.databinding.ActivityMainBinding
import com.zeynelinho.projectfly.databinding.ActivityUploadBinding
import java.util.UUID

class UploadActivity : AppCompatActivity() {

    private lateinit var binding : ActivityUploadBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var firestore : FirebaseFirestore
    private lateinit var storage : FirebaseStorage
    private lateinit var activityResultLauncher : ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher : ActivityResultLauncher<String>
    private var selectedUri : Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        registerLauncher()

        auth = Firebase.auth
        firestore = Firebase.firestore
        storage = Firebase.storage




    }

    fun upload(view : View) {

        val uuid = UUID.randomUUID()
        val randomId = "$uuid.jpg"

        val reference = storage.reference
        val imageReference = reference.child("images").child(randomId)

        if (selectedUri != null) {

            imageReference.putFile(selectedUri!!).addOnFailureListener {
                Toast.makeText(this,it.localizedMessage, Toast.LENGTH_SHORT).show()
            }.addOnSuccessListener {

                val downloadReference = storage.reference.child("images").child(randomId)

                downloadReference.downloadUrl.addOnSuccessListener {

                    val downloadUrl = it.toString()

                    if (auth.currentUser != null) {


                        val postMap = hashMapOf<String,Any>()

                        postMap["downloadUrl"] = downloadUrl
                        postMap["comment"] = binding.commentText.text.toString()
                        postMap["userEmail"] = auth.currentUser!!.email!!
                        postMap["date"] = Timestamp.now()

                        firestore.collection("Posts").add(postMap).addOnFailureListener {
                            Toast.makeText(this, it.localizedMessage, Toast.LENGTH_SHORT).show()
                        }.addOnSuccessListener {
                            finish()
                        }


                    }



                }

            }


        }

    }

    fun selectImage(view : View) {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Snackbar.make(view,"Permission needed for gallery!",Snackbar.LENGTH_INDEFINITE).setAction("Give Permission") {

                    //request permission

                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)

                }.show()


            }else {

                //request permission
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)

            }

        }else {

            //permission granted
            val intentToGallery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            activityResultLauncher.launch(intentToGallery)

        }


    }


    private fun registerLauncher() {


        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

            if (result.resultCode == RESULT_OK) {
                val resultData = result.data
                if (resultData != null) {
                    selectedUri = resultData.data
                    selectedUri.let {
                        binding.imageView.setImageURI(it)
                    }
                }
            }

        }

        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->

            if (result) {

                val intentToGallery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)

            }else {
                Toast.makeText(this@UploadActivity, "Permission needed for gallery!", Toast.LENGTH_LONG).show()
            }

        }

    }


}