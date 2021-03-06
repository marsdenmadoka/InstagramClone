package com.madokasoftwares.instagramclone.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.NonNull
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.madokasoftwares.instagramclone.Fragments.GalleryImageDetailsFragment
import com.madokasoftwares.instagramclone.Model.Comment
import com.madokasoftwares.instagramclone.Model.Post
import com.madokasoftwares.instagramclone.R
import com.squareup.picasso.Picasso

class MyGalleryAdapter(private val mContext:Context,
                       private val mPost:MutableList<Post>?,
                      // mPost:List<Post>
    )
    : RecyclerView.Adapter<MyGalleryAdapter.ViewHolder?>()
{
//our gallery code to be displayed in recycler_view_uploaded_pics in the ProfileFragment
   // private val mPost: List<Post>?=null
//    init {
//        this.mPost=mPost
//    }

    inner class ViewHolder(@NonNull itemView: View)
        :RecyclerView.ViewHolder(itemView)
    {
       var galleryImage: ImageView

        init{
            galleryImage=itemView.findViewById(R.id.gallery_image_pic)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(
            R.layout.gallery_images_layout,parent,false)
        return ViewHolder(view)


    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       val post:Post = mPost!![position]
        Picasso.get().load(post.getPostimage()).into(holder.galleryImage)  //get the posted image the store it to gallery

     holder.galleryImage.setOnClickListener {
         val editor=mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit()
         editor.putString("postId",post.getPostid())
         editor.apply()
         (mContext as FragmentActivity).getSupportFragmentManager()
             .beginTransaction()
             .replace(R.id.frame_container,GalleryImageDetailsFragment()).commit()

     }
    }

    override fun getItemCount(): Int {

        return mPost!!.size
    }
}