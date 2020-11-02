package com.madokasoftwares.instagramclone

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.madokasoftwares.instagramclone.Model.User
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_account_setting.*

class AccountSettingActivity : AppCompatActivity() {
    private lateinit var firebaseUser: FirebaseUser
     private var checker =""
    private var myUrl = ""
    private var imageUri: Uri? = null
    private var storageProfilePicRef: StorageReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_setting)

        firebaseUser=FirebaseAuth.getInstance().currentUser!!
        storageProfilePicRef= FirebaseStorage.getInstance().reference.child("Profile Pictures")

        logOut_btn.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this@AccountSettingActivity,SignInActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
        change_image_text_btn.setOnClickListener {
            checker="clicked"

            CropImage.activity()
                .setAspectRatio(1,1)
                .start(this@AccountSettingActivity)
        }
        save_info_profile_btn.setOnClickListener {

            if(checker == "clicked") //if profile image
            {
           uploadImageAndUpdateInfo()

            }else{ //if no profile image
                updateUserInfoOnly()
            }
        }

        DisplayUserInfo() //to understand how the code work practical check the xml file
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data!=null)
        {
            val result = CropImage.getActivityResult(data)
            imageUri = result.uri
            profile_image_view_profile_frag.setImageURI(imageUri)

        }
    }
    private fun updateUserInfoOnly() //update to firebase
    {
        when{
            TextUtils.isEmpty(full_name.toString())-> Toast.makeText(this,"full name is required", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(username.toString())-> Toast.makeText(this,"username is required", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(bio.toString())-> Toast.makeText(this,"About is required", Toast.LENGTH_LONG).show()
            else ->{
                val usersRef= FirebaseDatabase.getInstance().reference.child("Users")

                val userMap=HashMap<String,Any>()
                userMap["fullname"]=full_name.text.toString().toLowerCase() //remember the names of the usermap e.g userMap["fullname"] shoiuld be the same as those of signUp activity to avoid overwritting
                userMap["username"]=username.text.toString().toLowerCase()
                userMap["bio"]=bio.text.toString().toLowerCase()

                usersRef.child(firebaseUser.uid).updateChildren(userMap) //updateChildren since we are updating/changing the info in the database when is already there

                Toast.makeText(this,"Account info update successfully",Toast.LENGTH_LONG).show()
                val intent = Intent(this@AccountSettingActivity,MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }


    private fun DisplayUserInfo(){ //display to account activity.xml
        val usersRef= FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.uid)
        usersRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val user = snapshot.getValue<User>(User::class.java) //User here is our model class
                    Picasso.get().load(user !!.getImage()).placeholder(R.drawable.profile).into(profile_image_view_profile_frag)
                    username.setText(user.getUsername())
                    full_name.setText(user.getFullname())
                    bio.setText(user.getBio())

                }

            }

            override fun onCancelled(error: DatabaseError) {

            }


        })
    }


    private fun uploadImageAndUpdateInfo()
    {
        when {
            imageUri == null -> Toast.makeText(this,"please select image first",Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(full_name.toString())-> Toast.makeText(this,"full name is required", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(username.toString())-> Toast.makeText(this,"username is required", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(bio.toString())-> Toast.makeText(this,"About is required", Toast.LENGTH_LONG).show()

            else -> {
                val progressdialog=ProgressDialog(this)
                progressdialog.setTitle("Account settings")
                progressdialog.setMessage("please wait...")
                progressdialog.show()

    val  fileRef = storageProfilePicRef!!.child(firebaseUser!!.uid + ".jpg")
                var uploadTask: StorageTask<*>
                uploadTask = fileRef.putFile(imageUri!!)

               uploadTask.continueWithTask(Continuation <UploadTask.TaskSnapshot,Task<Uri>> { task->
                   if(!task.isSuccessful){ //if task is not successful
                       task.exception?.let{
                           throw it
                           progressdialog.dismiss()
                       }
                   }
                   return@Continuation fileRef.downloadUrl //if task is sucessfull get the download url

               }).addOnCompleteListener ( OnCompleteListener<Uri>{ task ->
                   if(task.isSuccessful){
                       val downloadUrl=task.result //downloading our Url and storing in to database
                       myUrl=downloadUrl.toString()//putting our file inside our firebase storage

                       val ref= FirebaseDatabase.getInstance().reference.child("Users")

                       val userMap=HashMap<String,Any>()
                       userMap["fullname"]=full_name.text.toString().toLowerCase() //remember the names of the usermap e.g userMap["fullname"] shoiuld be the same as those of signUp activity to avoid overwritting
                       userMap["username"]=username.text.toString().toLowerCase()
                       userMap["bio"]=bio.text.toString().toLowerCase()
                       userMap["image"]=myUrl

                       ref.child(firebaseUser.uid).updateChildren(userMap)

                       progressdialog.dismiss()

                       Toast.makeText(this,"Account info update successfully",Toast.LENGTH_LONG).show()
                       val intent = Intent(this@AccountSettingActivity,MainActivity::class.java)
                       startActivity(intent)
                       finish()

                   }else{
                       progressdialog.dismiss()
                   }

               } )



            }
        }
    }


}