package com.example.memestock

import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import android.content.Intent
import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley


import kotlinx.android.synthetic.main.activity_register.*
import org.json.JSONArray
import java.io.*
import java.lang.Exception
import java.net.URLEncoder
import java.util.regex.Pattern

class Register : AppCompatActivity() {

    var passstat: Int = 100
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)


        password.addTextChangedListener(object : TextWatcher {
            /**
             * every time the user types his password it checks it length and displays whether it too short medium or ok
             */
            override fun afterTextChanged(s: Editable?) {

                if (password.text.toString().length < 5) {
                    passwordstat.setText("Password length is too short")
                    passstat = 1

                } else if (password.text.toString().length >= 5 && password.text.toString().length < 9) {
                    passwordstat.setText("Password length is medium")
                    passstat = 2
                } else {
                    passwordstat.setText("Password length is ok")
                    passstat = 2
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    /**
     * check if the password ocntains an uppercase letter
     * @param pass it is the string that we check if it contains upper case letter
     * @return true if it contains false if not
     *
     *
     */
    fun ContainsUpper(pass: String): String {
        for (elements in pass) {
            if (elements.isUpperCase()) {
                return "OK"
            }
        }
        //throw Exception("doesnt contain uppercase letter")
        return "NOT OKAY"
    }

    /**
     * checks if the password contains a number
     * @param pass it is the string that we check if it contains a digit or not
     * @return true of it contains , false if not
     *
     *
     */

    fun ContainsDigit(pass: String): String {
        for (elements in pass) {
            if (elements.isDigit()) {
                return "OK"
            }
        }
        //throw Exception("doesnt contain a digit")
        return "NOT OKAY"
    }

    /**
     * checks if email is valid
     * @param email  is the email string
     * @return true if it is valid
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

    fun isEmailValid(email: String): String {
        if (EMAIL_ADDRESS_PATTERN.matcher(email).matches() == true) {
            return "VALID"
        } else {
            //   throw Exception("Email is not valid")
            return "NOT VALID"
        }

    }

    /**
     * checks everytime the register button is clicked that email,username and password are filled
     * and that email is valid and password contains digit,letter and is not too short
     * if everything is ok it returns to the login page
     * @param view
     *
     *
     */

    fun RegisteronClick(view: View) {

        if (userpost.text.isBlank()) {
            Toast.makeText(this, "Type Username", Toast.LENGTH_SHORT).show()
            return
        } else if (password.text.isBlank()) {
            Toast.makeText(this, "Type Password", Toast.LENGTH_SHORT).show()
            return
        } else if (email.text.isBlank()) {
            Toast.makeText(this, "Type Email", Toast.LENGTH_SHORT).show()
            return
        }
        if (passstat == 1) {
            Toast.makeText(this, "Password is too short", Toast.LENGTH_SHORT).show()
            return
        } else if (ContainsDigit(password.text.toString()) == "NOT OKAY") {
            Toast.makeText(this, "Password must Contain Digits", Toast.LENGTH_SHORT).show()
            return
        } else if (ContainsUpper(password.text.toString()) == "NOT OKAY") {
            Toast.makeText(this, "Password must Contain Capital Letters", Toast.LENGTH_SHORT).show()
            return
        }

        if (isEmailValid(email.text.toString()) == "NOT VALID") {
            Toast.makeText(this, "Type a Valid E-mail", Toast.LENGTH_SHORT).show()
            return
        }
        //TODO send username , email and check they are not already registered if everything is Ok register


            getUsers()



    }

    fun getUsers() {
        var userCorrect:Boolean=false
        var myEmail=email.text.toString()
        var myName=userpost.text.toString()
        var myPassword=password.text.toString()
        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)
        val url: String = "http://18.217.163.16/api/user/register"


        val builder = Uri.parse(url).buildUpon();

        val params = HashMap<String,String>()
        params["Username"] = myName
        params["Password"] = myPassword
        params["Email"] = myEmail
        val jsonObject = JSONObject(params)

        // Volley post request with parameters
//        var notSuccess = true

        val request = JsonObjectRequest(
            Request.Method.POST,builder.toString(),jsonObject,
            Response.Listener { response ->
                //                notSuccess = false;
                var strResp = response.toString()
//                val jsonObj: JSONObject = JSONObject(strResp)
               // val jsonArray:  JSONArray = jsonObject.getJSONArray("items")
                //if( jsonObj.get("message")=="successful login") {
                //    userCorrect = true
                //    Toast.makeText(this,"login Successful",Toast.LENGTH_LONG).show()
                //}

                val intent = Intent(this,MainActivity::class.java)
                Toast.makeText(this, "register Success" ,Toast.LENGTH_LONG).show()
                startActivity(intent)
                finish()
            },
            Response.ErrorListener { error ->

                Toast.makeText(this, "register Failed" ,Toast.LENGTH_LONG).show()
            })


        queue.add(request)


    }
}










