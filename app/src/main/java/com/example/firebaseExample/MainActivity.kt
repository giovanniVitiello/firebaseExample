package com.example.firebaseExample

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity() {

    private lateinit var fbAuth : FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fbAuth = FirebaseAuth.getInstance()
        database = Firebase.database.reference

        fbAuth.addAuthStateListener {
            if(fbAuth.currentUser == null){
                this.finish()
            }
        }

        writeNewUser("1", "giovy10","giovy@prova.com")
        writeNewUser("2", "nik","nik@prova.com")

        database.child("users").child("10").child("username").setValue("nik")
        database.child("users").child("10").child("email").setValue("nik@prova.it")

        // fun for read element in db
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.child("users").children) {
                    val username = ds.getValue(User::class.java)
                    Log.d("TAG", username.toString())
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException())
                // ...
            }
        }

        database.addValueEventListener(postListener)
    }

    private fun writeNewUser(userId: String, name: String, email: String?) {
        val user = User(name, email)
        database.child("users").child(userId).setValue(user) //load data in db with setValue and pass the object
    }
}

@IgnoreExtraProperties
data class User(
    var username: String? = "",
    var email: String? = ""
) {

    @Exclude
    fun toMap(): Map<String, String?> {
        return mapOf(
            "username" to username,
            "email" to email
        )
    }
}
