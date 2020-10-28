package com.madokasoftwares.instagramclone

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_sign_in.*
import kotlinx.android.synthetic.main.activity_sign_up.*

//making this as our /launcher activity we edit our manifest file check the manifest file
class SignInActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        SignUp_link_btn.setOnClickListener {
            startActivity(Intent(this,SignUpActivity::class.java))
        }
        login_btn.setOnClickListener {
            loginUser()
        }

    }

    //when the app start since this is our launcher/launching activity it should check first if te user is signed in or not if signed direct to main activity
    override fun onStart() {
        super.onStart()

        if(FirebaseAuth.getInstance().currentUser != null)
        {
            val intent = Intent(this@SignInActivity,MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }


    private fun loginUser() {
        val email = email_login.text.toString()
        val password = password_login.text.toString()
        when {
            TextUtils.isEmpty(email) -> Toast.makeText(this, "Email is required", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(password) -> Toast.makeText(this, "password is required", Toast.LENGTH_LONG).show()
            else -> {
                val progressDialog = ProgressDialog(this@SignInActivity)
                progressDialog.setTitle("SignIn")
                progressDialog.setMessage("please wait,this may take a while.....")
                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.show()
                val mAuth:FirebaseAuth=FirebaseAuth.getInstance()
                mAuth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener {task ->
                        if(task.isSuccessful){
                            progressDialog.dismiss()
                            val intent = Intent(this@SignInActivity,MainActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            finish()
                        }else{
                            progressDialog.dismiss()
                            val message=task.exception.toString()
                            Toast.makeText(this,"Error: $message",Toast.LENGTH_LONG).show()
                            FirebaseAuth.getInstance().signOut()
                        }
                    }
            }
        }
    }

}