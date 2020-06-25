package com.example.memestock

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
import android.widget.ListView


import android.content.Intent
import android.net.Uri

import android.preference.PreferenceManager
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup

import android.widget.*
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_home_page.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.adapter_view_layout.view.*
import kotlinx.android.synthetic.main.editcomment.*
import kotlinx.android.synthetic.main.posts.*
import kotlinx.android.synthetic.main.posts.view.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream


class editComment : AppCompatActivity() {


    var token:String=""
    var Id:String=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.editcomment)
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        token=intent.extras.get("Token").toString()
        Id=intent.extras.get("Id").toString()

    }

    fun editBU(view: View)
    {
        if(edittedComment.text.toString()=="")
        {
            Toast.makeText(this,"Please enter your Comment",Toast.LENGTH_LONG).show()
        }
        else
        {
            edit()
        }


    }


    fun edit()
    {
        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)
        val url: String = "http://18.217.163.16/api/comment/$Id"
        val builder = Uri.parse(url).buildUpon();
        val params = HashMap<String,String>()
        params["content"] = edittedComment.text.toString()

        val jsonObject = JSONObject(params)
        // Volley post request with parameters
//        var notSuccess = true

        val request =object : JsonObjectRequest(
            Request.Method.PUT,builder.toString(),jsonObject,
            Response.Listener { response ->
                //                notSuccess = false;
                var strResp = response.toString()
                val jsonObj: JSONObject = JSONObject(strResp)

                Toast.makeText(this, "edited successful", Toast.LENGTH_SHORT).show()
                finish()

            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "Edit Unsuccessful", Toast.LENGTH_SHORT).show()
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