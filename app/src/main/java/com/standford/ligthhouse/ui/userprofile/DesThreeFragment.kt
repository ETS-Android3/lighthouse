package com.standford.ligthhouse.ui.userprofile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.standford.ligthhouse.R
import com.standford.ligthhouse.utility.Share

class DesThreeFragment : Fragment() {
    var adapter: SenderModelAdapter? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_des_three, container, false)
        initData(view)
        return view
    }

    private fun initData(view: View) {
        val tvSiteName = view.findViewById<TextView>(R.id.tvSiteName)
        val rvDomainList = view.findViewById<ListView>(R.id.rvDomainList)
        try {
            tvSiteName.text = Share.selectModel.link
            adapter = SenderModelAdapter(activity, Share.selectModel.messagesContainingDomain)
            rvDomainList.adapter = adapter
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    companion object
}