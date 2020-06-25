package com.example.memestock

import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.ListView
import kotlinx.android.synthetic.main.activity_comment.*
import kotlinx.android.synthetic.main.adapter_view_layout.*

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import java.util.ArrayList

class inboxListAdapter

    (private val mContext: Context, private val mResource: Int, objects: ArrayList<inboxClass>) :
    ArrayAdapter<inboxClass>(mContext, mResource, objects) {
    private var lastPosition = -1

    /**
     * Holds variables in a View
     */
    private class ViewHolder {
        internal var Sender: TextView? = null
        internal var Subject: TextView? = null
        internal var messageBody: TextView? = null

    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        //get the persons information
        val Sender = getItem(position)!!.Sender
        val Subject = getItem(position)!!.Subject
        val messageBody=getItem(position)!!.messageBody


        //Create the person object with the information
        val myInbox = inboxClass(Sender, Subject, messageBody)

        //create the view result for showing the animation
        val result: View

        //ViewHolder object
        val holder: ViewHolder


        if (convertView == null) {
            val inflater = LayoutInflater.from(mContext)
            convertView = inflater.inflate(mResource, parent, false)
            holder = ViewHolder()
            holder.Sender = convertView!!.findViewById(R.id.Sender) as TextView
            holder.Subject = convertView.findViewById(R.id.Subject) as TextView
            holder.messageBody = convertView.findViewById(R.id.messageBody) as TextView


            result = convertView

            convertView.tag = holder
        } else {
            holder = convertView.tag as ViewHolder
            result = convertView
        }




        holder.Sender!!.setText(Sender)
        holder.Subject!!.setText(Subject)
        holder.messageBody!!.setText(messageBody)


        return convertView
    }

    companion object {

        private val TAG = "inboxListAdapter"
    }
}