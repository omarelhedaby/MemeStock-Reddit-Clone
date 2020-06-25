package com.example.memestock

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_emailchange.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_passwordchange.*
import kotlinx.android.synthetic.main.activity_settings.*
import org.json.JSONObject


class passwordchange : AppCompatActivity() {
    var username=""
    var passstat:Int=100
    var token=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_passwordchange)

        username=intent.extras.get("Username").toString()
        token=intent.extras.get("Token").toString()

        passvalidatinos()
    }

    /**
     * this function takes no parameters it just make 3 lenght of validations whether it is too short or medium or normal size
     */
    fun passvalidatinos()
    {
        passchange1.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

                if (passchange1.text.toString().length < 5) {
                    passalert.setText("Password length is too short")
                    passstat = 1

                } else if (passchange1.text.toString().length >= 5 && passchange1.text.toString().length < 9) {
                    passalert.setText("Password length is medium")
                    passstat = 2
                } else {
                    passalert.setText("Password length is ok")
                    passstat = 2
                }
                supportActionBar!!.title = "Go Back"
                supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            }


            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    /**
     * containsupper is a function to check if the entered password contains atleast one upeercase or not
     * @param string
     *@return Boolean
     *
     */
    fun containsupper(pass: String): Boolean {
        for (elements in pass) {
            if (elements.isUpperCase()) {
                return true
            }
        }
        return false
    }

    /**
     * * containsdigit is a function to check if the entered password contains atleast one digit or not
     * @param string
     *@return Boolean
     */
    fun containsdigit(pass: String): Boolean {
        for (elements in pass) {
            if (elements.isDigit()) {
                return true
            }
        }
        return false
    }

    /**
     * chekcpass makes all validations on the inputs
     * @param String,String,String
     * @return String
     */

    fun Checkpass( passchange:String,old:String):String
    {

        if(passchange.isBlank()) //if he didnt type anything in the password

        {
            //   throw Exception("password is blank")
            return "Password is blank"

        }
        //TODO function(username,password) if result is ok it moves on to Home page if not it retuns Toast message
        /*else if(rentry.isBlank())
         {
             return  "please renter the new password"
         }*/
        else if(old.isBlank())
        {
            return  "please enter your old password"
        }
        else
        {
            return "OK"

        }

    }
    /**
     * buutonsave is a function that saves the new pasword entered by the user
     * @param View
     *
     */
    fun buttonsave(view:View) {


        var check = Checkpass(oldpass.text.toString(),passchange1.text.toString())
        if (check=="Password is blank") {
            Toast.makeText(this, "Please enter the new password", Toast.LENGTH_LONG).show()

        }
        else if ( check=="please enter your old password")
        {
            Toast.makeText(this, "Please enter the old password", Toast.LENGTH_LONG).show()

        }
        else if(check=="please renter the new password")
        {
            Toast.makeText(this, "Please renter the new password", Toast.LENGTH_LONG).show()
        }
        if(passstat == 1)
        {
            Toast.makeText(this, "Passwor is too short", Toast.LENGTH_LONG).show()
            return
        }
        else if(containsupper(passchange1.text.toString()) == false) {
            Toast.makeText(this, "Password must contain uppercase", Toast.LENGTH_LONG).show()
            return
        }
        else if(containsdigit(passchange1.text.toString()) == false) {
            Toast.makeText(this, "Password must contain a number", Toast.LENGTH_LONG).show()
            return
        }
        /*else if((passchange1.text.toString()) != (renterpass.text.toString())) {
            Toast.makeText(this, "Please enter the same password", Toast.LENGTH_LONG).show()
            return
        }*/

        changepass()



    }
    fun changepass()
    {

        //var userName = usernameval.text.toString()
        var oLdpassword = oldpass.text.toString()
        var nEwpassword= passchange1.text.toString()
        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)
        val url: String = "http://18.217.163.16/api/me/edit/Password/$username"


        val builder = Uri.parse(url).buildUpon();

        val params = HashMap<String, String>()
        //  params["Username"] = userName
        params["OldPassword"] = oLdpassword
        params["NewPassword"] = nEwpassword

        val jsonObject = JSONObject(params)


        val request = object : JsonObjectRequest(
            Request.Method.PUT, builder.toString(), jsonObject,
            Response.Listener { response ->

                var strResp = response.toString()

                val jsonObj: JSONObject = JSONObject(strResp)




                //val intent = Intent(this,MainActivity::class.java)
                Toast.makeText(this, "Password changed successfully", Toast.LENGTH_LONG).show()
                //startActivity(intent)
                //finish()
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





