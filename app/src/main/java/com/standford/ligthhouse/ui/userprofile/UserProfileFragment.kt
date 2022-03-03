package com.standford.ligthhouse.ui.userprofile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.standford.ligthhouse.databinding.FragmentUserProfileBinding

class UserProfileFragment : Fragment() {


    private var _binding: FragmentUserProfileBinding? = null
    private lateinit var viewPagerAdapter: ViewPagerAdapter

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root
        createViewPager()
        return root
    }

    private fun createViewPager() {
        viewPagerAdapter = ViewPagerAdapter(childFragmentManager)

        viewPagerAdapter.addFragment(DesOneFragment())
        viewPagerAdapter.addFragment(DesTwoFragment())
        viewPagerAdapter.addFragment(DesThreeFragment())

        _binding!!.viewPager.adapter = viewPagerAdapter

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}