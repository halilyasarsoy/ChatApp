package com.halil.chatapp.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import coil.load
import com.halil.chatapp.databinding.FragmentDetailUsersBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class DetailUsersFragment : Fragment() {
    private var _binding: FragmentDetailUsersBinding? = null
    private val binding get() = _binding!!
    private val args: DetailUsersFragmentArgs by navArgs()


    @SuppressLint("RestrictedApi")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val supportActionBar: androidx.appcompat.app.ActionBar? =
            (requireActivity() as AppCompatActivity).supportActionBar
        supportActionBar?.hide()
        supportActionBar?.setShowHideAnimationEnabled(false)
        _binding = FragmentDetailUsersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.nameTv.text = "FirstName : ${args.name}"
        binding.lastNameTv.text = "LastName : ${args.lastname}"
        binding.profileImageTv.load(args.imgUrl)
    }


    override fun onStop() {
        super.onStop()
        val supportActionBar: androidx.appcompat.app.ActionBar? =
            (requireActivity() as AppCompatActivity).supportActionBar
        supportActionBar?.show()
    }


}

