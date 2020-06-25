package com.example.memestock

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.PopupMenu
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_comment.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.adapter_view_layout.*
import kotlinx.android.synthetic.main.adapter_view_layout.view.*
import kotlinx.android.synthetic.main.posts.view.*
import android.text.method.ScrollingMovementMethod
import android.view.*
import android.widget.*
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_home_page.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream


class comment : AppCompatActivity() {
    //var post=""
    var token:String=""
    var username:String=""
    var postid:String=""
    var subReddit:String=""
   // var title:String=""
   // var date:String=""
   // var otherUsername:String=""
    var Ahmed:commentClass= commentClass("1","Ahmed","hey there",0,0)
    var elrube3y:commentClass=commentClass("2","Hussein el Rube3y","hey there guys",0,0)
    var Omar:commentClass=commentClass("3","Omar","Hi my name is Omar",0,0)
    var Zack:commentClass=commentClass("4","Ahmed","yoh whats up",0,0)
    var Omda:commentClass=commentClass("5","Omda","hey there",0,0)
    var commentList=ArrayList<commentClass>()

    var mod=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment)
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        val intent=getIntent()
        // Getting extras from previous Intent
        username=intent.extras.get("Username").toString()
        token=intent.extras.get("Token").toString()
      //  post=intent.extras.get("body").toString()
        postid=intent.extras.get("postid").toString()
       // title=intent.extras.get("title").toString()
        subReddit=intent.extras.get("Subreddit").toString()
       // otherUsername=intent.extras.get("otherUsername").toString()
        //date=intent.extras.get("date").toString()

        var listView = findViewById<ListView>(R.id.CommentListView)

        // Initializing text views
        //PostText.setText(post)
        PostText.movementMethod = ScrollingMovementMethod()
        //PostTitle.setText(title)
        //PostOwner.setText(otherUsername)
        //PostDate.setText(date)
        getPostInfo()

        if (mod) {
            //Filling the list
            commentList.add(Ahmed)
            commentList.add(Omar)
            commentList.add(Omda)
            commentList.add(Zack)
            commentList.add(elrube3y)
            RefreshList(listView)
        }
        else
        {
            getCommentList()
        }
    }

    /**
     * this function refreshed the list to take the new value of the array
     * @param listView is the list
     * @property comments is the array of comments
     */
    fun RefreshList(listView:ListView)
    {
        var adapter=commentListAdapter(this,R.layout.adapter_view_layout,commentList)
        listView.adapter=adapter

    }

    fun getPostInfo()
    {
        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)
        val url: String = "http://18.217.163.16/api/sr/$subReddit/thread/$postid"
        val builder = Uri.parse(url).buildUpon();
        val params = HashMap<String,Any>()


        val jsonObject = JSONObject(params)
        val request =object : JsonObjectRequest(
            Request.Method.GET,builder.toString(),jsonObject,
            Response.Listener { response ->

                var strResp = response.toString()
                val jsonObj: JSONObject = JSONObject(strResp)
                var title= jsonObj.getString("title")
                var body= jsonObj.getString("body")
                var author=jsonObj.getString("creatorUsername")
                var postDate=jsonObj.getString("postDate")
                var postFile=jsonObj.getString("postFile")

                PostText.setText(body)
                PostText.movementMethod = ScrollingMovementMethod()
                PostTitle.setText(title)
                PostOwner.setText(author)
                PostDate.setText(postDate)

                Picasso.get().load("http://18.217.163.16/api$postFile").into(memePost)
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "${error.toString()}", Toast.LENGTH_SHORT).show()
            }) {

        }
        queue.add(request)

    }

    /**
     * this function adds the comment if there is one to the array of comments
     * @property comments is the array of comment
     * @param view
     */
    fun ReplyOnCall(view:View)
    {
        if(comment.text.toString()=="") {
            Toast.makeText(this,"please enter comment",Toast.LENGTH_LONG).show()
            return
        }
        else if(mod)
        {
            var listView = findViewById<ListView>(R.id.CommentListView)
            var newComment=commentClass(username,comment.text.toString())
            commentList.add(newComment)
            RefreshList(listView)
            comment.setText("")


        }
        else
        {
            addNewComment()
        }
    }

    /**
     * this function uses volley requests and json to get the comment list and add them to the comment class
     * @property comments is the array of comment
     */
    fun getCommentList()
    {
        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)
        val url: String = "http://18.217.163.16/api/comment/all/$postid"
        val builder = Uri.parse(url).buildUpon();
        val params = HashMap<String,Any>()
        params["comment"]=false

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
                    commentList.add(commentClass( jsonObj.getString("_id"),jsonObj.getString("username"), jsonObj.getString("content"),jsonObj.getInt("votes")))
                }
              RefreshList(CommentListView)


            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "${error.toString()}", Toast.LENGTH_SHORT).show()
            }) {

        }
        queue.add(request)
    }

    /**
     * this function uses volley requests and json to add comments to the comment list and add them to the comment class
     * @property comments is the array of comment
     */
    fun addNewComment()
    {
        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)
        val url: String = "http://18.217.163.16/api/comment/$postid"
        val builder = Uri.parse(url).buildUpon();
        val params = HashMap<String,String>()
        params["content"] = comment.text.toString()
        params["reply"] = "false"
        val jsonObject = JSONObject(params)
        // Volley post request with parameters

        val request =object : JsonObjectRequest(
            Request.Method.POST,builder.toString(),jsonObject,
            Response.Listener { response ->

                var strResp = response.toString()
                val jsonObj: JSONObject = JSONObject(strResp)
                var commentId =jsonObj.get("c_id")
                commentList.add(commentClass( commentId.toString(),username, comment.text.toString()))
                RefreshList(CommentListView)
                comment.text.clear()
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

    /**
     * this function uses volley requests and json to add an upvote to a comment
     * @param Stat the vote status, Id: the comment id
     */
    fun vote(Stat:Int,Id:String)
    {
        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)
        val url: String = "http://18.217.163.16/api/comment/vote/$Id"
        val builder = Uri.parse(url).buildUpon();
        val params = HashMap<String,Int>()
        params["direction"] = Stat

        val jsonObject = JSONObject(params)
        // Volley post request with parameters

        val request =object : JsonObjectRequest(
            Request.Method.PUT,builder.toString(),jsonObject,
            Response.Listener { response ->
                //                notSuccess = false;
                var strResp = response.toString()
                val jsonObj: JSONObject = JSONObject(strResp)

                Toast.makeText(this, "vote successful", Toast.LENGTH_SHORT).show()
                commentList.clear()
                getCommentList()

    },
            Response.ErrorListener { error ->
                Toast.makeText(this, "Already voted", Toast.LENGTH_SHORT).show()
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
     * this function uses volley requests and json to delete comments and update the comment list and remove them from the comment class
     * @property comments is the array of comment
     * @param Id of the deleted comment
     */
    fun Delete(Id:String){
        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)
        val url: String = "http://18.217.163.16/api/comment/$Id"
        val builder = Uri.parse(url).buildUpon();
        val params = HashMap<String,String>()

        val jsonObject = JSONObject(params)
        // Volley post request with parameters

        val request =object : JsonObjectRequest(
            Request.Method.DELETE,builder.toString(),jsonObject,
            Response.Listener { response ->

                var strResp = response.toString()
                val jsonObj: JSONObject = JSONObject(strResp)

                Toast.makeText(this, "Comment Deleted!", Toast.LENGTH_SHORT).show()
                commentList.clear()
                getCommentList()

    },
            Response.ErrorListener { error ->
                Toast.makeText(this, "error", Toast.LENGTH_SHORT).show()
            }) {
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["auth"] = token

                return headers
            }
        }
        queue.add(request)
    }

    inner class commentListAdapter
    /**
     * Default constructor for the PersonListAdapter
     * @param context
     * @param resource
     * @param objects
     */
        (private val mContext: Context, private val mResource: Int, objects: java.util.ArrayList<commentClass>) :
        ArrayAdapter<commentClass>(mContext, mResource, objects) {
        private var lastPosition = -1

        /**
         * Holds variables in a View
         */
        private inner class ViewHolder {
            internal var Author: Button? = null
            internal var comment: TextView? = null
            internal var Votes : TextView? = null
            internal var upVote : ImageButton? = null
            internal var downVote : ImageButton? = null
            internal var Menu : ImageButton? = null
            internal var Reply : ImageButton? = null
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var convertView = convertView
            //get the comments information
            val Id=getItem(position)!!.Id
            val Author = getItem(position)!!.Author
            val comment = getItem(position)!!.Comment
            val Votes=getItem(position)!!.Votes

            //Create the comment object with the information
            val mycomment = commentClass(Author, comment)

            //create the view result for showing the animation
            val result: View

            //ViewHolder object
            val holder: ViewHolder

            if (convertView == null) {
                val inflater = LayoutInflater.from(mContext)
                convertView = inflater.inflate(mResource, parent, false)
                holder = ViewHolder()
                holder.Author = convertView!!.findViewById(R.id.Author) as Button
                holder.comment = convertView.findViewById(R.id.Comment) as TextView
                holder.Votes = convertView.findViewById(R.id.Vote) as TextView
                holder.upVote = convertView.findViewById(R.id.upVote) as ImageButton
                holder.downVote = convertView.findViewById(R.id.downVote) as ImageButton
                holder.Menu = convertView.findViewById(R.id.Menu) as ImageButton
                holder.Reply=convertView.findViewById(R.id.Reply) as ImageButton
                registerForContextMenu(holder.Menu)
                result = convertView

                convertView.tag = holder
            } else {
                holder = convertView.tag as ViewHolder
                result = convertView
            }

            holder.Author!!.setText(Author)
            holder.comment!!.setText(comment)
            holder.comment!!.movementMethod = ScrollingMovementMethod()
            holder.Votes!!.setText(Votes.toString())

            holder.Author!!.setOnClickListener(View.OnClickListener
                {

                    val intent=Intent(this@comment, com.example.memestock.OthersProfile::class.java)
                    intent.putExtra("otherUsername",holder.Author!!.text.toString().removePrefix("u/"))
                    intent.putExtra("Username",username)

                    startActivity(intent)
                })

            holder.Reply!!.setOnClickListener(View.OnClickListener {
                val intent=Intent(this@comment, com.example.memestock.Reply::class.java)
                intent.putExtra("Token",token)
             //   intent.putExtra("commentWriter",holder.Author!!.text.toString().removePrefix("u/"))
                intent.putExtra("Username",username)
             //   intent.putExtra("comment",comment)
                intent.putExtra("commentid",Id)
                startActivity(intent)

            })

            holder.upVote!!.setOnClickListener(View.OnClickListener
            {
               vote(1,Id)
            })

            holder.downVote!!.setOnClickListener(View.OnClickListener
            {
                vote(-1,Id)
            })

            if(username.toUpperCase()!=holder.Author!!.text.toString().toUpperCase())
            {
                holder.Menu!!.visibility=View.INVISIBLE
            }

            holder.Menu!!.setOnClickListener(View.OnClickListener
            {
                var popup:PopupMenu=PopupMenu(this@comment,holder.Menu!!)

                popup.inflate(R.menu.commentmenu)
                popup.show()
                popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                    when(item.itemId) {
                        R.id.Delete ->
                            Delete(Id)
                        R.id.Edit->
                        {
                            val intent=Intent(this@comment,com.example.memestock.editComment::class.java)
                            intent.putExtra("Id",Id)
                            intent.putExtra("Token", token.toString()) // sends Token
                            startActivity(intent)
                        }
                    }
                    true
                })
            })
            return convertView
        }
    }




//    private lateinit var timer: Timer
  //  private val noDelay = 0L
   // private val everyFiveSeconds = 5000L

    //override fun onResume() {
      //  super.onResume()

       // val timerTask = object : TimerTask() {
         //   override fun run() {
           //     runOnUiThread { /* your code here */
            //        commentList.clear()
              //      getCommentList()}
            //}
        //}


        //timer = Timer()
        //timer.schedule(timerTask, noDelay, 1000)
    //}

    //override fun onPause() {
      //  super.onPause()

        //timer.cancel()
        //timer.purge()
    //}
}








