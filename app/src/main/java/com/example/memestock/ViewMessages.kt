package com.example.memestock

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.view.View


import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import org.json.JSONObject

import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_viewmessages.*
import kotlinx.android.synthetic.main.adapter_block_layout.*
import kotlinx.android.synthetic.main.adapter_inbox_layout.*
import kotlinx.android.synthetic.main.adapter_inbox_layout.view.*
import kotlinx.android.synthetic.main.adapter_sent_layout.*
import org.json.JSONArray


import java.util.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.set

class ViewMessages : AppCompatActivity() {
    var username = ""
    var token=""
    var inboxList= arrayListOf<inboxClass>()
    var sentList= arrayListOf<sentClass>()
    var blockList= arrayListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_viewmessages)
        val intent = getIntent()
        username = intent.extras.get("Username").toString()
        token=intent.extras.get("Token").toString()
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)

        var listView = findViewById<ListView>(R.id.inboxListView)

        getInbox()
    }

    fun buInbox(view: View)
    {
        markRead.setBackgroundColor(Color.parseColor("#000000"))
        unmarkRead.setBackgroundColor(Color.parseColor("#000000"))
        markRead.isEnabled=true
        unmarkRead.isEnabled=true
        getInbox()
    }

    fun buSent(view: View)
    {
        markRead.setBackgroundColor(0)
        unmarkRead.setBackgroundColor(0)
        markRead.isEnabled=false
        unmarkRead.isEnabled=false
        sentList.clear()
        var mine=false;
        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)
        val url: String = "http://18.217.163.16/api/me/pm?mine=$mine"


        val builder = Uri.parse(url).buildUpon();

        val params = HashMap<String,Boolean>()
        params["mine"]=mine
        val jsonObject = JSONObject(params)

        val request =object : JsonObjectRequest(
            Request.Method.GET,builder.toString(),jsonObject,
            Response.Listener { response ->
                //
                var strResp = response.toString()

                val jsonObj: JSONObject = JSONObject(strResp)
                val jsonArray: JSONArray = jsonObj.getJSONArray("messages")
                for(i in 0..jsonArray.length()-1)
                {
                    var jsonObj=jsonArray.getJSONObject(i)
                    sentList.add(sentClass(jsonObj.getString("_id"),jsonObj.getString("receiverUsername"),jsonObj.getString("subject"),jsonObj.getString("messageBody")))
                }
                RefreshsentList(inboxListView)
            },
            Response.ErrorListener { error ->

                Toast.makeText(this, "There are no sent messages" , Toast.LENGTH_LONG).show()
            }) {

            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["auth"] = token

                return headers
            }
        }

        queue.add(request)
    }

    fun buCompose(view: View)
    {
        val intent=Intent(this,com.example.memestock.Messaging::class.java)
        intent.putExtra("Username",username.toString())
        intent.putExtra("Token",token.toString())
        startActivity(intent)
        //finish()
    }

    fun RefreshinboxList(listView: ListView)
    {
        var adapter=inboxListAdapter(this,R.layout.adapter_inbox_layout,inboxList)
        listView.adapter=adapter
    }

    fun RefreshsentList(listView: ListView)
    {
        var adapter=sentListAdapter(this,R.layout.adapter_sent_layout,sentList)
        listView.adapter=adapter
    }

    fun RefreshblockList(listView: ListView)
    {
        var adapter=blockListAdapter(this,R.layout.adapter_block_layout,blockList)
        listView.adapter=adapter
    }

    fun getInbox()
    {
        inboxList.clear()
        var mine=true;
        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)
        val url: String = "http://18.217.163.16/api/me/pm?mine=$mine"


        val builder = Uri.parse(url).buildUpon();

        val params = HashMap<String,Boolean>()
        params["mine"]=mine
        val jsonObject = JSONObject(params)

        val request =object : JsonObjectRequest(
            Request.Method.GET,builder.toString(),jsonObject,
            Response.Listener { response ->
                //
                var strResp = response.toString()

                val jsonObj = JSONObject(strResp)
                val jsonArray: JSONArray = jsonObj.getJSONArray("messages")
                for(i in 0..jsonArray.length()-1)
                {
                    var jsonObj=jsonArray.getJSONObject(i)
                    inboxList.add(inboxClass(jsonObj.getString("_id"),jsonObj.getString("sender"),jsonObj.getString("subject"),jsonObj.getString("messageBody"),jsonObj.getBoolean("isRead")))
                }
                RefreshinboxList(inboxListView)
            },
            Response.ErrorListener { error ->

                Toast.makeText(this, "There are no messages in your inbox" , Toast.LENGTH_LONG).show()
            }) {

            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["auth"] = token

                return headers
            }
        }

        queue.add(request)
    }

    fun deleteMessage(id:String)
    {
        // Instantiate the RequestQueue.

        val queue = Volley.newRequestQueue(this)
        val url: String = "http://18.217.163.16/api/me/pm/delete?messageId=$id"


        val builder = Uri.parse(url).buildUpon();

        val params = HashMap<String,String>()
        params["messageId"]=id
        val jsonObject = JSONObject(params)

        val request =object : JsonObjectRequest(
            Request.Method.DELETE,builder.toString(),jsonObject,
            Response.Listener { response ->
                RefreshinboxList(inboxListView)
                RefreshsentList(inboxListView)
                Toast.makeText(this,"Message deleted!",Toast.LENGTH_LONG).show()

            },
            Response.ErrorListener { error ->
                RefreshinboxList(inboxListView)
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

    fun buBlock(view: View)
    {
        markRead.setBackgroundColor(0)
        unmarkRead.setBackgroundColor(0)
        markRead.isEnabled=false
        unmarkRead.isEnabled=false
        blockList.clear()
        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)
        val url: String = "http://18.217.163.16/api/me/pm/blocklist"


        val builder = Uri.parse(url).buildUpon();

        val params = HashMap<String,String>()
        val jsonObject = JSONObject(params)

        val request =object : JsonObjectRequest(
            Request.Method.GET,builder.toString(),jsonObject,
            Response.Listener { response ->
                //
                var strResp = response.toString()

                val jsonObj: JSONObject = JSONObject(strResp)
                val jsonArray: JSONArray = jsonObj.getJSONArray("blockList")
                for(i in 0..jsonArray.length()-1)
                {
                    var jsonObj=jsonArray.getJSONObject(i)
                    blockList.add(jsonObj.getString("blocked"))
                }
                RefreshblockList(inboxListView)
            },
            Response.ErrorListener { error ->

                Toast.makeText(this, "There are no blocked users" , Toast.LENGTH_LONG).show()
            }) {

            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["auth"] = token

                return headers
            }
        }

        queue.add(request)
    }

    fun Unblock(blocked:String)
    {
        val queue = Volley.newRequestQueue(this)
        val url: String = "http://18.217.163.16/api/me/pm/block"


        val builder = Uri.parse(url).buildUpon();

        val params = HashMap<String,Any>()
        params["blocked"]=blocked
        params["block"]=false
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

                Toast.makeText(this, "User Unblocked", Toast.LENGTH_LONG).show()
                RefreshblockList(inboxListView)
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

    fun buReadAll(view: View)
    {
        val queue = Volley.newRequestQueue(this)
        val url: String = "http://18.217.163.16/api/me/pm/markreadall"

        val builder = Uri.parse(url).buildUpon();

        val params = HashMap<String,Any>()
        params["isReadRequest"]=true
        val jsonObject = JSONObject(params)

        val request =object : JsonObjectRequest(
            Request.Method.POST,builder.toString(),jsonObject,
            Response.Listener { response ->
                /*
                var strResp = response.toString()
                val jsonObj = JSONObject(strResp)
                val success: Boolean = jsonObj.getBoolean("success")
*/
                Toast.makeText(this, "Messages mark as read!" , Toast.LENGTH_LONG).show()
                RefreshinboxList(inboxListView)

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

    fun buUnreadAll(view: View)
    {
        val queue = Volley.newRequestQueue(this)
        val url: String = "http://18.217.163.16/api/me/pm/markreadall"


        val builder = Uri.parse(url).buildUpon();

        val params = HashMap<String,Any>()
        params["isReadRequest"]=false
        val jsonObject = JSONObject(params)

        val request =object : JsonObjectRequest(
            Request.Method.POST,builder.toString(),jsonObject,
            Response.Listener { response ->
                /*var strResp = response.toString()
                val jsonObj = JSONObject(strResp)
                val success: Boolean = jsonObj.getBoolean("success")

                if (success==true) {

                    Toast.makeText(this, "Messages mark as unread!" , Toast.LENGTH_LONG).show()

                }*/
                Toast.makeText(this, "Messages mark as unread!" , Toast.LENGTH_LONG).show()

                RefreshinboxList(inboxListView)

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

    fun mark(id: String)
    {
        var isRead:Boolean=true

        val queue = Volley.newRequestQueue(this)
        val url: String = "http://18.217.163.16/api/me/pm/markread"

        val builder = Uri.parse(url).buildUpon();

        val params = HashMap<String,Any>()
        params["messageId"]=id
        params["isReadRequest"]=isRead
        val jsonObject = JSONObject(params)

        val request =object : JsonObjectRequest(
            Request.Method.PUT,builder.toString(),jsonObject,
            Response.Listener { response ->
                var strResp = response.toString()
                val jsonObj = JSONObject(strResp)
                val success: Boolean = jsonObj.getBoolean("success")

                if (success==true) {

                    Toast.makeText(this,"Message Mark as Read!",Toast.LENGTH_LONG)

                }
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

    inner class inboxListAdapter

        (private val mContext: Context, private val mResource: Int, objects: ArrayList<inboxClass>) :
        ArrayAdapter<inboxClass>(mContext, mResource, objects) {
        private var lastPosition = -1

        /**
         * Holds variables in a View
         */
        private inner class ViewHolder {
            internal var Sender: TextView? = null
            internal var Subject: TextView? = null
            internal var messageBody: TextView? = null
            internal var deleteBu: ImageButton?=null
            internal var markRead: ImageButton?=null
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var convertView = convertView
            //get the persons information
            val Sender = getItem(position)!!.Sender
            val Subject = getItem(position)!!.Subject
            val messageBody = getItem(position)!!.messageBody
            val id = getItem(position)!!.id
            val isRead=getItem(position)!!.isRead


            var myView = layoutInflater.inflate(R.layout.adapter_inbox_layout, null)
            //Create the person object with the information
            val myInbox = inboxClass(id, Sender, Subject, messageBody,isRead)

            //create the view result for showing the animation
            val result: View

            //ViewHolder object
            val holder: ViewHolder


            if (convertView == null) {
                val inflater = LayoutInflater.from(mContext)
                convertView = inflater.inflate(mResource, parent, false)
                holder = ViewHolder()
                holder.Sender = convertView!!.findViewById(R.id.Sender) as TextView
                holder.Subject = convertView.findViewById(R.id.Subject) as TextView
                holder.messageBody = convertView.findViewById(R.id.messageBody) as TextView
                holder.deleteBu = convertView!!.findViewById(R.id.deletBu) as ImageButton
                holder.markRead = convertView!!.findViewById(R.id.markAsRead) as ImageButton
                result = convertView

                convertView.tag = holder
            } else {
                holder = convertView.tag as ViewHolder
                result = convertView
            }

            /*markRead.setOnClickListener(View.OnClickListener {
                holder.Sender!!.setTypeface(holder.Sender!!.getTypeface(),Typeface.ITALIC)
            })*/
            markRead.setOnClickListener(View.OnClickListener {
                if (isRead==true)
                {
                    holder.Sender!!.setTypeface(holder.Sender!!.getTypeface(),Typeface.ITALIC)
                }
                else if(isRead==false)
                {
                    holder.Sender!!.setTypeface(holder.Sender!!.getTypeface(),Typeface.BOLD)
                }
            })
            unmarkRead.setOnClickListener(View.OnClickListener {
                if (isRead==true)
                {
                    holder.Sender!!.setTypeface(holder.Sender!!.getTypeface(),Typeface.ITALIC)
                }
                else if(isRead==false)
                {
                    holder.Sender!!.setTypeface(holder.Sender!!.getTypeface(),Typeface.BOLD)
                }
            })
            if (isRead==true)
            {
                holder.Sender!!.setTypeface(holder.Sender!!.getTypeface(),Typeface.ITALIC)
            }
            else if(isRead==false)
            {
                holder.Sender!!.setTypeface(holder.Sender!!.getTypeface(),Typeface.BOLD)
            }
            holder.Sender!!.setText(Sender)
            holder.Subject!!.setText(Subject)
            holder.messageBody!!.setText(messageBody)
            holder.messageBody!!.movementMethod = ScrollingMovementMethod()
            holder.deleteBu!!.setOnClickListener(View.OnClickListener {
                deleteMessage(id)
            })
            holder.markRead!!.setOnClickListener(View.OnClickListener {
                mark(id)
                holder.Sender!!.setTypeface(holder.Sender!!.getTypeface(),Typeface.ITALIC)
            })
            holder.Sender!!.setOnClickListener(View.OnClickListener
            {

                val intent = Intent(this@ViewMessages, com.example.memestock.OthersProfile::class.java)
                intent.putExtra("otherUsername", holder.Sender!!.text.toString())
                intent.putExtra("Username", username)
                intent.putExtra("Token", token)
                startActivity(intent)
            })

            return convertView
        }
    }

    inner class sentListAdapter

        (private val mContext: Context, private val mResource: Int, objects: ArrayList<sentClass>) :
        ArrayAdapter<sentClass>(mContext, mResource, objects) {
        private var lastPosition = -1

        /**
         * Holds variables in a View
         */
        private inner class ViewHolder {
            internal var Receiver: TextView? = null
            internal var Subject: TextView? = null
            internal var messageBody: TextView? = null
            internal var deleteBu: ImageButton?=null
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var convertView = convertView
            //get the persons information
            val Receiver = getItem(position)!!.Receiver
            val Subject = getItem(position)!!.Subject
            val messageBody=getItem(position)!!.messageBody
            val id=getItem(position)!!.id


            var myView=layoutInflater.inflate(R.layout.adapter_sent_layout,null)
            //Create the person object with the information
            val myInbox = inboxClass(id, Receiver, Subject, messageBody)

            //create the view result for showing the animation
            val result: View

            //ViewHolder object
            val holder: ViewHolder


            if (convertView == null) {
                val inflater = LayoutInflater.from(mContext)
                convertView = inflater.inflate(mResource, parent, false)
                holder = ViewHolder()
                holder.Receiver = convertView!!.findViewById(R.id.Receiver) as TextView
                holder.Subject = convertView.findViewById(R.id.SubjectS) as TextView
                holder.messageBody = convertView.findViewById(R.id.messageBodyS) as TextView
                holder.deleteBu=convertView!!.findViewById(R.id.deletBuS) as ImageButton
                result = convertView

                convertView.tag = holder
            } else {
                holder = convertView.tag as ViewHolder
                result = convertView
            }

            holder.Receiver!!.setText(Receiver)
            holder.Subject!!.setText(Subject)
            holder.messageBody!!.setText(messageBody)
            holder.messageBody!!.movementMethod = ScrollingMovementMethod()
            holder.deleteBu!!.setOnClickListener(View.OnClickListener {
                deleteMessage(id)
            })

            holder.Receiver!!.setOnClickListener(View.OnClickListener
            {

                val intent=Intent(this@ViewMessages, com.example.memestock.OthersProfile::class.java)
                intent.putExtra("otherUsername",holder.Receiver!!.text.toString())
                intent.putExtra("Username",username)
                intent.putExtra("Token",token)
                startActivity(intent)
            })

            return convertView
        }
    }

    inner class blockListAdapter

        (private val mContext: Context, private val mResource: Int, objects: ArrayList<String>) :
        ArrayAdapter<String>(mContext, mResource, objects) {
        private var lastPosition = -1

        /**
         * Holds variables in a View
         */
        private inner class ViewHolder {
            internal var Blocked: TextView? = null
            internal var Unblock: Button?=null
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var convertView = convertView
            //get the persons information
            //val Blocked = getItem(position)!!.
            val Blocked=blockList.get(position)


            var myView=layoutInflater.inflate(R.layout.adapter_block_layout,null)
            //Create the person object with the information
            //val myInbox = inboxClass(id, Receiver, Subject, messageBody)

            //create the view result for showing the animation
            val result: View

            //ViewHolder object
            val holder: ViewHolder


            if (convertView == null) {
                val inflater = LayoutInflater.from(mContext)
                convertView = inflater.inflate(mResource, parent, false)
                holder = ViewHolder()
                holder.Blocked = convertView!!.findViewById(R.id.Blocked) as TextView
                holder.Unblock=convertView!!.findViewById(R.id.buUnblock) as Button
                result = convertView

                convertView.tag = holder
            } else {
                holder = convertView.tag as ViewHolder
                result = convertView
            }

            holder.Blocked!!.setText(Blocked)
            holder.Unblock!!.setOnClickListener(View.OnClickListener {
                Unblock(Blocked)
            })

            holder.Blocked!!.setOnClickListener(View.OnClickListener
            {

                val intent=Intent(this@ViewMessages, com.example.memestock.OthersProfile::class.java)
                intent.putExtra("otherUsername",holder.Blocked!!.text.toString())
                intent.putExtra("Username",username)
                intent.putExtra("Token",token)
                startActivity(intent)
            })

            return convertView
        }
    }
}