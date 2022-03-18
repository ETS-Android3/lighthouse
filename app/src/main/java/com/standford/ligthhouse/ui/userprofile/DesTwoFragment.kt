package com.standford.ligthhouse.ui.userprofile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.ScrollView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.standford.ligthhouse.R
import com.standford.ligthhouse.model.BodyList
import com.standford.ligthhouse.utility.Share

class DesTwoFragment : Fragment() {
    var adapter: WriteUpAdapter? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_des_two, container, false)
        initData(view)
        return view
    }

    private fun initData(view: View) {
        val rvWriteUp = view.findViewById<ListView>(R.id.rvWriteUp)
        val tvWithOutArray = view.findViewById<TextView>(R.id.tvWithOutArray)
        val scrollView = view.findViewById<ScrollView>(R.id.scrollView)

        Log.e("DATA", "initData: " + Share.selectModel.writeup)

        try {
            rvWriteUp.visibility = View.VISIBLE
            tvWithOutArray.visibility = View.GONE
            scrollView.visibility = View.GONE
            val gson = Gson()
            val myType = object : TypeToken<ArrayList<BodyList>>() {}.type
            val logs =
                gson.fromJson<ArrayList<BodyList>>(Share.selectModel.writeup.toString(), myType)

            adapter = WriteUpAdapter(activity, logs)
            rvWriteUp.adapter = adapter
        } catch (e: Exception) {

            rvWriteUp.visibility = View.GONE
            tvWithOutArray.visibility = View.VISIBLE
            scrollView.visibility = View.VISIBLE
            tvWithOutArray.text = Share.selectModel.writeup.toString()
        }


    }


    companion object
}