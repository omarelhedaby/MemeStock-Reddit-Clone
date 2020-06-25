package com.example.memestock

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_comment.*
import kotlinx.android.synthetic.main.activity_reply.*
import kotlinx.android.synthetic.main.adapter_reply_layout.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.ArrayList
import java.util.HashMap

class Reply : AppCompatActivity() {

    var username =""
    var token=""
  //  var author=""
   // var comment=""
    var commentid=""
    var replyList= arrayListOf<replyClass>()
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reply)

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        val intent = getIntent()
        username = intent.extras.get("Username").toString()
        token=intent.extras.get("Token").toString()
        //author=intent.extras.get("commentWriter").toString()
        //comment=intent.extras.get("comment").toString()
        commentid=intent.extras.get("commentid").toString()
        var listView = findViewById<ListView>(R.id.ReplyListView)


        getCommentInfo()
        //commentWriter.setText(author)
        //commentR.setText(comment)
        commentR.movementMethod = ScrollingMovementMethod()

        getReplies()
    }

    fun getCommentInfo()
    {
        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)
        val url: String = "http://18.217.163.16/api/comment/$commentid"
        val builder = Uri.parse(url).buildUpon();
        val params = HashMap<String,Any>()


        val jsonObject = JSONObject(params)
        val request =object : JsonObjectRequest(
            Request.Method.GET,builder.toString(),jsonObject,
            Response.Listener { response ->

                var strResp = response.toString()
                val jsonObj: JSONObject = JSONObject(strResp)
                var content= jsonObj.getString("content")
                var username= jsonObj.getString("username")


                commentWriter.setText(username)
                commentR.setText(content)

            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "${error.toString()}", Toast.LENGTH_SHORT).show()
            }) {

        }
        queue.add(request)

    }

    /**
     * this function refreshed the list to take the new value of the array
     * @param listView is the list
     * @property replies is the array of comments
     */
    fun RefreshList(listView: ListView)
    {
        var adapter=replyListAdapter(this,R.layout.adapter_reply_layout,replyList)
        listView.adapter=adapter

    }

    fun addReply()
    {
        val queue = Volley.newRequestQueue(this)
        val url: String = "http://18.217.163.16/api/comment/$commentid"
        val builder = Uri.parse(url).buildUpon();
        val params = HashMap<String,Any>()
        params["id"]=commentid
        params["content"] = theReply.text.toString()
        params["reply"] = true
        val jsonObject = JSONObject(params)
        // Volley post request with parameters

        val request =object : JsonObjectRequest(
            Request.Method.POST,builder.toString(),jsonObject,
            Response.Listener { response ->
                //                notSuccess = false;
                var strResp = response.toString()
                val jsonObj: JSONObject = JSONObject(strResp)
                var commentId =jsonObj.get("c_id")
                replyList.add(replyClass(username, theReply.text.toString()))
                RefreshList(ReplyListView)
                theReply.text.clear()
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "${error.toString()}", Toast.LENGTH_SHORT).show()
            }) {
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["auth"] = token

                return headers
            }

        }

        queue.add(request)

    }

    fun replyToComment(view: View)
    {

        addReply()
    }


    fun getReplies()
    {
        val queue = Volley.newRequestQueue(this)
        val url: String = "http://18.217.163.16/api/comment/all/$commentid?comment=true"
        val builder = Uri.parse(url).buildUpon();
        val params = HashMap<String,Any>()
        params["comment"]=true

        val jsonObject = JSONObject(params)

        val request =object : JsonObjectRequest(
            Request.Method.GET,builder.toString(),jsonObject,
            Response.Listener { response ->
                var strResp = response.toString()
                val jsonObj: JSONObject = JSONObject(strResp)
                val jsonArray: JSONArray = jsonObj.getJSONArray("comments")
                for(i in 0..jsonArray.length()-1)
                {
                    var jsonObj=jsonArray.getJSONObject(i)
                    replyList.add(replyClass(jsonObj.getString("username"), jsonObj.getString("content")))
                }
                RefreshList(ReplyListView)

            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "${error.toString()}", Toast.LENGTH_SHORT).show()
            }) {


        }

        queue.add(request)

    }

    inner class replyListAdapter

        (private val mContext: Context, private val mResource: Int, objects: ArrayList<replyClass>) :
        ArrayAdapter<replyClass>(mContext, mResource, objects) {
        private var lastPosition = -1

        /**
         * Holds variables in a View
         */
        private inner class ViewHolder {
            internal var Replier: TextView? = null
            internal var Reply: TextView? = null
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var convertView = convertView
            //get the persons information
            val Replier = getItem(position)!!.Author
            val Reply = getItem(position)!!.Reply


            var myView=layoutInflater.inflate(R.layout.adapter_reply_layout,null)
            //Create the person object with the information
            val myInbox = replyClass(Replier,Reply)

            //create the view result for showing the animation
            val result: View

            //ViewHolder object
            val holder: ViewHolder


            if (convertView == null) {
                val inflater = LayoutInflater.from(mContext)
                convertView = inflater.inflate(mResource, parent, false)
                holder = ViewHolder()
                holder.Replier = convertView!!.findViewById(R.id.Author) as Button
                holder.Reply = convertView!!.findViewById(R.id.Reply) as TextView
                result = convertView

                convertView.tag = holder
            } else {
                holder = convertView.tag as ViewHolder
                result = convertView
            }

            holder.Replier!!.setText(Replier)
            holder.Reply!!.setText(Reply)
            holder.Replier!!.setOnClickListener(
                View.OnClickListener
                {

                    val intent= Intent(this@Reply, OthersProfile::class.java)
                    intent.putExtra("otherUsername",holder.Replier!!.text.toString())
                    intent.putExtra("Username",username)
                    intent.putExtra("Token",token)
                    startActivity(intent)
                })

            return convertView
        }
    }


}