package com.example.memestock

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager

import kotlinx.android.synthetic.main.activity_search.*
import android.view.inputmethod.EditorInfo
import android.app.Activity
import android.content.Intent
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import android.widget.Toast
import android.widget.AdapterView
import org.json.JSONArray
import java.io.IOException
import java.io.InputStream
import java.lang.Exception


class Search : AppCompatActivity() {
    var moc: Boolean = true
    var username=""
    var subreddit = arrayListOf<String>("Egypt","Movies","fdsa","Technology","car","ai","avengers","cairo","Arduino","Android","Web","Movies","Game of Thrones","Batman","Memes","Travel","Travellia","GOT","Series")
    var users = arrayListOf<String>("omda1","omda12","omda2","omda13","omar12","Emad","mostafa3","aly", "zeina", "emad", "omda", "ahmed", "drake", "michael","youssef","omar123","SniperMeme121","ahmed2","ahmed3","ahmed4")
    var arrres = mutableListOf<String>()
    var prev = "subreddit"
    var token=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        var listView = findViewById<ListView>(R.id.Searchlist)
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        search.requestFocus()
        username=intent.getStringExtra("Username")
        token=intent.extras.get("Token").toString()
        search.setOnEditorActionListener(object :
            TextView.OnEditorActionListener { //works everytime the search button is pressed
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                    return true
                }
                return false
            }
        })


        listView.setOnItemClickListener(object : android.widget.AdapterView.OnItemClickListener {
            /**
             * if an item in the list is clicked it redirects it to the subreddit page and sends the user data with it
             */
            override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val item = listView.getItemAtPosition(position) as String
                if (prev == "subreddit") {
                    val intent = Intent(this@Search, Subreddit::class.java)
                    intent.putExtra("Username",username)
                    intent.putExtra("Subreddit", item)
                    intent.putExtra("Token",token)
                    startActivity(intent)
                } else if (prev == "user") {

                    val intent = Intent(this@Search, OthersProfile::class.java)
                    intent.putExtra("Token",token)
                    intent.putExtra("Username", username)
                    intent.putExtra("otherUsername",item)
                    startActivity(intent)
                }
            }
        })

        search.addTextChangedListener(object :
            TextWatcher { //function used to determine what to show for the user depending
            //on what is written in the search EditText

            override fun afterTextChanged(s: Editable?) {
                if (userOrSubreddit(search.text.toString()) == "subreddit") {
                    if (prev == "user") {
                        arrres.clear()
                    }
                    prev = "subreddit"
                    Search_OnChange(listView)

                } else if (userOrSubreddit(search.text.toString()) == "user") {
                    if (prev == "subreddit") {
                        arrres.clear()
                    }
                    prev = "user"
                    Search_Users(listView)
                }


            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().trim().length == 0) {
                    Searchlist.visibility = View.INVISIBLE
                }

            }
        })


    }
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            val intent = Intent(this, HomePage::class.java)
            intent.putExtra("Username",username)
            intent.putExtra("Token",token)
            startActivity(intent)
            finish()
            return false
        }
        return false
    }

    /**
     * this function  if the user is going to search for a subreddit or a user
     * @param String
     * @return String
     */
    fun userOrSubreddit(list: String): String {

        if (list.length < 2) {
            return "abort"
        } else {
            if ((list[0] == 'r'|| list[0] =='R') && list[1] == '/') {
                return "subreddit"
            } else if ((list[0] == 'u'|| list[0] =='U') && list[1] == '/') {
                return "user"
            }
        }
        return "abort"
    }

    /**
     *this function checks if an array of strings contain a certain string
     * @param arr2 array that we use to compare
     * @param s  is the string we compare with
     * @return true if answer it contains it false if else
     *
     */
    fun contains_ignore_case(

        arr2: MutableList<String>,
        s: String
    ): Boolean { //Function that checks if an array contains a string


        for (elements in arr2) {
            if (elements.toLowerCase() == (s.toLowerCase())) {
                return true
            }

        }
        return false

    }

    /**
     * checks every time the user types something and returns the subreddit that contain the typed letter
     * and will view it in the list
     * @param listview is the ListView
     *
     *
     */
    fun Search_OnChange(listview: ListView) {

        if (search.text.toString().length == 2) {
            return
        }
        /*var adpt= ArrayAdapter(this,android.R.layout.simple_list_item_1,arr)
        jason_list.adapter=adpt*/
        /*jason_list.onItemClickListener=AdapterView.OnItemClickListener{parent,view ,position , id->
        Toast.makeText(applicationContext,"Type Selected is" + listarr[position],Toast.LENGTH_LONG).show()*/


        for (sub in subreddit) {

            var count = 0
            for (index in 2..(search.length() - 1)) {
                if (sub.length >= search.length() - 2 && sub[index - 2].equals(
                        search.text.toString()[index],
                        true
                    )
                ) {
                    count = count + 1


                }

            }
            if (count != 0 && count == search.text.toString().length - 2 && !contains_ignore_case(
                    arrres,
                    sub
                )
            ) {
                arrres.add(sub)
            } else if ((count != search.length() - 2 && contains_ignore_case(
                    arrres,
                    sub
                ) || sub.length < search.length() - 2 && contains_ignore_case(arrres, sub))
            ) {
                arrres.remove(sub)
            }

        }
        val adapter =
            ArrayAdapter<String>(this@Search, android.R.layout.simple_list_item_1, arrres.toMutableList())
        listview.adapter = adapter
        Searchlist.visibility = View.VISIBLE


    }


    /**
     * This function checks if the typed user exists or not and show this user for you
     * @param Listview
     */
    fun Search_Users(listview: ListView) {

        if (search.text.toString().length == 2) {
            return
        }
        for (sub in users) {
            var count = 0
            for (index in 2..(search.length() - 1)) {
                if (sub.length >= search.length() - 2 && sub[index - 2].equals(
                        search.text.toString()[index],
                        true
                    )
                ) {
                    count = count + 1


                }

            }
            if (count != 0 && count == search.text.toString().length - 2 && !contains_ignore_case(
                    arrres,
                    sub
                )
            ) {
                arrres.add(sub)
            } else if ((count != search.length() - 2 && contains_ignore_case(
                    arrres,
                    sub
                ) || sub.length < search.length() - 2 && contains_ignore_case(arrres, sub))
            ) {
                arrres.remove(sub)
            }

        }
        val adapter =
            ArrayAdapter<String>(this@Search, android.R.layout.simple_list_item_1, arrres.toMutableList())
        listview.adapter = adapter
        Searchlist.visibility = View.VISIBLE



    }
}





