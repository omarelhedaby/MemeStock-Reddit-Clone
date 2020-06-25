package com.example.memestock

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_messaging.*
import org.json.JSONObject

class Messaging : AppCompatActivity() {
    var username = ""
    var token=""
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messaging)
        val intent = getIntent()
        username = intent.extras.get("Username").toString()
        token=intent.extras.get("Token").toString()
    }


    fun buBack(View: View)
    {
        val intent=Intent(this, com.example.memestock.ViewMessages::class.java)
        intent.putExtra("Username",username.toString())
        startActivity(intent)
        finish()
    }

    /**
     * When Send button is clicked, checks that there is a sender, subject and message, then sends the message.
     * @param View
     */
    fun buSendMessage(View:View)
    {
        if(etTo.text.toString()=="")
        {
            Toast.makeText(this,"Please enter a username or an email",Toast.LENGTH_LONG).show()
        }
        else if(etSubject.text.toString()=="")
        {
            Toast.makeText(this,"Please enter a subject",Toast.LENGTH_LONG).show()
        }
        else if(etMessage.text.toString()=="")
        {
            Toast.makeText(this,"Please enter a message",Toast.LENGTH_LONG).show()
        }
        else
        {
            sendMessage()
        }
    }

    fun sendMessage() {

        var receiverUserName=etTo.text.toString()
        var subject=etSubject.text.toString()
        var messageBody=etMessage.text.toString()
        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)
        val url: String = "http://18.217.163.16/api/me/pm/compose"


        val builder = Uri.parse(url).buildUpon();

        val params = HashMap<String,String>()
        params["receiverUsername"] = receiverUserName
        params["subject"] = subject
        params["messageBody"] = messageBody
        val jsonObject = JSONObject(params)

        // Volley post request with parameters
//        var notSuccess = true

        val request =object : JsonObjectRequest(
            Request.Method.POST,builder.toString(),jsonObject,
            Response.Listener { response ->

                var strResp = response.toString()
                val jsonObj = JSONObject(strResp)
                val success: Boolean = jsonObj.getBoolean("success")

                if (success==true) {

                    Toast.makeText(this,"Message Sent!",Toast.LENGTH_LONG).show()

                }


            },
            Response.ErrorListener { error ->

                Toast.makeText(this, error.toString() ,Toast.LENGTH_LONG).show()
            })

        {override fun getHeaders(): Map<String, String> {
            val headers = HashMap<String, String>()
            headers["auth"] = token

            return headers
        }}


        queue.add(request)

    }

    fun buClick(view: View) {
        val buttonclicked = view as ImageButton
        when (buttonclicked.id) {
            R.id.Home -> {
                val intent = Intent(this, HomePage::class.java)
                intent.putExtra("Username", username)
                intent.putExtra("Token", token.toString()) // sends Token
                startActivity(intent)
                finish()

            }
            /*           R.id.Messaging -> {
                           val intent = Intent(this, Messaging::class.java)
                           intent.putExtra("Username", username)
                           startActivity(intent)

                       }*/
            R.id.Notification2 -> {
                val intent = Intent(this, com.example.memestock.Notification::class.java)
                intent.putExtra("Username", username)
                intent.putExtra("Token", token.toString()) // sends Token
                startActivity(intent)
                finish()

            }
            R.id.Profile -> {
                val intent = Intent(this, com.example.memestock.Profile::class.java)
                intent.putExtra("Username", username)
                intent.putExtra("Token", token.toString()) // sends Token
                startActivity(intent)
                finish()

            }
            R.id.Settings -> {
                val intent = Intent(this, com.example.memestock.Settings::class.java)
                intent.putExtra("Username", username)
                intent.putExtra("Token", token.toString()) // sends Token
                startActivity(intent)
                finish()

            }
            R.id.Post -> {
                val intent=Intent(this,com.example.memestock.post::class.java)
                intent.putExtra("Username",username)
                intent.putExtra("Token", token.toString()) // sends Token
                startActivity(intent)


            }
        }
    }
}
