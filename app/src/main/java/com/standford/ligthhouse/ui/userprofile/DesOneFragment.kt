package com.standford.ligthhouse.ui.userprofile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.standford.ligthhouse.R
import com.standford.ligthhouse.utility.Share

class DesOneFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_des_one, container, false)
        initData(view)
        return view
    }

    private fun initData(view: View) {
        val tvSiteName = view.findViewById<TextView>(R.id.tvSiteName)
        val tvSiteDescrip = view.findViewById<TextView>(R.id.tvSiteDescrip)
        val tvRating = view.findViewById<TextView>(R.id.tvRating)
        val tvScore = view.findViewById<TextView>(R.id.tvScore)

        try {
            tvSiteName.text = "" + Share.selectModel.identifier
            tvSiteDescrip.text = "" + Share.selectModel.topline
            tvRating.text = "" + Share.selectModel.rank
            tvScore.text = "" + Share.selectModel.score
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    companion object
}