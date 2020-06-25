package com.example.memestock

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_edit_thread.*
import kotlinx.android.synthetic.main.activity_profile.*
import org.json.JSONObject
import java.util.HashMap

class EditThread : AppCompatActivity() {
var username=""
    var token=""
    var spoiler=""
    var src=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_thread)
        username=intent.extras.getString("Username")
        token=intent.extras.getString("Token")
        Body.setText(intent.extras.getString("body"))
        Title.setText(intent.extras.getString("title"))
        src=intent.extras.getString("Src")
        spoiler=intent.extras.getString("spoiler")
        if(spoiler=="true")
        {
            Spoil.visibility= View.INVISIBLE
        }
        Done.setOnClickListener {
            editPost(intent.extras.getString("postid"),intent.extras.getString("subname"))
            if(src=="Homepage")
            {
                val intent=Intent(this,HomePage::class.java)
                intent.putExtra("Token",token)
                intent.putExtra("Username",username)
            }
            else
            {
                val intent=Intent(this,Subreddit::class.java)
                intent.putExtra("Token",token)
                intent.putExtra("Username",username)
            }

        }
    }

    /**
     * functiont that edits post body,title and spoiler status
     * @param id its id of the post
     * @param subname is the subreddit of the post
     * @property token is the token of user
     */
    fun editPost(id:String,subname:String)
    { // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)
        val url: String = "http://18.217.163.16/api/sr/$subname/thread/$id"
        val builder = Uri.parse(url).buildUpon()
        var spoilerString:String=""
        if(Spoil.isChecked==true)
        {
            spoilerString="true"
        }
        else
        {
            spoilerString="false"
        }
        val params = HashMap<String,Any>()
        params["title"] = Title.text.toString()
        params["threadBody"] = Body.text.toString()
        params["spoiler"]=spoilerString
        val jsonObject = JSONObject(params)

        // Volley post request with parameters
//        var notSuccess = true

        val request =object : JsonObjectRequest(
            Request.Method.PUT,builder.toString(),jsonObject,
            Response.Listener { response ->
                //
                var strResp = response.toString()
                Toast.makeText(this, "Successful" , Toast.LENGTH_LONG).show()
                finish()

            },
            Response.ErrorListener { error ->

            }) {

            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["auth"] = token

                return headers
            }
        }

        queue.add(request)
    }
}
