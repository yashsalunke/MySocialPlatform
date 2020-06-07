package com.example.mysocialplatform

import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.mysocialplatform.models.Post
import com.example.mysocialplatform.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_create_post.*

private const val TAG = "CreatePost"
private const val PICK_PHOTO_CODE = 1234
private var photoUri: Uri? = null

class CreatePost : AppCompatActivity() {

    private var signedInUser: User? = null
    private lateinit var firestoreDB: FirebaseFirestore
    private lateinit var storageReference: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)

        firestoreDB = FirebaseFirestore.getInstance()
        storageReference = FirebaseStorage.getInstance().reference

        firestoreDB.collection("users")
            .document(FirebaseAuth.getInstance().currentUser?.uid as String)
            .get()
            .addOnSuccessListener { userSnapshot ->
                signedInUser = userSnapshot.toObject(User::class.java)
                Log.i(TAG,"Signed in as $signedInUser")
            }
            .addOnFailureListener{exception->
                Log.e(TAG,"Failure in Fetching Signed in User",exception)
            }

        btnPickImage.setOnClickListener{
            Log.i(TAG,"OPen up Image Picker ON device")

            val imagePickerIntent = Intent(Intent.ACTION_GET_CONTENT)
            imagePickerIntent.type = "image/*"
            if(imagePickerIntent.resolveActivity(packageManager) != null){
                startActivityForResult(imagePickerIntent,PICK_PHOTO_CODE)
            }
        }

        btnSubmit.setOnClickListener{
            handleSUbmitButtonClick()
        }
    }

    private fun handleSUbmitButtonClick() {
        if(photoUri == null) {
            Toast.makeText(this,"Please Select Image....",Toast.LENGTH_SHORT).show()
            return
        }
        if(etDescription.text.isBlank()){
            Toast.makeText(this,"Please Enter Description.....",Toast.LENGTH_SHORT).show()
            return
        }
        if(signedInUser == null){
            Toast.makeText(this,"Please Login First.....",Toast.LENGTH_SHORT).show()
            return
        }

        btnSubmit.isEnabled = false
        val photoUploadUri = photoUri as Uri
        val photoReference = storageReference.child("images/${System.currentTimeMillis()}-photo.jpg")
        photoReference.putFile(photoUploadUri)
            .continueWithTask {photoUploadTask->
                Log.i(TAG,"uploaded bytes: ${photoUploadTask.result?.bytesTransferred}")
                photoReference.downloadUrl
            }.continueWithTask {downloadUrlTask ->
                val post = Post(
                    etDescription.text.toString(),
                    downloadUrlTask.result.toString(),
                    System.currentTimeMillis(),
                    signedInUser)

                firestoreDB.collection("posts").add(post)
            }.addOnCompleteListener { postCreationTask ->
                btnSubmit.isEnabled = true
                if(!postCreationTask.isSuccessful){
                    Log.e(TAG,"Exception during Firebase Operation : ",postCreationTask.exception)
                    Toast.makeText(this,"Post Creation Failed",Toast.LENGTH_SHORT).show()

                }
                etDescription.text.clear()
                imageView.setImageResource(0)
                Toast.makeText(this,"Post Uploaded Successfully",Toast.LENGTH_SHORT).show()
                val profileIntent = Intent(this, ProfileActivity::class.java)
                profileIntent.putExtra(EXTRA_USERNAME,signedInUser?.name)
                startActivity(profileIntent)
                finish()
            }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode ==PICK_PHOTO_CODE){
            if(resultCode == AppCompatActivity.RESULT_OK){
                photoUri = data?.data
                Log.i(TAG,"Photouri $photoUri ")
                imageView.setImageURI(photoUri)
            }else{
                Toast.makeText(this, "Image Picker Action Failed",Toast.LENGTH_SHORT).show()
            }
        }
    }
}
