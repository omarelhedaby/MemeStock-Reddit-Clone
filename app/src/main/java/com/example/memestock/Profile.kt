package com.example.memestock

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.annotation.RequiresPermission
import android.support.v4.app.ActivityCompat
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_edit.*
import kotlinx.android.synthetic.main.activity_profile.*
import javax.xml.transform.Result

class Profile : AppCompatActivity() {
    var username=""
    var token=""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        profilephoto.setOnClickListener(View.OnClickListener {
            checkPermission()
        })
        var intent =getIntent()
        username=intent.extras.get("Username").toString()
        token=intent.extras.get("Token").toString()
        val about=intent.getStringExtra("About")
        UserName.setText(username)
        val resultTV= findViewById<TextView>(R.id.About)
        resultTV.text=about

    }
    val ReadImage:Int=253
    /**
     * Check you have the permission to access the mobile storage to access photos
     */
    fun checkPermission()
    {
        if(Build.VERSION.SDK_INT>=23)
        {
            if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),ReadImage)
                return
            }
        }
        loadImage()
    }

    /**
     *User response is passed and the function is override to check the permission is granted, if so it calls the function load image.
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode)
        {
            ReadImage->{
                if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    loadImage()
                }
                else
                {
                    Toast.makeText(this,"Can't access your images",Toast.LENGTH_LONG).show()
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
    val PICK_IMAGE_CODE=123

    /**
     * uploads the image from the storage.
     */
    fun loadImage()
    {
        var intent=Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent,PICK_IMAGE_CODE)
    }


    /**
     *User response is passed and the function is override to check there is data 'Image' and then sets the profile photo.
     * @param requestCode
     * @param resultCode
     * @param data
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==PICK_IMAGE_CODE && data!=null)
        {
            val selectedImage=data.data
            val filePathColum=arrayOf(MediaStore.Images.Media.DATA)
            val cursor= contentResolver.query(selectedImage,filePathColum,null,null,null)
            cursor.moveToFirst()
            val coloumnIndex=cursor.getColumnIndex(filePathColum[0])
            val picturePath=cursor.getString(coloumnIndex)
            cursor.close()
            profilephoto.setImageBitmap(BitmapFactory.decodeFile(picturePath))
        }

    }
    /**
     * When the Edit button is clicked it opens the Edit profile page.
     * @param view
     */
    fun buEdit(view: View)
    {
        val intent=Intent(this,EditProfile::class.java)
        intent.putExtra("Username",username.toString())
        intent.putExtra("Token",token.toString())
        startActivity(intent)
        //   finish()
    }

    /**
     * When the Posts button is clicked it views the posts on the profile page.
     * @param view
     */
    fun buPosts(View:View)
    {
        textView2.text="Posts"
    }

    /**
     * When the Comments button is clicked it views the comments on the profile page.
     * @param view
     */
    fun buComments(View:View)
    {
        textView2.text="Comments"
    }

    fun buViewReports(view: View)
    {
        val intent=Intent(this,Reports::class.java)
        intent.putExtra("Username",username.toString())
        intent.putExtra("Token",token)
        startActivity(intent)
     //   finish()
    }

    /**
     * When the About button is clicked it views the bio on the profile page.
     * @param view
     */
    fun buAbout(View:View)
    {
        textView2.text="about"
    }


    fun buClick(view: View)
    {
        val buttonclicked=view as ImageButton
        when(buttonclicked.id)
        {
            R.id.Home -> {
                val intent= Intent(this,com.example.memestock.HomePage::class.java)
                intent.putExtra("Username",username)
                intent.putExtra("Token", token.toString()) // sends Token
                startActivity(intent)
                finish()

            }
            R.id.Messaging -> {
                val intent=Intent(this,ViewMessages::class.java)
                intent.putExtra("Username",username)
                intent.putExtra("Token", token.toString()) // sends Token
                startActivity(intent)
                finish()

            }
            R.id.Notification -> {
                val intent= Intent(this,com.example.memestock.Notification::class.java)
                intent.putExtra("Username",username)
                intent.putExtra("Token", token.toString()) // sends Token
                startActivity(intent)
                finish()

            }
            /* R.id.Profile -> {
                 val intent= Intent(this,Profile::class.java)
                 intent.putExtra("Username",username)
                 startActivity(intent)

             }*/
            R.id.Settings -> {
                val intent= Intent(this,com.example.memestock.Settings::class.java)
                intent.putExtra("Username",username)
                intent.putExtra("Token", token.toString()) // sends Token
                startActivity(intent)
                finish()

            }
            R.id.Post->
            {
                val intent=Intent(this,com.example.memestock.post::class.java)
                intent.putExtra("Username",username)
                intent.putExtra("Token", token.toString()) // sends Token
                startActivity(intent)
            }
        }
    }
}
