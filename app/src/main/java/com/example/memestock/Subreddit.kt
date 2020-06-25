package com.example.memestock

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.PopupMenu
import android.view.View
import android.view.WindowManager
import kotlinx.android.synthetic.main.activity_subreddit.*
import android.view.ViewGroup
import android.widget.*
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.squareup.picasso.Picasso
//import kotlinx.android.synthetic.main.activity_home_page.*
//import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.posts.view.*
import org.json.JSONArray
import org.json.JSONObject


class Subreddit : AppCompatActivity() {
    var username=""
    var token=""
    var posts= arrayListOf<posts>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subreddit)
        //Rules.setText(rules)
        val intent=intent
        subname.setText(intent.extras.get("Subreddit").toString())
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        username=intent.extras.get("Username").toString()
        token=intent.extras.get("Token").toString()
        getSubInfo()
       refresh.setOnClickListener {
            posts.clear()
            getPosts(spinner3.selectedItem.toString().toLowerCase())
        }
        Follow.setOnClickListener {
            if(Follow.text.toString()=="Unsubscribe")
            {
                unfollowSubReddit()
            }
            else{
                followSubReddit()
            }
        }

        spinner3.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
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
                    getPosts("hot")
                }
                else if (position==2)
                {
                    posts.clear()
                    getPosts("new")
                }
            }

        }




        /*   Rules.setOnClickListener {

               if(Follow.text.toString()=="Edit Rules")
               {
                   getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
               }
               else
               {
                   getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
               }
           }*/
    }
    /**
     * sends a request to unfollow a subreddit
     *@property  subname the subreddit name
     * @property  token the user's token
     */
    fun unfollowSubReddit()
    {
        var subname= subname.text.toString()
        val queue = Volley.newRequestQueue(this)
        val url: String = "http://18.217.163.16/api/sr/$subname/subs"
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
                Toast.makeText(this, "Unsubscribed to Subreddit" ,Toast.LENGTH_SHORT).show()
                Follow.setText("Subscribe")
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
     * sends a request to follow a subreddit
     *@property  subname the subreddit name
     * @property  token the user's token
     */
    fun followSubReddit()
    {
        var subname= subname.text.toString()
        val queue = Volley.newRequestQueue(this)
        val url: String = "http://18.217.163.16/api/sr/$subname/subs"
        val builder = Uri.parse(url).buildUpon()
        val jsonObject = JSONObject("{}")
        // Volley post request with parameters
        val request =object : JsonObjectRequest(
            Request.Method.POST,builder.toString(),jsonObject,
            Response.Listener { response ->
                var strResp = response.toString()
                val jsonObj: JSONObject = JSONObject(strResp)
                val jsonArray: JSONArray = jsonObj.getJSONArray("subscribed_users")

                //                notSuccess = false;
                Toast.makeText(this, "Subscribe to Subreddit" ,Toast.LENGTH_SHORT).show()
                Follow.setText("Unsubscribe")
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
    /*  fun EditRules(view:View)
      {
          if(Follow.text.toString()=="Edit Rules") {
              Follow.setText("Confirm")
              getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
          }
          else
          {
              Follow.setText("Edit Rules")
              getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
          }

      }*/


    inner class MyPostsAdapter : BaseAdapter
    {
        var listposts= arrayListOf<posts>()
        constructor(listposts:ArrayList<posts>):super() {
            this.listposts=listposts
        }
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            var myView=layoutInflater.inflate(R.layout.posts,null)
            var mypost=listposts[position]
            myView.title.text=mypost.title
            myView.userpost.text=mypost.username
            Picasso.get().load("http://18.217.163.16/api${mypost.meme}").into(myView.meme)
            myView.date.text=mypost.date.subSequence(0,10)
            myView.post.text=mypost.post
            myView.subreddit.visibility=View.INVISIBLE
            // myView.votenum.setText(mypost.up.toString())
            myView.votenum.setText(mypost.votes.toString())
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
                var popup: PopupMenu = PopupMenu(this@Subreddit,myView.menu)

                popup.inflate(R.menu.commentmenu)
                popup.show()



                popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                    when(item.itemId) {
                        R.id.Delete ->
                        {
                            deletePost(mypost.postid,subname.text.toString().removePrefix("r/"))
                            posts.clear()
                            getPosts(spinner3.selectedItem.toString().toLowerCase())

                        }
                        R.id.Edit->
                        {
                            val intent=Intent(this@Subreddit,EditThread::class.java)
                            intent.putExtra("Token",token)
                            intent.putExtra("Username",username)
                            intent.putExtra("Src","Subreddit")
                            intent.putExtra("postid",mypost.postid)
                            intent.putExtra("body",mypost.post)
                            intent.putExtra("title",mypost.title)
                            intent.putExtra("spoiler",mypost.spoiler)
                            intent.putExtra("subname",subname.text.toString().removePrefix("r/"))
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

                val intent=Intent(this@Subreddit,comment::class.java)
                intent.putExtra("Token",token)
                intent.putExtra("Username",username)
                intent.putExtra("Subreddit",mypost.subreddit.toString().removePrefix("r/"))
                intent.putExtra("postid",mypost.postid)
                startActivity(intent)
            })
            myView.userpost.setOnClickListener(View.OnClickListener {

                val intent=Intent(this@Subreddit,com.example.memestock.OthersProfile::class.java)
                intent.putExtra("otherUsername",myView.userpost.text.toString().removePrefix("u/"))
                intent.putExtra("Username",username)
                intent.putExtra("Token",token)
                startActivity(intent)
            })
            /*myView.upvote.setOnClickListener(View.OnClickListener {
                mypost.up=mypost.up+1


            })
            myView.downvote.setOnClickListener(View.OnClickListener {
                mypost.down=mypost.down-1
            })*/

            myView.upvote.setOnClickListener(View.OnClickListener
            {
                vote(spinner3.selectedItem.toString().toLowerCase(),mypost.subreddit.toString().removePrefix("r/"),mypost.postid,true)
            })

            myView.downvote.setOnClickListener(View.OnClickListener
            {
                vote(spinner3.selectedItem.toString().toLowerCase(),mypost.subreddit.toString().removePrefix("r/"),mypost.postid,false)
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

    /**
     * gets posts of the subreddit of type hot,new,top
     * @param id of post
     *@param subreddit is the name of the subreddit
     * @property token the user's token
     */
    fun deletePost(id:String, subreddit:String)
    {
        val queue = Volley.newRequestQueue(this)
        val url: String = "http://18.217.163.16/api/sr/${subname.text.toString()}/thread/$id"
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

    /**
     * gets posts of the subreddit of type hot,new,top
     * @param type could be hot,top,new
     *@property subname the subreddit name
     * @property token the user's token
     */
    fun getPosts(type:String) {
        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)
        val url: String = "http://18.217.163.16/api/sr/${subname.text.toString()}/listing/$type"
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
                val jsonArray: JSONArray = jsonObj.getJSONArray("postList")

                for(i in 0..jsonArray.length()-1)
                {
                    var jsonObj=jsonArray.getJSONObject(i)
                    posts.add(posts( jsonObj.getString("_id"),"r/"+ jsonObj.getString("subredditName"),"u/" +jsonObj.getString("creatorUsername"),jsonObj.getString("postDate"),jsonObj.getString("title"),jsonObj.getString("body"),jsonObj.getString("spoiler"),jsonObj.getString("postFile"),jsonObj.getInt("votes")))

                }
                val adapter = MyPostsAdapter(posts)
                Searchlist.adapter=adapter
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
     * gets subreddit info like photo,rules and adds them to thier locations
     *@property subname the subreddit name
     * @property token the user's token
     */
    fun getSubInfo()
    {
        val queue = Volley.newRequestQueue(this)
        val url: String = "http://18.217.163.16/api/sr/${subname.text.toString()}/meta"
        val builder = Uri.parse(url).buildUpon()
        val jsonObject = JSONObject("{}")
        // Volley post request with parameters
        val request =object : JsonObjectRequest(
            Request.Method.GET,builder.toString(),jsonObject,
            Response.Listener { response ->
                var strResp = response.toString()
                val jsonObj: JSONObject = JSONObject(strResp)
                val subUsers:JSONArray=jsonObj.getJSONArray("subscribed_users")
                var rules:JSONArray=jsonObj.getJSONArray("rules")
                Rules.setText(rules[0].toString())
                Picasso.get().load("http://18.217.163.16/api${jsonObj.get("subredditFile")}").into(subpic)
                if(containJson(subUsers,username))
                {
                    Follow.setText("Unsubscribe")
                }
                else
                {
                    Follow.setText("Subscribe")
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
     * gets JSON array and checks if the String we are looking for exists in it
     *@param array Json array
     * @param username String that we are looking for
     * @return Boolean true if it exists false if it doesn't
     */
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


    /**
     * it a function that redirects the page to other pages of home,messaging ,notifications,profile,settings
     * and send user data
     * @param view
     *
     *
     *
     */
    fun buClick(view: View) {
        val buttonclicked = view as ImageButton
        when (buttonclicked.id) {
            R.id.Home -> {
                val intent = Intent(this, HomePage::class.java)
                intent.putExtra("Username",username)
                intent.putExtra("Token",token)
                startActivity(intent)

            }
            R.id.Messaging -> {
                val intent = Intent(this, com.example.memestock.ViewMessages::class.java)
                intent.putExtra("Username",username)
                intent.putExtra("Token",token)
                startActivity(intent)
                finish()

            }
            R.id.Notification2 -> {
                val intent = Intent(this, com.example.memestock.Notification::class.java)
                intent.putExtra("Username",username)
                intent.putExtra("Token",token)
                startActivity(intent)
                finish()

            }
            R.id.Profile -> {
                val intent = Intent(this, com.example.memestock.Profile::class.java)
                intent.putExtra("Username",username)
                intent.putExtra("Token",token)
                startActivity(intent)
                finish()

            }
            R.id.Settings -> {
                val intent = Intent(this, com.example.memestock.Settings::class.java)
                intent.putExtra("Username",username)
                intent.putExtra("Token",token)
                startActivity(intent)
                finish()

            }
            R.id.Post -> {
                val intent = Intent(this, post::class.java)
                intent.putExtra("Username",username)
                intent.putExtra("Token",token)
                startActivity(intent)


            }

        }
    }
}
