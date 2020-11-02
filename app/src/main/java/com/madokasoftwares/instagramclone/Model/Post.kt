package com.madokasoftwares.instagramclone.Model

class Post {
//will use this to send data to our PostAdapter and home fragment
    private var postid: String = ""  //make sure these names are the same exactly as those in our database Posts collection
    private var postimage: String = ""
    private var description: String = ""
    private var publisher: String = ""

    constructor()
    constructor(postid: String, postimage: String, description: String, publisher: String) {
        this.postid = postid
        this.postimage = postimage
        this.description = description
        this.publisher = publisher
    }

    fun getPostid():String{
        return postid
    }
    fun getPostimage():String{
        return postimage
    }
    fun getPublisher():String{
        return publisher
    }
    fun getDescription():String{
        return description
    }


    fun setPostid(postid:String){
          this.postid = postid
      }
    fun setdescription(description:String){
        this.description =description
    }
    fun setPublisher(publisher: String){
        this.publisher= publisher
    }
    fun setPostimage(postimage: String){
        this.postimage = postimage
    }








}