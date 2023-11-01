package com.example.firebaseauthentication.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.firebaseauthentication.R
import com.example.firebaseauthentication.data.remote.FirebaseViewModel
import com.example.firebaseauthentication.databinding.FragmentForgotPasswordBinding
import com.example.firebaseauthentication.extensions.addOnBackPressedCallback
import com.example.firebaseauthentication.extensions.showToast

class ForgotPassword : Fragment() {

    private var _binding: FragmentForgotPasswordBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FirebaseViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        addOnBackPressedCallback { findNavController().popBackStack() }


        binding.resetPassword.setOnClickListener {
            if (binding.editTextEmail.text.toString().isNotEmpty()) {

                binding.progressbar.visibility = View.VISIBLE
                binding.resetPassword.isClickable = false

                sendPasswordResetEmail(binding.editTextEmail.text.toString())
            } else {
                showToast(getString(R.string.all_fields_must_be_filled))
            }
        }

        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun sendPasswordResetEmail(email: String) {
        viewModel.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                binding.progressbar.visibility = View.GONE
                binding.resetPassword.isClickable = true

                if (task.isSuccessful) {
                    findNavController().navigate(R.id.action_forgotPassword_to_login)
                    showToast(getString(R.string.password_reset_success))
                } else {
                    showToast(getString(R.string.password_reset_failed))
                }
            }
    }

}