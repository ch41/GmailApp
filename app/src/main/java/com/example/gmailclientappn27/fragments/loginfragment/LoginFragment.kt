package com.example.gmailclientappn27.fragments.loginfragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.gmailclientappn27.R
import com.example.gmailclientappn27.database.MessagesViewModel
import com.example.gmailclientappn27.databinding.FragmentLoginBinding
import com.example.gmailclientappn27.fragments.basefragment.BaseFragment
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.services.gmail.Gmail
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.*
import kotlin.math.sign


private const val TAG = "LoginFragment"
private const val RC_SIGN_IN = 0
const val RQ_FIREBASE_AUTH = 1

class LoginFragment : BaseFragment<FragmentLoginBinding>() {
    private lateinit var mMessagesViewModel: MessagesViewModel
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private lateinit var mLoginFragmentViewModel: LoginFragmentViewModel


    @SuppressLint("CommitPrefEdits")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        mMessagesViewModel = ViewModelProvider(this).get(MessagesViewModel::class.java)
        mLoginFragmentViewModel = ViewModelProvider(this).get(LoginFragmentViewModel::class.java)

        mGoogleSignInClient = mLoginFragmentViewModel.createRequest(
            getString(R.string.default_web_client_id),
            requireActivity()
        )
        connectAuthenticate()

        binding.signInButton.setOnClickListener {
           signIn()
            Toast.makeText(requireContext(),"You logged as ${auth.currentUser?.email}",Toast.LENGTH_LONG).show()
        }
        binding.loginButton.setOnClickListener {
            val sharedPreferences =
                requireActivity().getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences?.edit()
            editor?.apply {
                putBoolean("BOOLEAN_KEY", binding.rememberSwitch.isChecked)
            }?.apply()
            findNavController().navigate(R.id.action_loginFragment_to_messagesFragment)
        }
    }

    override fun onStart() {
        super.onStart()
       val sharedPreferences = requireActivity().getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val savedBoolean = sharedPreferences?.getBoolean("BOOLEAN_KEY", false)
        if(savedBoolean == true){
            findNavController().navigate(R.id.action_loginFragment_to_messagesFragment)
        }

    }

    private fun signIn() {
        val signInIntent = mGoogleSignInClient.signInIntent;
        startActivityForResult(signInIntent, RC_SIGN_IN)

    }


    private fun connectAuthenticate() {
        if (FirebaseAuth.getInstance().currentUser == null) {
            val providers = arrayListOf(
                AuthUI.IdpConfig.GoogleBuilder().build()
            )

            startActivityForResult(
                AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .build(),
                RQ_FIREBASE_AUTH
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                mLoginFragmentViewModel.firebaseAuthWithGoogle(account.idToken!!, auth)
            } catch (e: ApiException) {
                Log.d(TAG, e.message.toString())
            }
        }
    }


    override fun getFragmentView(): Int {
        return R.layout.fragment_login
    }
}


