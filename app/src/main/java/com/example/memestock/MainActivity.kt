package com.example.memestock

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*


import org.json.JSONObject
import java.io.InputStream

import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import java.io.IOException


import kotlin.collections.HashMap
import kotlin.collections.set
import android.preference.PreferenceManager
import android.R.id.edit





class MainActivity : AppCompatActivity() {
    //var arr= arrayListOf< String>()
    var moc :Boolean=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(baseContext)
        val value = sharedPreferences.getString("username", "")
        val Tok=sharedPreferences.getString("token", "")
        if (value != ""||Tok!="") {
            val intent = Intent(this, HomePage::class.java)

            intent.putExtra(
                "Username",
                value
            ) // sends username to homepage activity and other activities

            intent.putExtra(
                "Token",
               Tok
            ) // sends Token

            startActivity(intent)
            finish()
        }
        forgot.setOnClickListener {
            forgotPassword()
        }

    }
    /**
     * * it sends request with the username of the user to get a new password
     * @param type String which is the type whether it is hot,new,top
     */
    fun forgotPassword()
    {

        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)
        val url: String = "http://18.217.163.16/api/user/ForgetPassword/${userpost.text.toString()}"
        val builder = Uri.parse(url).buildUpon()
        val params = java.util.HashMap<String, Any>()
        val jsonObject = JSONObject(params)

        // Volley post request with parameters
//        var notSuccess = true

        val request =object : JsonObjectRequest(
            Request.Method.PUT,builder.toString(),jsonObject,
            Response.Listener { response ->
                //
                var strResp = response.toString()
                val jsonObj: JSONObject = JSONObject(strResp)
                var message=jsonObj.getString("message")


                Toast.makeText(this, message ,Toast.LENGTH_LONG).show()
                finish()

            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "${error.toString()}" ,Toast.LENGTH_LONG).show()

            }) {
        }

        queue.add(request)
    }


    /**
     * Checks username and password are entered, uses mock service or connects to backend by calling GetUser Function
     */
    fun LoginClick(view:View) {
        var isUser: Boolean = false
        if (userpost.text.isBlank()) //if he didnt type anything in the username
        {
            Toast.makeText(this, "Type Username", Toast.LENGTH_SHORT).show()
        } else if (password.text.isBlank()) //if he didnt type anything in the password
        {
            Toast.makeText(this, "Type Password", Toast.LENGTH_SHORT).show()
        }
        //TODO function(username,password) if result is ok it moves on to Home page if not it retuns Toast message
        else {
            if (moc == true) {

                var json: String? = null
                try {
                    var isa: InputStream = assets.open("login.json")
                    json = isa.bufferedReader().use { it.readText() }
                    var jsonarr = JSONArray(json)
                    for (i in 0..jsonarr.length() - 1) {
                        var jsnobj = jsonarr.getJSONObject(i)
                        if (jsnobj.getString("username") == userpost.text.toString() && jsnobj.getString("password") == password.text.toString()) {
                            isUser = true
                        }
                        //                  arr.add(jsnobj.getString("name"))
                        //listarr.add(jsnobj.getString("type"))/
                    }
                    if (isUser) {
                        val SP = PreferenceManager.getDefaultSharedPreferences(baseContext)
                        val editor = SP.edit()
                        val strUserName = editor.putString("username", userpost.text.toString())
                        val strpassword = editor.putString("password", password.text.toString())
                        editor.commit()
                        Toast.makeText(this, "Login Successful", Toast.LENGTH_LONG).show()
                        val intent = Intent(this, HomePage::class.java)

                        intent.putExtra(
                            "Username",
                            userpost.text.toString()
                        ) // sends username to homepage activity and other activities

                        startActivity(intent)
                        finish()

                    } else {
                        Toast.makeText(this, "Login Failed", Toast.LENGTH_LONG).show()
                    }

                } catch (e: IOException) {
                }


            } else {
                //Todo with backend

                getUsers()

            }

        }
    }
    fun RegisterClick(view:View)
    {
        val intent=Intent(this,Register::class.java)
        startActivity(intent)
        finish()
    }


    /**
     *Connects to the backend by volley request, send the username and password and checks whether the entered user exists.
     * @return true if user is correct
     */
    fun getUsers() {

        var userName=userpost.text.toString()
        var Password=password.text.toString()
        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)
        val url: String = "http://18.217.163.16/api/user/Login"


        val builder = Uri.parse(url).buildUpon();

        val params = HashMap<String,String>()
        params["Username"] = userName
        params["Password"] = Password
        val jsonObject = JSONObject(params)

        // Volley post request with parameters
//        var notSuccess = true

        val request =object :JsonObjectRequest(Request.Method.POST,builder.toString(),jsonObject,
            Response.Listener { response ->
//                notSuccess = false;
          var strResp = response.toString()
       //           var str:String="{\n" +
        //                  "    \"receivedRequests\": [\n" +
         //                 "        \"omar12345\",\n" +
         //                 "        \"omar1234567\"\n" +
         //                 "    ]\n" +
          //                "}"
           //     val jsonObj1: JSONObject = JSONObject(str)
            //    val jsonArray: JSONArray = jsonObj1.getJSONArray("receivedRequests")
                 val jsonObj: JSONObject = JSONObject(strResp)
                 //val jsonArray=JSONArray(stringd)
                var respon =jsonObj.get("message")
                var synctoken= jsonObj.get("token")

                //if( jsonObj.get("message")=="successful login") {
                //    userCorrect = true
                //    Toast.makeText(this,"login Successful",Toast.LENGTH_LONG).show()
                //}
                val SP = PreferenceManager.getDefaultSharedPreferences(baseContext)
                val editor = SP.edit()
                val strUserName = editor.putString("username", userpost.text.toString())
                val token = editor.putString("token", synctoken.toString())
                val strpassword = editor.putString("password", password.text.toString())
                editor.commit()
                val intent = Intent(this, HomePage::class.java)

                intent.putExtra(
                    "Username",
                    userpost.text.toString()
                ) // sends username to homepage activity and other activities
                intent.putExtra(
                    "Token",
                    synctoken.toString()
                ) // sends Token

                startActivity(intent)
                finish()
            },
            Response.ErrorListener { error ->

                Toast.makeText(this, "login Failed" ,Toast.LENGTH_LONG).show()
            })
        {}

        queue.add(request)


    }
}