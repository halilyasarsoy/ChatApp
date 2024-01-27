package com.halil.chatapp.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.halil.chatapp.databinding.FragmentSelectionScreenBinding

class SelectionScreenFragment : Fragment() {
    private var _binding: FragmentSelectionScreenBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelectionScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navigateButton()
    }

    private fun navigateButton() {
        binding.loginButtonSelectionFragment.setOnClickListener {
            val actionLogin =
                SelectionScreenFragmentDirections.actionSelectionScreenFragmentToLoginScreenFragment()
            findNavController().navigate(actionLogin)
        }
        binding.registerButtonSelectionFragment.setOnClickListener {
            val actionRegister =
                SelectionScreenFragmentDirections.actionSelectionScreenFragmentToRegisterFragment()
            findNavController().navigate(actionRegister)
        }
    }
}