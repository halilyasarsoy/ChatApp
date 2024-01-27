package com.halil.chatapp.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
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
    private var isEnlarged = false

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
        binding.nameTv.text = args.name
        binding.lastNameTv.text = args.lastname
        binding.profileImageTv.load(args.imgUrl)
        binding.profession.text = args.university
        binding.profileImageTv.setOnClickListener {
            if (isEnlarged) {
                shrinkView()
            } else {
                enlargeView()
            }
            isEnlarged = !isEnlarged
        }
    }

    private fun enlargeView() {
        val scaleAnimation = ScaleAnimation(
            1f, 1.3f,
            1f, 1.3f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        )
        scaleAnimation.duration = 500
        scaleAnimation.fillAfter = true

        binding.profileImageTv.startAnimation(scaleAnimation)
    }

    private fun shrinkView() {
        val scaleAnimation = ScaleAnimation(
            1.3f, 1f,
            1.3f, 1f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        )
        scaleAnimation.duration = 500
        scaleAnimation.fillAfter = true

        binding.profileImageTv.startAnimation(scaleAnimation)
    }

    override fun onStop() {
        super.onStop()
        val supportActionBar: androidx.appcompat.app.ActionBar? =
            (requireActivity() as AppCompatActivity).supportActionBar
        supportActionBar?.show()
    }
}

