package com.example.memestock

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.WindowManager
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_comment.*
import kotlinx.android.synthetic.main.activity_reports.*
import kotlinx.android.synthetic.main.activity_viewmessages.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.ArrayList
import java.util.HashMap

class Reports: AppCompatActivity() {
    var username = ""
    var token=""
    var reportList= arrayListOf<reportClass>()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reports)
        val intent = getIntent()
        username = intent.extras.get("Username").toString()
        token=intent.extras.get("Token").toString()
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)

        var listView = findViewById<ListView>(R.id.reportListView)

        getReports()
    }


    fun RefreshList(listView: ListView)
    {
        var adapter=reportsListAdapter(this,R.layout.adapter_report_layout,reportList)
        listView.adapter=adapter

    }

    fun getReports()
    {
        val queue = Volley.newRequestQueue(this)
        val url: String = "http://18.217.163.16/api/Moderator/Reports"
        val builder = Uri.parse(url).buildUpon();
        val params = HashMap<String,Any>()

        val jsonObject = JSONObject(params)

        val request =object : JsonObjectRequest(
            Request.Method.GET,builder.toString(),jsonObject,
            Response.Listener { response ->
                //                notSuccess = false;
                var strResp = response.toString()
                val jsonObj: JSONObject = JSONObject(strResp)
                val jsonArray: JSONArray = jsonObj.getJSONArray("reports")
                for(i in 0..jsonArray.length()-1)
                {
                    var jsonObj=jsonArray.getJSONObject(i)
                    if (jsonObj.getBoolean("post")==true)
                    {
                        reportList.add(reportClass( jsonObj.getString("reportedId"),jsonObj.getString("srName"), "Post"))
                    }
                    else
                    {
                        reportList.add(reportClass( jsonObj.getString("reportedId"),jsonObj.getString("srName"), "Comment"))
                    }

                }
                RefreshList(reportListView)

            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "${error.toString()}", Toast.LENGTH_SHORT).show()
            }) {


        }

        queue.add(request)


    }

    fun deleteRep(id:String)
    {
        val queue = Volley.newRequestQueue(this)
        val url: String = "http://18.217.163.16/api/Moderator/Reports/$id"


        val builder = Uri.parse(url).buildUpon();

        val params = HashMap<String,String>()
        params["reportId"]=id
        val jsonObject = JSONObject(params)

        val request =object : JsonObjectRequest(
            Request.Method.DELETE,builder.toString(),jsonObject,
            Response.Listener { response ->

                RefreshList(reportListView)
                Toast.makeText(this,"Report deleted!",Toast.LENGTH_LONG).show()

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

    fun leaveMod(SrName:String)
    {
        val queue = Volley.newRequestQueue(this)
        val url: String = "http://18.217.163.16/api/Moderator/leave"


        val builder = Uri.parse(url).buildUpon();

        val params = HashMap<String,String>()
        params["SrName"]=SrName
        val jsonObject = JSONObject(params)

        val request =object : JsonObjectRequest(
            Request.Method.PUT,builder.toString(),jsonObject,
            Response.Listener { response ->

                RefreshList(reportListView)
                Toast.makeText(this,"You left the moderation!",Toast.LENGTH_LONG).show()

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


    inner class reportsListAdapter

        (private val mContext: Context, private val mResource: Int, objects: ArrayList<reportClass>) :
        ArrayAdapter<reportClass>(mContext, mResource, objects) {
        private var lastPosition = -1

        /**
         * Holds variables in a View
         */
        private inner class ViewHolder {
            internal var srName: TextView? = null
            internal var postOrComment: TextView? = null
            internal var deleteRep: Button? = null
            internal var leaveMod: Button?=null
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var convertView = convertView
            //get the persons information
            val srName = getItem(position)!!.srName
            val postOrComment = getItem(position)!!.postOrComment
            val id=getItem(position)!!.id


            var myView=layoutInflater.inflate(R.layout.adapter_report_layout,null)
            //Create the person object with the information
            val myReport = reportClass(id,srName,postOrComment)

            //create the view result for showing the animation
            val result: View

            //ViewHolder object
            val holder: ViewHolder


            if (convertView == null) {
                val inflater = LayoutInflater.from(mContext)
                convertView = inflater.inflate(mResource, parent, false)
                holder = ViewHolder()
                holder.srName = convertView!!.findViewById(R.id.srName) as TextView
                holder.postOrComment = convertView!!.findViewById(R.id.postOrComment) as TextView
                holder.deleteRep=convertView!!.findViewById(R.id.deleteReport) as Button
                holder.leaveMod=convertView!!.findViewById(R.id.leaveModeration) as Button
                result = convertView

                convertView.tag = holder
            } else {
                holder = convertView.tag as ViewHolder
                result = convertView
            }

            holder.srName!!.setText(srName)
            holder.postOrComment!!.setText(postOrComment)
            holder.deleteRep!!.setOnClickListener(View.OnClickListener {
                deleteRep(id)
            })
            holder.leaveMod!!.setOnClickListener(View.OnClickListener {
                leaveMod(holder.srName!!.text.toString())
            })
            return convertView
        }
    }
}