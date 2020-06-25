package com.example.memestock

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_create__sub_reddit.*
import kotlinx.android.synthetic.main.activity_post.*
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.*

class post : AppCompatActivity() {
    var userName=""
    var token=""
    var bitmap: Bitmap? = null
    var encodeCode= ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)
        userName=intent.extras.get("Username").toString()
        token=intent.extras.get("Token").toString()

        buImage.setOnClickListener(View.OnClickListener {
            checkPermission()
        })

    }

    fun ButtonClick(view: View)
    {
        val buttonclicked=view as Button
        when(buttonclicked.id) {

            R.id.Post->{
                if(TitleTextBox.text.isBlank())
                {
                    Toast.makeText(this,"Please Enter A title",Toast.LENGTH_LONG).show()
                    return
                }
                postThread()
            }
            else->finish()

        }
    }
    fun postThread()
    {
        var subreddit:String=Community.text.toString()
        var title:String=TitleTextBox.text.toString()
        var threadBody:String=DescriptionTextBox.text.toString()
        var spoilerBool:Boolean=Spoiler.isChecked
        var spoilerString:String
        if(spoilerBool)
        {
            spoilerString="true"
        }
        else
        {
            spoilerString="false"
        }
        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)
        val url: String = "http://18.217.163.16/api/sr/$subreddit/thread"
         var message:String="Error posting"
        val builder = Uri.parse(url).buildUpon()

        val params = HashMap<String,Any>()
        params["Token"] =token
        params["title"] = title
        params["threadBody"] = threadBody
        params["spoiler"]=spoilerString
        if(encodeCode!="") {
            params["base64image"] = encodeCode
        }
        val jsonObject = JSONObject(params)

        // Volley post request with parameters
//        var notSuccess = true

        val request =object : JsonObjectRequest(
            Request.Method.POST,builder.toString(),jsonObject,
            Response.Listener { response ->
                //
                var strResp = response.toString()


                Toast.makeText(this, "Successful" ,Toast.LENGTH_LONG).show()
                finish()

            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "${error.toString()}" ,Toast.LENGTH_LONG).show()

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

            postImage.setImageBitmap(BitmapFactory.decodeFile(picturePath))

            bitmap= MediaStore.Images.Media.getBitmap(contentResolver,selectedImage)

            encodeCode=imageToString(bitmap!!)
            cursor.close()
        }

    }

}
