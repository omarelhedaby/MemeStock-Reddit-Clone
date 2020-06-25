package com.example.memestock

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import kotlinx.android.synthetic.main.activity_settings.*

class Settings : AppCompatActivity() {
var username=""
 var token=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        val intent =getIntent()
        username=intent.extras.get("Username").toString()
        token=intent.extras.get("Token").toString()
       logout()
    }

    /**
     * logout function is used to allow user to logout of the application by opening a frame when the logout button get pressed
     * @param nothing
     */
    fun logout()
    {
        Logout.setOnClickListener {
            frame.visibility= View.VISIBLE
        }
        logout.setOnClickListener {
            val intent= Intent(this,MainActivity::class.java)
            val SP = PreferenceManager.getDefaultSharedPreferences(baseContext)
            val editor = SP.edit()
            val strUserName = editor.putString("username", "")
            val strToken = editor.putString("token", "")
            editor.commit()
            startActivity(intent)
            finish()
        }
        cancel.setOnClickListener {
            frame.visibility= View.INVISIBLE
        }

    }

    /**
     * buClick allows you to navigate between the app features by checking the pressed button
     * @param view
     *
     */
    fun buClick(view: View)
    {
        val buttonclicked=view as ImageButton
        when(buttonclicked.id)
        {
            R.id.Home -> {
                val intent= Intent(this,com.example.memestock.HomePage::class.java)
                intent.putExtra("Username",username)
                intent.putExtra("Token", token.toString()) // sends Token
                startActivity(intent)
                finish()

            }
            R.id.Messaging -> {
                val intent=Intent(this,ViewMessages::class.java)
                intent.putExtra("Username",username)
                intent.putExtra("Token", token.toString()) // sends Token
                startActivity(intent)
                finish()

            }
             R.id.Notification2 -> {
                  val intent= Intent(this,Notification2::class.java)
                  intent.putExtra("Username",username)
                 intent.putExtra("Token", token.toString()) // sends Token
                  startActivity(intent)

              }
            R.id.Profile -> {
                val intent= Intent(this,com.example.memestock.Profile::class.java)
                intent.putExtra("Username",username)
                intent.putExtra("Token", token.toString()) // sends Token
                startActivity(intent)
                finish()

            }
     /*       R.id.Settings -> {
                val intent= Intent(this,com.example.memestock.Settings::class.java)
                intent.putExtra("Username",username)
                startActivity(intent)
                finish()

            }*/

            R.id.Post -> {
                val intent=Intent(this,com.example.memestock.post::class.java)
                intent.putExtra("Username",username)
                intent.putExtra("Token", token.toString()) // sends Token
                startActivity(intent)


            }
        }
    }
    /**
     * swclick fun allow the user to navigate to the settings options like changing password and email
     * @param View
     */
    fun swclick(view:View)
    {
        val buttonc= view as Button
        when(buttonc.id)
        {
            R.id.bupass -> {
                val intent=Intent(this,com.example.memestock.passwordchange::class.java)
                intent.putExtra("Username",username)
                intent.putExtra("Token",token)
                startActivity(intent)
                }

            R.id.bumail -> {
                val intent=Intent(this,com.example.memestock.emailchange::class.java)
                intent.putExtra("Username",username)
                intent.putExtra("Token",token)
                startActivity(intent)
                }

        }

    }


}
