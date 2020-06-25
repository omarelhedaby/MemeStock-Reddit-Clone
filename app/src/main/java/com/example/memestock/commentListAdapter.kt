package com.example.memestock



import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ArrayAdapter
import android.widget.TextView

import java.util.ArrayList

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

import android.view.WindowManager
import android.widget.Button

import android.widget.ListView
import kotlinx.android.synthetic.main.activity_comment.*
import kotlinx.android.synthetic.main.adapter_view_layout.*

/**
 * Created by User on 3/14/2017.
 */

 class commentListAdapterr
/**
 * Default constructor for the PersonListAdapter
 * @param context
 * @param resource
 * @param objects
 */
    (private val mContext: Context, private val mResource: Int, objects: ArrayList<commentClass>) :
    ArrayAdapter<commentClass>(mContext, mResource, objects) {
    private var lastPosition = -1

    /**
     * Holds variables in a View
     */
    private class ViewHolder {
        internal var Author: Button? = null
        internal var comment: TextView? = null

    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        //get the persons information
        val Author = getItem(position)!!.Author
        val comment = getItem(position)!!.Comment


        //Create the person object with the information
        val mycomment = commentClass(Author, comment)

        //create the view result for showing the animation
        val result: View

        //ViewHolder object
        val holder: ViewHolder


        if (convertView == null) {
            val inflater = LayoutInflater.from(mContext)
            convertView = inflater.inflate(mResource, parent, false)
            holder = ViewHolder()
            holder.Author = convertView!!.findViewById(R.id.Author) as Button
            holder.comment = convertView.findViewById(R.id.Comment) as TextView


            result = convertView

            convertView.tag = holder
        } else {
            holder = convertView.tag as ViewHolder
            result = convertView
        }




        holder.Author!!.setText(Author)
        holder.comment!!.setText(comment)



        return convertView
    }

    companion object {

        private val TAG = "commentListAdapter"
    }
}