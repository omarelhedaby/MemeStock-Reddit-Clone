package com.example.memestock

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import  android.widget.Button
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_edit.*
import kotlinx.android.synthetic.main.activity_messaging.*
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_register.*
import android.content.SharedPreferences
import kotlinx.android.synthetic.main.activity_main.*


class EditProfile : AppCompatActivity() {

    var editor: SharedPreferences.Editor? = null
    var username = ""
    var token=""
    //var textBox: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        val editbio =findViewById<EditText>(R.id.etbio)
        val savebtn =findViewById<Button>(R.id.save)
        checks()

        savebtn.setOnClickListener {
            val bio=editbio.text.toString()
            val intent =Intent(this@EditProfile,Profile::class.java)
            intent.putExtra("Username",username)
            intent.putExtra("Token",token)
            intent.putExtra("About",bio)
            startActivity(intent)
        }
        /* val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(baseContext)
         val value = sharedPreferences.getString("username", "")
         val Tok=sharedPreferences.getString("token", "")
         if (value != ""||Tok!="") {
             val intent = Intent(this, HomePage::class.java)

             intent.putExtra(
                 "Username",
                 value
             ) // sends username to homepage activity and other activities

             intent.putExtra(
                 "Token",
                 Tok
             ) // sends Token

             startActivity(intent)
             finish()
         }*/
    }


    /**
     * When the Save button is clicked it returns to the profile page.
     * @param view
     */


    fun buCancel(view: View) {
        val intent = Intent(this, Profile::class.java)
        intent.putExtra("Username", username)
        startActivity(intent)
        finish()
    }
    fun checks()
    {
        if (etusername.text.toString() == "" && etbio.text.toString() == "") {
            Toast.makeText(this, "Please enter a new username or a new bio", Toast.LENGTH_LONG).show()
            return
        } else if (etusername.text.toString() == "") {
            Toast.makeText(this,"About updated sucessfully",Toast.LENGTH_LONG).show()
            /*val about= etbio.getText().toString()
            val intent = Intent(this@EditProfile, Profile::class.java)
            intent.putExtra("Token",token)
            intent.putExtra("Username", username)
            intent.putExtra("About",about)
            startActivity(intent)*/
        } else if (etbio.text.toString() == "") {
            username = etusername.text.toString()
            Toast.makeText(this, "Edits Saved!", Toast.LENGTH_LONG).show()
        } else {

            Toast.makeText(this, "About Successful", Toast.LENGTH_LONG).show()
            /*val SP = PreferenceManager.getDefaultSharedPreferences(baseContext)
            val editor = SP.edit()
            //val strUserName = editor.putString("username", etusername.text.toString())
            val strabout = editor.putString("password", etbio.text.toString())
            editor.commit()
            //Toast.makeText(this, "Login Successful", Toast.LENGTH_LONG).show()
            val intent = Intent(this, Profile::class.java)

            intent.putExtra(
                "About",
                etbio.text.toString()
            ) // sends username to homepage activity and other activities

            startActivity(intent)
            finish()*/
            //val str = etbio.getText().toString()

            // val intent = Intent(applicationContext, Profile::class.java)
            //intent.putExtra("About", str)

        }
    }
}

