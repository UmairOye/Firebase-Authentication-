package com.example.firebaseauthentication.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.firebaseauthentication.R
import com.example.firebaseauthentication.data.remote.FirebaseViewModel
import com.example.firebaseauthentication.databinding.FragmentSignUpBinding
import com.example.firebaseauthentication.extensions.addOnBackPressedCallback
import com.example.firebaseauthentication.extensions.showToast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.launch

class SignUp : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FirebaseViewModel by activityViewModels()
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var signInLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        addOnBackPressedCallback { findNavController().popBackStack() }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)


        binding.signUpButton.setOnClickListener {
            if (binding.editTextEmail.text.toString()
                    .isNotEmpty() && binding.editTextMobile.text.toString().isNotEmpty()
                && binding.editTextName.text.toString()
                    .isNotEmpty() && binding.editTextPassword.text.toString().isNotEmpty()
            ) {
                binding.progressbar.visibility = View.VISIBLE
                binding.signUpButton.isClickable = false
                lifecycleScope.launch {
                    viewModel.registerUser(
                        binding.editTextName.text.toString(),
                        binding.editTextEmail.text.toString(),
                        binding.editTextMobile.text.toString(),
                        binding.editTextPassword.text.toString()
                    ).observe(viewLifecycleOwner) {
                        binding.signUpButton.isClickable = true
                        binding.progressbar.visibility = View.GONE

                        if (it) {
                            showToast(getString(R.string.register_successfully))
                            findNavController().popBackStack()
                        } else {
                            showToast(getString(R.string.something_went_wrong))
                        }
                    }
                }
            } else {
                showToast(getString(R.string.all_fields_must_be_filled))
            }
        }

        binding.alreadyHaveAnAccount.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnGoogleLogin.setOnClickListener {
            signInWithGoogle()
        }


        signInLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = result.data
                    data?.let {
                        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                        try {
                            val account = task.getResult(ApiException::class.java)
                            account?.idToken?.let { token ->
                                viewModel.signInWithGoogle(token) { findNavController().navigate(R.id.action_signUp_to_welcome2) }
                            }
                        } catch (e: ApiException) {
                            showToast(e.message.toString())
                        }
                    }
                }
            }


        binding.backPressed.setOnClickListener {
            findNavController().popBackStack()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        signInLauncher.launch(signInIntent)
    }
}