package com.standford.ligthhouse.ui.home

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.standford.ligthhouse.R
import com.standford.ligthhouse.model.LinkModel
import java.util.*

class LinkModelAdapter
/**
 * adapter class to populate LIstView with data from list of LinkModel objects.
 * @param context
 * @param links
 */
    (context: Context?, links: ArrayList<LinkModel?>?) : ArrayAdapter<LinkModel?>(
    context!!, 0, links!!
) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val link = getItem(position)
        if (convertView == null) {
            convertView =
                LayoutInflater.from(context).inflate(R.layout.item_main_layout_2, parent, false)
        }

        Log.e("DATA", "getView: " + link)

        val tvWrittenBy = convertView!!.findViewById<View>(R.id.tvWrittenBy) as TextView
        val tvContent = convertView.findViewById<View>(R.id.tvContent) as TextView
        val tvSiteName = convertView.findViewById<View>(R.id.tvSiteName) as TextView
        val tvSiteDescrip = convertView.findViewById<View>(R.id.tvSiteDescrip) as TextView
        val tvRating = convertView.findViewById<View>(R.id.tvRating) as TextView
        val tvScore = convertView.findViewById<View>(R.id.tvScore) as TextView

        tvSiteName.text = link!!.link
        tvSiteDescrip.text = link.topline
        tvRating.text = link.rank
        tvScore.text = "" + link.score + "/100"

        tvWrittenBy.text = link.sender
        tvContent.text = link.originalMessage


        return convertView
    }
}