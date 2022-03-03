package com.standford.ligthhouse.ui.current

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.standford.ligthhouse.databinding.FragmentCurrentBinding
import com.standford.ligthhouse.ui.userprofile.ViewPagerAdapter

class CurrentFragment : Fragment() {

    private var _binding: FragmentCurrentBinding? = null
    private lateinit var viewPagerAdapter: ViewPagerAdapter

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentCurrentBinding.inflate(inflater, container, false)
        val root: View = binding.root


        createViewPager()

        return root
    }

    private fun createViewPager() {
        viewPagerAdapter = ViewPagerAdapter(childFragmentManager)

        viewPagerAdapter.addFragment(CurrentOneFragment())
        viewPagerAdapter.addFragment(CurrentTwoFragment())
        viewPagerAdapter.addFragment(CurrentThreeFragment())
        viewPagerAdapter.addFragment(CurrentFourFragment())
        viewPagerAdapter.addFragment(CurrentFiveFragment())
        viewPagerAdapter.addFragment(CurrentSixFragment())

        _binding!!.viewPager.adapter = viewPagerAdapter

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}