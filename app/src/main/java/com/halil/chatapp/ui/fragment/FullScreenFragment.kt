package com.halil.chatapp.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.halil.chatapp.databinding.FragmentFullScreenBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FullScreenFragment : Fragment() {
    private var _binding: FragmentFullScreenBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFullScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val imageUrl = arguments?.getString("imageUri") ?: ""
        if (imageUrl.isNotEmpty()) {
            Glide.with(this).load(imageUrl).into(binding.fullScreenImageView)
        } else {
            // Eğer imageUrl boş ise, hata mesajı veya varsayılan bir resim gösterebilirsiniz.
            // Örneğin:
//             Glide.with(this).load(R.drawable.placeholder_image).into(binding.fullScreenImageView)
            // veya
            // binding.fullScreenImageView.setImageResource(R.drawable.placeholder_image)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}