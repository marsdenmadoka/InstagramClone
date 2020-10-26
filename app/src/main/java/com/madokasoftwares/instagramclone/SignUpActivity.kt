package com.madokasoftwares.instagramclone

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_sign_up.*


class SignUpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        SignIn_link_btn.setOnClickListener {
            startActivity(Intent(this,SignInActivity::class.java ))
        }

        signUp_btn.setOnClickListener {
            CreateAccount()
        }

    }

    private fun CreateAccount()
    {
        val fullName=fullName_signup.text.toString()
        val userName=Username_signup.text.toString()
        val email=email_signup.text.toString()
        val password=password_signup.text.toString()

        when{
            TextUtils.isEmpty(fullName)-> Toast.makeText(this,"full name is required",Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(userName)-> Toast.makeText(this,"username is required",Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(email)-> Toast.makeText(this,"Email is required",Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(password)-> Toast.makeText(this,"password is required",Toast.LENGTH_LONG).show()

        else ->{
            val progressDialog = ProgressDialog(this@SignUpActivity)
            progressDialog.setTitle("SignUp")
            progressDialog.setMessage("please wait,this may take a while.....")
            progressDialog.setCanceledOnTouchOutside(false)
            progressDialog.show()
            val mAuth:FirebaseAuth=FirebaseAuth.getInstance()
            mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener {task ->
                    if(task.isSuccessful)
                    {
                   saveUserInfo(fullName,userName,email,progressDialog)
                    }else{
                        progressDialog.dismiss()
                        val message=task.exception.toString()
                        Toast.makeText(this,"Error: $message",Toast.LENGTH_LONG).show()
                       mAuth.signOut()
                    }
                }
        }
        }


    }

    private fun saveUserInfo(fullName: String, userName: String, email: String,progressDialog: ProgressDialog)
    {
  val currentUserID=FirebaseAuth.getInstance().currentUser!!.uid
   val usersRef: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Users")

      val userMap=HashMap<String,Any>()
        userMap["uid"]=currentUserID
        userMap["fullname"]=currentUserID
        userMap["username"]=currentUserID
        userMap["email"]=currentUserID
        userMap["bio"]="hey am using Instagram"
        userMap["image"]=" "


        usersRef.child(currentUserID).setValue(userMap)
            .addOnCompleteListener { task ->
                if(task.isSuccessful)
                {
             progressDialog.dismiss()
                    Toast.makeText(this,"Account has been Created successfully",Toast.LENGTH_LONG).show()
                    val intent = Intent(this@SignUpActivity,MainActivity::class.java)
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