package com.example.memestock

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.media.session.MediaSession
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.view.View
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_create__sub_reddit.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_profile.*
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.*
import java.util.Base64.getEncoder



class Create_SubReddit : AppCompatActivity() {
    var userName:String=""
    var token:String=""
    var bitmap: Bitmap? = null
    var encodeCode= ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create__sub_reddit)
        userName=intent.extras.get("Username").toString()
        token=intent.extras.get("Token").toString()

        Moderators.setOnClickListener { val intent=Intent(this,com.example.memestock.Search::class.java)
            intent.putExtra("Username",userName)
            intent.putExtra("Token",token)
            startActivity(intent)
            finish() }
        button.setOnClickListener(View.OnClickListener {
            checkPermission()
        })
    }

    /**
     * checks if title is not more than 120 letters
     * checks if all fields are not empty
     * and if everything is valid it creates the subreddit
     * @param view
     *
     */

    fun BUCreate (view: View)
    {
        if(srTitle.text.isBlank()||srTitle.text.length>30)
        {
            Toast.makeText(this,"Please Enter a Title",Toast.LENGTH_LONG).show()

            return
        }

        if(srRules.text.isBlank())
        {
            Toast.makeText(this,"Please Enter the Rules",Toast.LENGTH_LONG).show()
            return
        }

        sendrequest()


        finish()

    }

    fun sendrequest()
    {

        var srName=srTitle.text.toString()
        var srRules=srRules.text.toString()
        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)
        val url: String = "http://18.217.163.16/api/sr/create"


        //adding pic


        //
        val builder = Uri.parse(url).buildUpon();

        val params = HashMap<String,String>()
        //params["Username"] = userName
        params["srName"] = srName
        params["srRules"] = srRules

        params["base64image"]=encodeCode
        val jsonObject = JSONObject(params)

        // Volley post request with parameters
//        var notSuccess = true

        val request =object :JsonObjectRequest(
            Request.Method.POST,builder.toString(),jsonObject,
            Response.Listener { response ->
                //
                var strResp = response.toString()


                Toast.makeText(this, "Successful" ,Toast.LENGTH_LONG).show()

                finish()
            },
            Response.ErrorListener { error ->

                Toast.makeText(this,error.toString(),Toast.LENGTH_LONG).show()
            }) {

            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["auth"] = token

                return headers
            }
        }

        queue.add(request)


    }



    fun imageToString(bitmap: Bitmap):String {

        if (Build.VERSION.SDK_INT >= 26) {
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
            var imageBytes = byteArrayOutputStream.toByteArray()
            val encodedImage = Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray())
            return encodedImage
        }
        return ""
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
                if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
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

            insertPhoto.setImageBitmap(BitmapFactory.decodeFile(picturePath))
            insertPhoto.visibility=View.VISIBLE
            button.visibility=View.INVISIBLE
            bitmap=MediaStore.Images.Media.getBitmap(contentResolver,selectedImage)

            encodeCode=imageToString(bitmap!!)
            cursor.close()
        }

    }



}
