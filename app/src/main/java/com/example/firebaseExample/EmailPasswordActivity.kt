package com.example.firebaseExample

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_email_password.*
import java.util.*
import java.util.prefs.Preferences

const val MY_REQUEST_CODE = 100

class EmailPasswordActivity : AppCompatActivity() {

    private lateinit var provider: ArrayList<AuthUI.IdpConfig>
    private lateinit var btnLogout: Button
    private lateinit var btnDelete: Button
    private lateinit var goToMain: Button
    private lateinit var sharedPreferences: SharedPreferences

    //instance authentication firebase
    private val fbAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email_password)

        sharedPreferences = this.getSharedPreferences("uuid",Context.MODE_PRIVATE)
        sharedPreferences.getString("uuid", " ")

        //provider authentication type
        provider = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.PhoneBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build(),
            AuthUI.IdpConfig.FacebookBuilder().build(),
            AuthUI.IdpConfig.AnonymousBuilder().build()
        )

        btnLogout = findViewById(R.id.btn_sign_out)
        btnDelete = findViewById(R.id.btn_delete)
        goToMain = findViewById(R.id.go_to_main_screen)

        showSignInOption()
        clickListener()
    }

    @SuppressLint("RestrictedApi")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MY_REQUEST_CODE) {
            val response = IdpResponse.fromResultIntent(data) // response are the data of auth(email,phone,provider)
            if (resultCode == Activity.RESULT_OK) {
                val token = response?.idpToken
                val user = fbAuth.currentUser  // get current user
                Toast.makeText(this, "" + user?.email, LENGTH_LONG).show()
                val userUid = user?.uid
                sharedPreferences.edit().putString("uuid", userUid).apply()


                btn_sign_out.isEnabled = true
            } else {
                Toast.makeText(this, "" + response?.error?.message, LENGTH_LONG).show()
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
            signOut()
        }

        btnDelete.setOnClickListener {
            delete()
        }

        goToMain.setOnClickListener {
            val myIntent = Intent(this@EmailPasswordActivity, MainActivity::class.java)
            startActivity(myIntent)
        }
    }

    private fun signOut() {
        AuthUI.getInstance()
            .signOut(this@EmailPasswordActivity) //logOut instance
            .addOnCompleteListener {task ->
                if (task.isSuccessful){
                    btn_sign_out.isEnabled = false
                    showSignInOption()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "" + e.message, LENGTH_LONG).show()
            }
    }

    private fun delete() {
        fbAuth.currentUser
            ?.delete()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, getString(R.string.deleted), LENGTH_LONG).show()
                    showSignInOption()
                }
            }
    }
}
