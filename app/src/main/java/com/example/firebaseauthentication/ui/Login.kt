package com.example.firebaseauthentication.ui

import android.app.Activity.RESULT_OK
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
import com.example.firebaseauthentication.databinding.FragmentLoginBinding
import com.example.firebaseauthentication.extensions.showToast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.launch

class Login : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var signInLauncher: ActivityResultLauncher<Intent>
    private val viewModel: FirebaseViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)


        binding.tvForgotPassword.setOnClickListener {
            findNavController().navigate(R.id.action_login_to_forgotPassword)
        }

        binding.tvNewUserRegisterNow.setOnClickListener { moveToSignUp() }
        binding.btnGoogleLogin.setOnClickListener {
            signInWithGoogle()
        }

        binding.loginButton.setOnClickListener {
            if (binding.editTextEmail.text.toString()
                    .isNotEmpty() && binding.editTextPassword.text.toString().isNotEmpty()
            ) {
                binding.loginButton.isClickable = false
                binding.progressbar.visibility = View.VISIBLE
                lifecycleScope.launch {
                    viewModel.loginUser(
                        binding.editTextEmail.text.toString(),
                        binding.editTextPassword.text.toString()
                    ).observe(viewLifecycleOwner, Observer {
                        binding.loginButton.isClickable = true
                        binding.progressbar.visibility = View.GONE
                        if (it) {
                            findNavController().navigate(R.id.action_login_to_welcome2)
                        } else {
                            showToast(getString(R.string.something_went_wrong))
                        }
                    })
                }
            } else {
                showToast(getString(R.string.all_fields_must_be_filled))
            }
        }



        binding.plusButton.setOnClickListener { moveToSignUp() }

        signInLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val data: Intent? = result.data
                    data?.let {
                        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                        try {
                            val account = task.getResult(ApiException::class.java)
                            account?.idToken?.let { token ->
                                viewModel.signInWithGoogle(token) { findNavController().navigate(R.id.action_login_to_welcome2) }
                            }
                        } catch (e: ApiException) {
                            showToast(e.message.toString())
                        }
                    }
                }
            }


        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun moveToSignUp() {
        try {
            findNavController().navigate(R.id.action_login_to_signUp)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        signInLauncher.launch(signInIntent)
    }


}