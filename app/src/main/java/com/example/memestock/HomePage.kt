package com.example.memestock

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.PopupMenu
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_home_page.*
import kotlinx.android.synthetic.main.posts.view.*
import org.json.JSONArray
import org.json.JSONObject
import com.squareup.picasso.Picasso


class HomePage : AppCompatActivity() {

    var posts= arrayListOf<posts>()
    var username:String=""
    var token:String=""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)

        var listView = findViewById<ListView>(R.id.Searchlist)
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        val intent =getIntent()
        username=intent.extras.get("Username").toString()
        if(intent.extras.get("Token")!=null) {
            token = intent.extras.get("Token").toString()
        }
        refresh.setOnClickListener {
            getPosts(spinner1.selectedItem.toString().toLowerCase(),posts[(posts.size-1)].postid,posts[(posts.size-1)].votes)
        }
        var post=Searchlist.getItemAtPosition(3)
        spinner1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                  //top,hot,new
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
               if(position==0)
               {
                   posts.clear()
                   getPosts("top")
               }
                else if(position==1)
               {
                   posts.clear()
                   getPosts("top")
               }
                else if (position==2)
               {
                   posts.clear()
                   getPosts("new")
               }
            }

        }
        search.setOnClickListener {
            val intent=Intent(this,com.example.memestock.Search::class.java)
            intent.putExtra("Username",username)
            intent.putExtra("Token",token)
            startActivity(intent)
            finish()
        }
    }
    inner class MyPostsAdapter :BaseAdapter
    {
        var listposts= arrayListOf<posts>()
        constructor(listposts:ArrayList<posts>):super() {
            this.listposts=listposts
        }
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            var myView=layoutInflater.inflate(R.layout.posts,null)
            var mypost=listposts[position]
            myView.title.text=mypost.title
                Picasso.get().load("http://18.217.163.16/api${mypost.meme}").into(myView.meme)
            myView.userpost.text=mypost.username
            myView.date.text=mypost.date.subSequence(0,10)
            myView.post.text=mypost.post
            myView.subreddit.text=mypost.subreddit
            //myView.meme.setImageResource(mypost.meme.toInt())
            myView.votenum.setText(mypost.votes.toString())
            if(position==listposts.size-1)
            {
                getPosts(spinner1.selectedItem.toString(),mypost.postid)
            }

            if(mypost.spoiler=="false")
            {
                myView.LinearSpoiler.visibility=View.GONE
                myView.title.visibility=View.VISIBLE
                myView.post.visibility=View.VISIBLE
                myView.meme.visibility=View.VISIBLE
            }
            else
            {
                myView.LinearSpoiler.visibility=View.VISIBLE
                myView.post.visibility=View.GONE
                myView.meme.visibility=View.GONE
            }
            if(myView.userpost.text.toString().removePrefix("u/")==username)
            {
                myView.Linearmenu.visibility=View.VISIBLE
                myView.title.visibility=View.VISIBLE
                myView.post.visibility=View.VISIBLE
                myView.meme.visibility=View.VISIBLE
                myView.LinearSpoiler.visibility=View.GONE
            }
            else
            {
                myView.Linearmenu.visibility=View.GONE
            }

            //myView.downvotenum.setText(mypost.down.toString())
            myView.menu.setOnClickListener(View.OnClickListener
            {
                var popup:PopupMenu=PopupMenu(this@HomePage,myView.menu)

                popup.inflate(R.menu.commentmenu)
                popup.show()
                popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                    when(item.itemId) {
                        R.id.Delete ->
                        {
                            deletePost(mypost.postid,mypost.subreddit.removePrefix("r/"))
                            posts.clear()
                            getPosts(spinner1.selectedItem.toString().toLowerCase())
                        }
                        R.id.Edit->
                        {
                            val intent=Intent(this@HomePage,EditThread::class.java)
                           intent.putExtra("Token",token)
                          intent.putExtra("Username",username)
                            intent.putExtra("Src","HomePage")
                            intent.putExtra("postid",mypost.postid)
                            intent.putExtra("body",mypost.post)
                            intent.putExtra("title",mypost.title)
                            intent.putExtra("spoiler",mypost.spoiler)
                            intent.putExtra("subname",mypost.subreddit.removePrefix("r/"))
                            startActivity(intent)


                        }
                    }
                    true
                })

            })
            myView.Spoiler.setOnClickListener {
                mypost.spoiler="false"
                myView.LinearSpoiler.visibility=View.GONE
                myView.title.visibility=View.VISIBLE
                myView.post.visibility=View.VISIBLE
                myView.meme.visibility=View.VISIBLE
            }
            myView.reply.setOnClickListener(View.OnClickListener {

                val intent=Intent(this@HomePage,comment::class.java)
                intent.putExtra("Token",token)
                intent.putExtra("Username",username)
                intent.putExtra("Subreddit",mypost.subreddit.toString().removePrefix("r/"))
                intent.putExtra("postid",mypost.postid)
             //   intent.putExtra("body",mypost.post)
             //   intent.putExtra("title",mypost.title)
             //   intent.putExtra("date",mypost.date)
             //   intent.putExtra("otherUsername",myView.userpost.text.toString().removePrefix("u/"))
                startActivity(intent)
            })
            myView.subreddit.setOnClickListener(View.OnClickListener {
                val intent=Intent(this@HomePage,Subreddit::class.java)
                intent.putExtra("Username",username)
                intent.putExtra("Subreddit",mypost.subreddit.toString().removePrefix("r/"))
                intent.putExtra("Token",token)
                startActivity(intent)
                finish()
            })
            myView.userpost.setOnClickListener(View.OnClickListener {

                val intent=Intent(this@HomePage,com.example.memestock.OthersProfile::class.java)
                intent.putExtra("otherUsername",myView.userpost.text.toString().removePrefix("u/"))
                intent.putExtra("Username",username)
                intent.putExtra("Token",token)
                startActivity(intent)
            })
/*            myView.upvote.setOnClickListener(View.OnClickListener {
                mypost.up=mypost.up+1


            })
            myView.downvote.setOnClickListener(View.OnClickListener {
                mypost.down=mypost.down-1
            })
*/
            myView.upvote.setOnClickListener(View.OnClickListener
            {
                vote(spinner1.selectedItem.toString().toLowerCase(),mypost.subreddit.toString().removePrefix("r/"),mypost.postid,true)
            })

            myView.downvote.setOnClickListener(View.OnClickListener
            {
                vote(spinner1.selectedItem.toString().toLowerCase(),mypost.subreddit.toString().removePrefix("r/"),mypost.postid,false)
            })


            return myView
        }

        override fun getItem(position: Int): Any {
            return listposts[position]

        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return  listposts.size

        }
    }


    fun deletePost(id:String, subreddit:String)
    {
        /**
         * * it sends request to delete a post of a certain id in a subreddit
         * @param id String which is the id of the post
         * @param subreddit which is the subreddit name
         */
        val queue = Volley.newRequestQueue(this)
        val url: String = "http://18.217.163.16/api/sr/$subreddit/thread/$id"
        val builder = Uri.parse(url).buildUpon()
        val jsonObject = JSONObject("{}")
        // Volley post request with parameters
        val request =object : JsonObjectRequest(
            Request.Method.DELETE,builder.toString(),jsonObject,
            Response.Listener { response ->
                var strResp = response.toString()
                val jsonObj: JSONObject = JSONObject(strResp)
                val jsonArray: JSONArray = jsonObj.getJSONArray("subscribed_users")

                //                notSuccess = false;
                Toast.makeText(this, "Post Deleted" ,Toast.LENGTH_SHORT).show()
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

    fun getPosts(type:String, id:String="0",votes:Int=0)
    {
        /**
         * * it sends request to get post of types hot,new,top
         * @param type String which is the type whether it is hot,new,top
         */
        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)
        val url: String = "http://18.217.163.16/api/me/listing?type=$type&_id=$id&votes=${votes}&hotindex=0"
        val builder = Uri.parse(url).buildUpon();
        val params = HashMap<String,Int>()
        val jsonObject = JSONObject(params)
        // Volley post request with parameters
//        var notSuccess = true

        val request =object : JsonObjectRequest(
            Request.Method.GET,builder.toString(),jsonObject,
            Response.Listener { response ->
                //                notSuccess = false;
                var strResp = response.toString()
                val jsonObj: JSONObject = JSONObject(strResp)
                val jsonArray: JSONArray = jsonObj.getJSONArray("posts")
                for(i in 0..jsonArray.length()-1)
                {
                    var jsonObj=jsonArray.getJSONObject(i)

                    posts.add(posts( jsonObj.getString("_id"),"r/"+ jsonObj.getString("subredditName"),"u/" +jsonObj.getString("creatorUsername"),jsonObj.getString("postDate"),jsonObj.getString("title"),jsonObj.getString("body"),jsonObj.getString("spoiler"),jsonObj.getString("postFile"),jsonObj.getInt("votes")))

                }
                    val adapter = MyPostsAdapter(posts)
                    Searchlist.adapter = adapter

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

    fun vote(type:String,sr:String,postId: String, dir:Boolean)
    {
        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)
        val url: String = "http://18.217.163.16/api/sr/$sr/thread/$postId/vote"
        val builder = Uri.parse(url).buildUpon();
        val params = java.util.HashMap<String, Boolean>()
        params["upvote"] = dir

        val jsonObject = JSONObject(params)
        // Volley post request with parameters

        val request =object : JsonObjectRequest(
            Request.Method.POST,builder.toString(),jsonObject,
            Response.Listener { response ->
                //                notSuccess = false;
                var strResp = response.toString()
                val jsonObj: JSONObject = JSONObject(strResp)

                Toast.makeText(this, "Vote successful", Toast.LENGTH_SHORT).show()
                posts.clear()
                getPosts(type)

            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "Already voted", Toast.LENGTH_SHORT).show()
            }) {
            override fun getHeaders(): Map<String, String> {
                val headers = java.util.HashMap<String, String>()
                headers["auth"] = token

                return headers
            }
        }
        queue.add(request)

    }


    fun buClick(view:View)
    {
        val buttonclicked=view as ImageButton
        when(buttonclicked.id)
        {
          /*  R.id.Home -> {
                val intent=Intent(this,HomePage::class.java)
                intent.putExtra("Username",username)
                startActivity(intent)

            }*/
            R.id.Messaging -> {
                val intent=Intent(this,ViewMessages::class.java)
                intent.putExtra("Username",username)
                intent.putExtra("Token", token.toString()) // sends Token
                startActivity(intent)
                finish()

            }
            R.id.Notification2 -> {
                val intent=Intent(this,com.example.memestock.Notification::class.java)
                intent.putExtra("Username",username)
                intent.putExtra("Token", token.toString()) // sends Token
                startActivity(intent)
                finish()

            }
            R.id.Profile -> {
                val intent=Intent(this,com.example.memestock.Profile::class.java)
                intent.putExtra("Username",username)
                intent.putExtra("Token", token.toString()) // sends Token
                startActivity(intent)
                finish()

            }
            R.id.Settings -> {
                val intent=Intent(this,com.example.memestock.Settings::class.java)
                intent.putExtra("Username",username)
                intent.putExtra("Token", token.toString()) // sends Token
                startActivity(intent)
                finish()

            }
            R.id.Post -> {
                val intent=Intent(this,com.example.memestock.post::class.java)
                intent.putExtra("Username",username)
                intent.putExtra("Token", token.toString()) // sends Token
                startActivity(intent)
                finish()


            }
            R.id.AddNewReddit->{
                val intent=Intent(this,com.example.memestock.Create_SubReddit::class.java)
                intent.putExtra("Username",username)
                intent.putExtra(
                    "Token",
                   token
                ) // sends Token
                startActivity(intent)
            }

        }
    }
}
