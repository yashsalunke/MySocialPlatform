package com.example.mysocialplatform

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mysocialplatform.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_register.*

private const val TAG = "CreateUser"
class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestoreDB: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        auth = FirebaseAuth.getInstance()
        firestoreDB = FirebaseFirestore.getInstance()

        btn_signUp.setOnClickListener {
            signUpUser()
        }

        btn_login.setOnClickListener {
            startActivity(Intent(this,LoginActivity::class.java))
            finish()
        }


    }

    private fun signUpUser() {

        if (tv_name.text.toString().isEmpty()) {
            tv_name.error = "Please enter email"
            tv_name.requestFocus()
            return
        }

        if (tv_username.text.toString().isEmpty()) {
            tv_username.error = "Please enter Your Name"
            tv_username.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(tv_username.text.toString()).matches()) {
            tv_username.error = "Please enter valid email"
            tv_username.requestFocus()
            return
        }

        if (tv_password.text.toString().isEmpty()) {
            tv_password.error = "Please enter password"
            tv_password.requestFocus()
            return
        }

        if (tv_age==null) {
            tv_age.error = "Please Enter Your Age"
            tv_age.requestFocus()
            return
        }

        val name = tv_name.text.toString()
        val age = tv_age.text.toString().toInt()


        auth.createUserWithEmailAndPassword(tv_username.text.toString(), tv_password.text.toString())
            .addOnCompleteListener(this){userCreationTask->
                if(userCreationTask.isSuccessful){
                    Toast.makeText(this,"User Creation Successful",Toast.LENGTH_SHORT).show()

                    val user = User(
                        name,
                        age
                    )

                    firestoreDB.collection("users")
                        .document(auth.currentUser!!.uid)
                        .set(user)
                        .addOnCompleteListener { userDataUploadTask->
                            if (userDataUploadTask.isSuccessful){

                                tv_age.text.clear()
                                tv_name.text.clear()
                                tv_password.text.clear()
                                tv_username.text.clear()

                                auth.signOut()

                                startActivity(Intent(this,LoginActivity::class.java))
                                finish()
                            }


                        }

                }
                else{
                    Toast.makeText(this, "Sign Up failed. Try again after some time.", Toast.LENGTH_SHORT).show()
                }
            }

    }

}



