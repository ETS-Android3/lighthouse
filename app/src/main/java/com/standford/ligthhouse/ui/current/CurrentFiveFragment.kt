package com.standford.ligthhouse.ui.current

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.standford.ligthhouse.R

class CurrentFiveFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_current_one, container, false)
        initData(view)
        return view
    }

    private fun initData(view: View) {


    }

    companion object
}