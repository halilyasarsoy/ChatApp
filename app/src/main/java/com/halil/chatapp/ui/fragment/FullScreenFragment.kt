package com.halil.chatapp.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.halil.chatapp.databinding.FragmentFullScreenBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FullScreenFragment : Fragment() {
    private var _binding: FragmentFullScreenBinding? = null
    private val binding get() = _binding!!
    private val db = FirebaseFirestore.getInstance()
    private val uid = FirebaseAuth.getInstance().currentUser?.uid

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
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()

        if (imageUrl.isNotEmpty()) {
            Glide.with(this).load(imageUrl).into(binding.fullScreenImageView)
        } else {
            uid?.let { userId ->
                db.collection("users").document(userId).get()
                    .addOnSuccessListener { document ->
                        val mImageView = document.getString("imgUrl")
                        if (!mImageView.isNullOrEmpty()) {
                            Glide.with(this).load(mImageView).into(binding.fullScreenImageView)
                        } else {
                            // Eğer imageUrl boş ise, hata mesajı veya varsayılan bir resim gösterebilirsiniz.
                            // Örneğin:
                            // Glide.with(this).load(R.drawable.placeholder_image).into(binding.fullScreenImageView)
                            // veya
                            // binding.fullScreenImageView.setImageResource(R.drawable.placeholder_image)
                        }
                    }
                    .addOnFailureListener { exception ->
                        // Hata durumunda hata mesajı gösterilebilir
                    }
            }
        }
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Geri tuşuna basıldığında SettingFragment'a geri dön
                val action = FullScreenFragmentDirections.actionFullScreenFragmentToSettingFragment()
                findNavController().navigate(action)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        (requireActivity() as AppCompatActivity).supportActionBar?.show()

    }
}