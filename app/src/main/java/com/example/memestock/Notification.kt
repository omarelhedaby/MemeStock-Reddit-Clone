package com.example.memestock

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageButton
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_notification.*
import kotlinx.android.synthetic.main.friendrequests.view.*
import kotlinx.android.synthetic.main.notification.view.*
import org.json.JSONArray
import org.json.JSONObject

class Notification : AppCompatActivity() {
    var str=""
    var count=0
var username=""
    var token=" "
    var defaultType:String="friendRequest"
   var friendrequests= arrayListOf<String>()
    var notification= arrayListOf<Notifications>()
    var previous :String="friendRequest"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)
        val intent =getIntent()
        username=intent.extras.get("Username").toString()
        token=intent.extras.get("Token").toString()

        if(defaultType=="friendRequest")
        {
            getrequests()
        }
        Notificate.setOnClickListener {
            if(previous=="friendRequest")
            {
                defaultType=""
                previous="Notification"
                getNotification()
            }
        }
        FriendRequests.setOnClickListener {
            defaultType=""
            if(previous=="Notification")
            {
                previous="friendRequest"
                getrequests()
            }
        }
        Refresh.setOnClickListener {

            if(previous=="friendRequest")
            {
                getrequests()
            }
            else if(previous=="Notification")
            {
                count++
                getNotification(count)
            }
        }






    }
    /**
     * this function  gets notifications of type message ,post,comment and puts it in a listView
     * @param token of the user to send the request
     *
     */
    fun getNotification(position :Int=0)
    {
        val queue = Volley.newRequestQueue(this)
        val url: String = "http://18.217.163.16/api/notif?startPosition=$position"
        val builder = Uri.parse(url).buildUpon()
        val params = HashMap<String, Any>()
        val jsonObject = JSONObject(params)
        // Volley post request with parameters
        val request =object : JsonObjectRequest(
            Request.Method.GET,builder.toString(),jsonObject,
            Response.Listener { response ->
                //                notSuccess = false;
                var strResp = response.toString()
                val jsonObj: JSONObject = JSONObject(strResp)
                val jsonArray: JSONArray = jsonObj.getJSONArray("notifications")
                if(jsonArray.length()==0)
                {
                    count=count-1
                }
                for(i in 0..jsonArray.length()-1)
                {
                    var jsonObj=jsonArray.getJSONObject(i)
                    var type=jsonObj.getString("type")
                    if(type!="friendRequest")
                    {
                        notification.add(Notifications(jsonObj.getString("_id"),jsonObj.getString("username"),jsonObj.getString("fromUser"),jsonObj.getString("type"),jsonObj.getString("message"),jsonObj.getString("sourceID"),jsonObj.getString("date"),jsonObj.getString("read")))
                    }
                }
                val adapter = MyNotificationAdapter(notification)
                listview.adapter = adapter
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
    /**
     * this function  gets notifications of type message ,post,comment and puts it in a listView
     *@param username which is the username of the user
     * @param token token of the user
     */
    fun getrequests()
    {
        friendrequests.clear()
        val queue = Volley.newRequestQueue(this)
        val url: String = "http://18.217.163.16/api//me/About/$username"
        val builder = Uri.parse(url).buildUpon()
        val jsonObject = JSONObject("{}")
        // Volley post request with parameters
        val request =object : JsonObjectRequest(
            Request.Method.GET,builder.toString(),jsonObject,
            Response.Listener { response ->
                var strResp = response.toString()
                val jsonObj: JSONObject = JSONObject(strResp)
                val recievedRequests: JSONArray = jsonObj.getJSONArray("RecReq")
                for(i in 0..recievedRequests.length()-1)
                {
                       friendrequests.add(recievedRequests[i].toString())
                }
                val adapter = MyPostsAdapter(friendrequests)
                listview.adapter = adapter
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
    inner class MyNotificationAdapter:BaseAdapter
    {
        var listrequests= arrayListOf<Notifications>()
        constructor(listrequests:ArrayList<Notifications>):super() {
            this.listrequests=listrequests
        }
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            var myView=layoutInflater.inflate(R.layout.notification,null)
            var myNotification=listrequests[position]
            if(myNotification.read=="true")
            {
                myView.setBackgroundColor(resources.getColor(R.color.white))
            }
            else
            {
               // myView.setBackgroundColor(resources.getColor(R.color.darkorange))
            }
            myView.notification.setText(myNotification.message)
            myView.postdate.setText(myNotification.date.toString().subSequence(0,10))
            myView.setOnClickListener {
                if(myNotification.type=="message")
                {
                    markRead(myNotification._id)
                    val intent=Intent(this@Notification,ViewMessages::class.java)
                    intent.putExtra("Username",username)
                    intent.putExtra("Token",token)
                    startActivity(intent)
                    finish()
                }
                else if(myNotification.type=="comment")
                {
                    markRead(myNotification._id)
                    val intent=Intent(this@Notification,Reply::class.java)
                    intent.putExtra("Username",username)
                    intent.putExtra("Token",token)
                    intent.putExtra("commentid",myNotification.sourceID.toString())
                    startActivity(intent)
                }
                else if(myNotification.type=="post")
                {
                    markRead(myNotification._id)
                    var subreddit=myNotification.sourceID.toString().split("/")[2]
                    var postid=myNotification.sourceID.toString().split("/")[4]
                    val intent=Intent(this@Notification,comment::class.java)
                    intent.putExtra("Username",username)
                    intent.putExtra("Token",token)
                    intent.putExtra("postid",postid)
                    intent.putExtra("Subreddit",subreddit)
                    startActivity(intent)
                }
            }

            return myView
        }

        override fun getItem(position: Int): Any {
            return listrequests[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return  listrequests.size
        }

    }
    inner class MyPostsAdapter : BaseAdapter
    {
        var listrequests= arrayListOf<String>()
        constructor(listrequests:ArrayList<String>):super() {
            this.listrequests=listrequests
        }
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            var myView=layoutInflater.inflate(R.layout.friendrequests,null)
            var frequest=listrequests[position]
            myView.requestuser.text=frequest

            myView.requestuser.setOnClickListener(View.OnClickListener {

                val intent=Intent(this@Notification,com.example.memestock.OthersProfile::class.java)
                intent.putExtra("otherUsername",myView.requestuser.text.toString())
                intent.putExtra("Username",username)
                intent.putExtra("Token",token)
                startActivity(intent)
            })
            myView.Accept.setOnClickListener(View.OnClickListener {

            Accept(myView.requestuser.text.toString(),true)

            })
            myView.Refuse.setOnClickListener(View.OnClickListener {
                Accept(myView.requestuser.text.toString(),false)

            })
            return myView

        }

        override fun getItem(position: Int): Any {
            return listrequests[position]

        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return  listrequests.size

        }
    }
    fun markRead(id:String)
    {
        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)
        var url="http://18.217.163.16/api/notif/read/$id"
        val builder = Uri.parse(url).buildUpon()
        val params = HashMap<String, Any>()
        val jsonObject = JSONObject(params)

        // Volley post request with parameters
//        var notSuccess = true

        val request = object : JsonObjectRequest(
            Request.Method.PUT, builder.toString(), jsonObject,
            Response.Listener { response ->
                //


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
    /**
     * this function used to Accept or Refuse a friend request
     *@param friendusername which is the username of the user we want to accept or reject
     * @param accept is a Boolean if it is true then we are accepting, if not we are rejecting
     */
    fun Accept(friendusername:String,accept:Boolean) {
        var Friendusername = friendusername
        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)
        var url=""
        var message=""
        if(accept) {
            url = "http://18.217.163.16/api/me/user/accept"
            message = "Error in Accepting Request"
        }
        else
        {
             url = "http://18.217.163.16/api/me/user/reject"
             message = "Error in Declining Request"
        }


        val builder = Uri.parse(url).buildUpon()
        val params = HashMap<String, Any>()
        params["fUsername"] = Friendusername
        val jsonObject = JSONObject(params)

        // Volley post request with parameters
//        var notSuccess = true

        val request = object : JsonObjectRequest(
            Request.Method.PUT, builder.toString(), jsonObject,
            Response.Listener { response ->
                //
                var strResp = response.toString()
                val jsonObj: JSONObject = JSONObject(strResp)
                message = jsonObj.getString("message")
                Toast.makeText(this, "$message", Toast.LENGTH_SHORT).show()
                getrequests()

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
    fun buClick(view: View)
    {
        val buttonclicked=view as ImageButton
        when(buttonclicked.id)
        {
            R.id.Home -> {
                val intent= Intent(this,com.example.memestock.HomePage::class.java)
                intent.putExtra("Username",username)
                intent.putExtra("Token",token)
                startActivity(intent)
                finish()

            }
            R.id.Messaging -> {
                val intent=Intent(this,ViewMessages::class.java)
                intent.putExtra("Username",username)
                intent.putExtra("Token",token)
                startActivity(intent)
                finish()

            }
          /*  R.id.Notification -> {
                val intent= Intent(this,Notification::class.java)
                intent.putExtra("Username",username)
                startActivity(intent)

            }*/
            R.id.Profile -> {
                val intent= Intent(this,com.example.memestock.Profile::class.java)
                intent.putExtra("Username",username)
                intent.putExtra("Token",token)
                startActivity(intent)
                finish()

            }
            R.id.Settings -> {
                val intent= Intent(this,com.example.memestock.Settings::class.java)
                intent.putExtra("Username",username)
                intent.putExtra("Token",token)
                startActivity(intent)
                finish()

            }

            R.id.Post -> {
                val intent=Intent(this,com.example.memestock.post::class.java)
                intent.putExtra("Username",username)
                intent.putExtra("Token",token)
                startActivity(intent)


            }
        }
    }
}
