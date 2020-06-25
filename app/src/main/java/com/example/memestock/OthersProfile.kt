package com.example.memestock

import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.PopupMenu
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_othersprofile.*
import kotlinx.android.synthetic.main.activity_post.*
import kotlinx.android.synthetic.main.activity_subreddit.*
import kotlinx.android.synthetic.main.activity_viewmessages.*
import org.json.JSONArray
import org.json.JSONObject


class OthersProfile: AppCompatActivity() {
    var username = ""
    var otherusername = ""
    var token = ""
    var subredditname=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_othersprofile)
        username = intent.extras.get("Username").toString()
        otherusername = intent.extras.get("otherUsername").toString()
        token=intent.extras.get("Token").toString()
        //subredditname=intent.getStringExtra("srName")
        UserName.setText(intent.extras.get("otherUsername").toString())
        modoptions.setOnClickListener {
            val popupmenu = PopupMenu(this,it)
            popupmenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.modinvit -> {
                        invitemoderation()
                        true
                    }
                    R.id.unbanmod -> {
                        // TODO withbackend
                        true
                    }
                    R.id.banmod -> {// TODO withbackend
                        true
                    }
                    R.id.removemod -> {// TODO withbackend
                        true
                    }
                    else -> false

                }
            }
            popupmenu.inflate(R.menu.moderationmenu)
            popupmenu.show()
        }
        getInfo()
        getifBlocked()
        Add.setOnClickListener {
            if(Add.text.toString()=="Add")
            {
                buadd()
            }
            else if(Add.text.toString()=="Delete Request")
            {
                deleteRequest()
            }
            else if(Add.text.toString()=="User Sent Request")
            {

            }
            else {
                buunfriend()
            }
        }
        Block.setOnClickListener {
            if(Block.text.toString()=="Block")
            {
                buBlock()
            }
            else
            {
                buunBlock()
            }
        }
    }

    fun buPosts(View: View) {
        Message.text = "Posts"
    }

    fun buComments(View: View) {
        Message.text = "Comments"
    }

    fun buBlock() {
        blockuser()
    }

    fun buunBlock() {
        unblockuser()
    }
    /**
     * it is a request to delete a friend request that the user sent
     * @property otherusername which is the username of the user we want to remove the request sent to
     * @property token token of the user
     *
     */
    fun deleteRequest()
    {
        var Friendusername = otherusername
        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)
        val url: String = "http://18.217.163.16/api/me/user/removeReq"
        var message: String = "Error Adding this user as a friend"
        val builder = Uri.parse(url).buildUpon();
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
                Toast.makeText(this, "$message", Toast.LENGTH_LONG).show()
                Add.setText("Add")

            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "$error", Toast.LENGTH_LONG).show()

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
     * it is a request to add a friend request
     * @property otherusername which is the username of the user we want to add as a friend
     * @property token token of the user
     *
     */
    fun buadd() {

        var Friendusername = otherusername
        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)
        val url: String = "http://18.217.163.16/api/me/user/Add"
        var message: String = "Error Adding this user as a friend"


        val builder = Uri.parse(url).buildUpon();

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

                Toast.makeText(this, "$message", Toast.LENGTH_LONG).show()
                Add.setText("Delete Request")

            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "$error", Toast.LENGTH_LONG).show()

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
     * it is a request to unfriend a user
     * @property otherusername which is the username of the user we want to unfriend
     * @property token token of the user
     *
     */

    fun buunfriend() {
        var Friendusername = otherusername
        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)
        val url: String = "http://18.217.163.16/api/me/user/Unfriend"
        var message: String = "Error Adding this user as a friend"


        val builder = Uri.parse(url).buildUpon();

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

                Toast.makeText(this, "$message", Toast.LENGTH_LONG).show()
                Add.setText("Add")

            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "${error.toString()}", Toast.LENGTH_LONG).show()

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
     * to get info of the user and sent requests,friend,block users to specify what should the buttons do
     * @property username which is the username of the user which includes sent request ,blocked user and etx..
     * @property token token of the user
     *
     */
    fun getInfo()
    {
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
                val friends: JSONArray = jsonObj.getJSONArray("Friends")
                val sentRequests: JSONArray = jsonObj.getJSONArray("SentReq")
                val recievedRequests: JSONArray = jsonObj.getJSONArray("RecReq")
                if(containJson(friends,otherusername))
                {
                    Add.setText("Unfriend")
                }
                else if(containJson(sentRequests,otherusername))
                {
                    Add.setText("Delete Request")
                }
                else if(containJson(recievedRequests,otherusername))
                {
                    Add.setText("User Sent Request")
                }
                else
                {
                    Add.setText("Add")
                }

            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "$error" ,Toast.LENGTH_SHORT).show()
            }) {
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["auth"] = token

                return headers
            }
        }

        queue.add(request)
    }
    fun containJson(array:JSONArray,username:String):Boolean
    {
        for(i in 0.. array.length()-1)
        {
            if(array.getString(i)==username)
            {
                return true
            }
        }
        return false
    }

    fun getifBlocked()
    {
        val queue = Volley.newRequestQueue(this)
        val url: String = "http://18.217.163.16/api/me/blockedusers"
        val builder = Uri.parse(url).buildUpon()
        val jsonObject = JSONObject("{}")
        // Volley post request with parameters
        val request =object : JsonObjectRequest(
            Request.Method.GET,builder.toString(),jsonObject,
            Response.Listener { response ->
                var strResp = response.toString()
                val jsonObj: JSONObject = JSONObject(strResp)
                val jsonArray: JSONArray = jsonObj.getJSONArray("Blocked")
                if(containJson(jsonArray,otherusername))
                {
                    Block.setText("UnBlock")
                }
                else
                {
                    Block.setText("Block")
                }

            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "$error" ,Toast.LENGTH_SHORT).show()
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
     * blocks user
     * @property otherusername which is the username of the user that we want to block
     * @property token token of the user
     *
     */
    fun blockuser()
    {
        var Blockedusername = otherusername
        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)
        val url: String = "http://18.217.163.16/api/me/user/block"
        var message: String = "This user is already blocked "


        val builder = Uri.parse(url).buildUpon();

        val params = HashMap<String, Any>()
        params["blockedUser"] = Blockedusername
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

                Toast.makeText(this, "$message", Toast.LENGTH_LONG).show()

            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "$message", Toast.LENGTH_LONG).show()

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
     * blocks user
     * @property otherusername which is the username of the user that we want to unblock
     * @property token token of the user
     *
     */
    fun unblockuser()
    {
        var unBlockedusername = otherusername
        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)
        val url: String = "http://18.217.163.16/api/me/user/unblock"
        var message: String = "This user is already unblocked "


        val builder = Uri.parse(url).buildUpon();

        val params = HashMap<String, Any>()
        params["unblockedUser"] = unBlockedusername
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

                Toast.makeText(this, "$message", Toast.LENGTH_LONG).show()

            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "$message", Toast.LENGTH_LONG).show()

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
     * blocks user
     * @property otherusername which is the username of the user that we want to  message block
     * @property token token of the user
     *
     */
    fun buMessageBlock(View: View)
    {
        val queue = Volley.newRequestQueue(this)
        val url: String = "http://18.217.163.16/api/me/pm/block"


        val builder = Uri.parse(url).buildUpon();

        val params = HashMap<String,Any>()
        params["blocked"]=otherusername
        params["block"]=true
        val jsonObject = JSONObject(params)

        val request =object : JsonObjectRequest(
            Request.Method.POST,builder.toString(),jsonObject,
            Response.Listener { response ->
                /*
                                var strResp = response.toString()
                                val jsonObj = JSONObject(strResp)
                                val success: Boolean = jsonObj.getBoolean("success")

                                if (success==true) {

                                    Toast.makeText(this, "User Unblocked", Toast.LENGTH_LONG).show()

                                }
                                */

                Toast.makeText(this, "User Blocked from sending messages", Toast.LENGTH_LONG).show()
                },
            Response.ErrorListener { error ->

                Toast.makeText(this, error.toString() , Toast.LENGTH_LONG).show()
            }) {

            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["auth"] = token

                return headers
            }
        }

        queue.add(request)
    }
    fun invitemoderation()
    {
        var subreddit = subredditname
        var user= username
        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)
        val url: String = "http://18.217.163.16/api/Moderator/Invite"
        // var message: String = "This user is already unblocked "


        val builder = Uri.parse(url).buildUpon();

        val params = HashMap<String, Any>()
        params["Username"]= user
        params["SrName"] = subreddit

        val jsonObject = JSONObject(params)

        // Volley post request with parameters
//        var notSuccess = true

        val request = object : JsonObjectRequest(
            Request.Method.PUT, builder.toString(), jsonObject,
            Response.Listener { response ->
                //
                var strResp = response.toString()
                val jsonObj: JSONObject = JSONObject(strResp)

                Toast.makeText(this, "Invitation sent", Toast.LENGTH_LONG).show()

            },
            Response.ErrorListener { error ->
                Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show()

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

