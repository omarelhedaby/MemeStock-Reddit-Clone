
package com.example.memestock

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_emailchange.*
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.util.regex.Pattern

class emailchange : AppCompatActivity() {
    var username: String = ""
    var token:String=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emailchange)
        supportActionBar!!.title = "Go Back"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        username=intent.extras.get("Username").toString()
        token=intent.extras.get("Token").toString()

    }

    /**
     * mailvalidity this is a function that check if the input from the user is valid mail format or not
     * @param string
     * @return Boolean
     */
    val EMAIL_ADDRESS_PATTERN = Pattern.compile(
        "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"
    )

    fun mailvalidity(email: String): String {
        if (EMAIL_ADDRESS_PATTERN.matcher(email).matches() == true) {
            return "VALID"
        } else {
            //   throw Exception("Email is not valid")
            return "NOT VALID"
        }

    }

    /**
     * busavemail is function called after the user press the save button to save the new email entered but it checks if the entered email is in the right format and check that enetered password is right
     * @param view
     */
    fun busavemail(view: View) {
        val buttonclicked = view as Button

        if (mailinp.text.isBlank()) {

            Toast.makeText(this, "pleas enter an email", Toast.LENGTH_LONG).show()
            return
        }
        else if (mailvalidity(mailinp.text.toString()) == "NOT VALID") {
            Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_LONG).show()
            return

        }
        updatemail()


        /* if((usernameval.text.toString()) != (password.text.toString()))
       {
           Toast.makeText(this, "Incorrect username", Toast.LENGTH_LONG).show()
           return
       }*/


    }

    fun updatemail() {
        // var userCorrect:Boolean=false
        var userName = usernameval.text.toString()
        var eMail = mailinp.text.toString()
        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)
        val url: String = "http://18.217.163.16/api/me/edit/email/$username"


        val builder = Uri.parse(url).buildUpon();

        val params = HashMap<String, String>()
        // params["Username"] = userName
        params["Email"] = eMail
        val jsonObject = JSONObject(params)


        val request = object : JsonObjectRequest(
            Request.Method.PUT, builder.toString(), jsonObject,
            Response.Listener { response ->

                var strResp = response.toString()

                val jsonObj: JSONObject = JSONObject(strResp)





                Toast.makeText(this, "Mail changed successfully", Toast.LENGTH_LONG).show()

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




