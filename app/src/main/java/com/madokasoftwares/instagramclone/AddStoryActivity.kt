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
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_add_post.*

class AddStoryActivity : AppCompatActivity() {
    private var myUrl = ""
    private var imageUri: Uri? = null
    private var storageStoryPicRef: StorageReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_story)


        storageStoryPicRef= FirebaseStorage.getInstance().reference.child("Story Pictures") //our folder when our pictures will be stored

        CropImage.activity()
            .setAspectRatio(9,16) //pic will on horizontal form
            .start(this@AddStoryActivity)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data!=null)
        {
            val result = CropImage.getActivityResult(data)
            imageUri = result.uri
            uploadStory()
        }else{
            Toast.makeText(this,"invalid file please select an image", Toast.LENGTH_LONG).show() //if you select something else
        }
    }

    private fun uploadStory() {
        when{
            imageUri == null -> Toast.makeText(this,"please select image first",Toast.LENGTH_LONG).show()

            else ->{

                val progressdialog= ProgressDialog(this)
                progressdialog.setMessage("Uploading your story. please wait...")
                progressdialog.show()
                val  fileRef = storageStoryPicRef!!.child( System.currentTimeMillis().toString() + ".jpg" ) //give it a random key and time so that a user can upload many pictures

                var uploadTask: StorageTask<*>
                uploadTask = fileRef.putFile(imageUri!!)
                uploadTask.continueWithTask(Continuation <UploadTask.TaskSnapshot, Task<Uri>> { task->
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
                        myUrl=downloadUrl.toString() //putting our file inside our firebase storage

                        val ref= FirebaseDatabase.getInstance().reference.child("Story")
                            .child(FirebaseAuth.getInstance().currentUser!!.uid)//where we will store our downloadurl for images in datasebase
                        val storyid = (ref.push().key).toString() //ceating a random key for every pic since our user will post many pics this is different as that one of profile pic


                        val timeEnd=System.currentTimeMillis() + 86400000 //this add an increment of one day//meaning our stories ends after one day refer more code in Homefragment
//                        val timeStart=System.currentTimeMillis()

                        val storyMap=HashMap<String,Any>()
                        storyMap  ["userid"]=FirebaseAuth.getInstance().currentUser!!.uid
                        storyMap["storyid"]=storyid
                        storyMap["timeend"]= timeEnd
                        storyMap["timestart"]=ServerValue.TIMESTAMP
                        storyMap["imageurl"]=myUrl

                        ref.child(storyid).updateChildren(storyMap) //we use the postid since there many pictures a user can upload

                        progressdialog.dismiss()

                        Toast.makeText(this,"Story uploaded successfully",Toast.LENGTH_LONG).show()
                        val intent = Intent(this@AddStoryActivity,MainActivity::class.java)
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