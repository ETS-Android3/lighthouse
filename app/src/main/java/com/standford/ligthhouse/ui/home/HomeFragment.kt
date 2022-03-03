package com.standford.ligthhouse.ui.home

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.standford.ligthhouse.R
import com.standford.ligthhouse.databinding.FragmentHomeBinding
import com.standford.ligthhouse.model.LinkModel
import com.standford.ligthhouse.ui.DashboardActivity
import com.standford.ligthhouse.utility.Share
import com.standford.ligthhouse.utility.SharedPrefs

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var linkListView: ListView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        /*
        linkListView is a frontend component that uses a list view rendering LinkModel objects using
        the LinkModelAdapter class. An onClickListener for each list element uses the LinkModel object's
        link attribute to hyperlink the original disinfo link. This will eventually be replaced by
        a link to the debunking article.
        // hyperlink list object to original website
        */

        if (!SharedPrefs.getString(activity, SharedPrefs.ACCEPTED).equals("1")) {
            termsConditionDialog(requireActivity())
        }
        try {
            if (SharedPrefs.getAllFilteredMessage(activity).size < 0) {
                adapter = LinkModelAdapter(activity, Share.interceptedLinks)
            } else {
                adapter = LinkModelAdapter(activity, SharedPrefs.getAllFilteredMessage(activity))
            }
        } catch (e: Exception) {
            adapter = LinkModelAdapter(activity, Share.interceptedLinks)
        }

        _binding!!.rvDateWise.adapter = adapter

        _binding!!.rvDateWise.setOnItemClickListener { parent, view, position, id ->

            val obj = adapter!!.getItem(position) as LinkModel
            Share.selectModel = obj
            DashboardActivity.navController!!.navigate(R.id.navigation_user_profile)

//            val uri = Uri.parse("https://www." + obj.link)
//            val intent = Intent(Intent.ACTION_VIEW, uri)
//            startActivity(intent)
        }
        return root
    }

    fun termsConditionDialog(mContext: Activity) {
        try {

            val inflater = mContext.layoutInflater
            val alertLayout = inflater.inflate(R.layout.dialog_terms_condition, null)
            val tvTitle = alertLayout.findViewById<TextView>(R.id.exit_tv_title)
            val tvDesc = alertLayout.findViewById<TextView>(R.id.exit_tv_desc)
            val btnNo = alertLayout.findViewById<TextView>(R.id.exit_btn_no)
            val btnYes = alertLayout.findViewById<TextView>(R.id.exit_btn_yes)


            val alert = AlertDialog.Builder(mContext)
            alert.setView(alertLayout)
            alert.setCancelable(false)
            val dialog = alert.create()
            btnNo.setOnClickListener {
                mContext.finish()
                dialog.dismiss()
            }
            btnYes.setOnClickListener {
                SharedPrefs.save(mContext, SharedPrefs.ACCEPTED, "1")
                dialog.dismiss()
            }
            dialog.show()

        } catch (ignored: Exception) {
            Log.e("TAG", ignored.toString())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        var adapter: LinkModelAdapter? = null
    }
}