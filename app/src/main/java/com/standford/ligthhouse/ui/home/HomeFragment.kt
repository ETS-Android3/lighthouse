package com.standford.ligthhouse.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.standford.ligthhouse.R
import com.standford.ligthhouse.databinding.FragmentHomeBinding
import com.standford.ligthhouse.model.LinkModel
import com.standford.ligthhouse.ui.DashboardActivity
import com.standford.ligthhouse.utility.Share

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

        adapter = LinkModelAdapter(activity, Share.interceptedLinks)
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


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        var adapter: LinkModelAdapter? = null
    }
}