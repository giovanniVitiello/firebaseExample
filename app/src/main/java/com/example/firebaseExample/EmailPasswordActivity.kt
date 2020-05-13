package com.example.firebaseExample

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_email_password.*
import java.util.*

const val MY_REQUEST_CODE = 100

class EmailPasswordActivity : AppCompatActivity() {

    private lateinit var fbAuth: FirebaseAuth
    private lateinit var provider: ArrayList<AuthUI.IdpConfig>
    private lateinit var btnLogout: Button
    private lateinit var goToMain: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email_password)

        //instance authentication firebase
        fbAuth = FirebaseAuth.getInstance()

        //provider authentication type
        provider = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.PhoneBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build(),
            AuthUI.IdpConfig.FacebookBuilder().build()
        )

        btnLogout = findViewById(R.id.btn_sign_out)
        goToMain = findViewById(R.id.go_to_main_screen)
        showSignInOption()
        clickListener()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MY_REQUEST_CODE) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK) {
                val token = response?.idpToken
                val user = FirebaseAuth.getInstance().currentUser  // get current user
                Toast.makeText(this, "" + user?.email, Toast.LENGTH_LONG).show()

                btn_sign_out.isEnabled = true
            } else {
                Toast.makeText(this, "" + response?.error?.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun showSignInOption() {
        startActivityForResult(
            AuthUI.getInstance().createSignInIntentBuilder()
                .setAvailableProviders(provider)
                .setTheme(R.style.MyTheme)
                .build(), MY_REQUEST_CODE
        )
    }

    private fun clickListener() {
        btnLogout.setOnClickListener {
            AuthUI.getInstance().signOut(this@EmailPasswordActivity) //logOut instance
                .addOnCompleteListener {
                    btn_sign_out.isEnabled = false
                    showSignInOption()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "" + e.message, Toast.LENGTH_LONG).show()
                }
        }

        goToMain.setOnClickListener {
            val myIntent = Intent(this@EmailPasswordActivity, MainActivity::class.java)
            startActivity(myIntent)
        }
    }
}
