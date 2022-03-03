package com.standford.ligthhouse.ui.userprofile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.standford.ligthhouse.R
import com.standford.ligthhouse.model.BodyList
import com.standford.ligthhouse.utility.Share
import java.util.*

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

        adapter = WriteUpAdapter(activity, Share.selectModel.writeup as ArrayList<BodyList>?)
        rvWriteUp.adapter = adapter
    }


    companion object
}