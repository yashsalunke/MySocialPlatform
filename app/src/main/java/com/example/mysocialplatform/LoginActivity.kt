package com.example.mysocialplatform

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        val auth = FirebaseAuth.getInstance()

        if(auth.currentUser != null) {
            goPostsActivity()
        }
        btnRegister.setOnClickListener{
            goRegisterActivity()
        }
        btnLogin.setOnClickListener {
            btnLogin.isEnabled =false
            progressBar.isEnabled = true
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email/Password cannot be Empty", Toast.LENGTH_SHORT).show()
                btnLogin.isEnabled = true
                return@setOnClickListener
            }
            //Firebase Authentication

            auth.signInWithEmailAndPassword(email,password).addOnCompleteListener{task ->
                progressBar.isEnabled = false
                btnLogin.isEnabled = true
                if(task.isSuccessful){
                    Toast.makeText(this,"Success!",Toast.LENGTH_SHORT).show()
                    goPostsActivity()

                }else{
                    Log.e("TAG","SignInFailed",task.exception)
                    Toast.makeText(this,"SignInFailed",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun goPostsActivity() {

        Log.i("TAG","goPostsActivity")
        val intent = Intent(this,PostsActivity::class.java)
        startActivity(intent)
        finish()
    }
    private fun goRegisterActivity() {
        Log.i("TAG","goRegisterActivity")
        val intentReg = Intent(this,RegisterActivity::class.java)
        startActivity(intentReg)

    }

}
