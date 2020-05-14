package com.example.firebaseExample

import android.content.Context
import android.content.SharedPreferences
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
    private lateinit var sharedPreferences : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fbAuth = FirebaseAuth.getInstance()
        database = Firebase.database.reference
        sharedPreferences = this.getSharedPreferences("uuid", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("uuid", " ")

        fbAuth.addAuthStateListener {
            if(fbAuth.currentUser == null){
                this.finish()
            }
        }

        // here I pass the same id of authentication in the database
        writeNewUser(userId, "giovy10","giovy@prova.com", 15)
        writeNewUser("16", "nik","nik@prova.com", 680)

        // how to update child value in node for example "username" or "email" or "score"
        database.child("users").child("10").child("username").setValue("nik")
        database.child("users").child("10").child("email").setValue("nik@prova.it")
        database.child("users").child("10").child("score").setValue(520)

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

        //make a query in database realtime
        //orderbychild need to create a list of all child of node users with those path("score")

        val query: Query = database.child("users").orderByChild("score")
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (userSnapshot in dataSnapshot.children) {
                    Log.i("TAG","username in alphabetical order " + userSnapshot.child("score"))
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(
                   "FragmentActivity.TAG",
                    "loadPost:onCancelled",
                    databaseError.toException()
                )
            }
        })

        database.addValueEventListener(postListener)
    }

    private fun writeNewUser(userId: String?, name: String, email: String?, score: Int) {
        val user = User(name, email, score)
        if (userId != null) {
            database.child("users").child(userId).setValue(user)
        } //load data in db with setValue and pass the object
    }
}

@IgnoreExtraProperties
data class User(
    var username: String? = "",
    var email: String? = "",
    var score: Int? = 0
) {

    @Exclude
    fun toMap(): Map<String, String?> {
        return mapOf(
            "username" to username,
            "email" to email
        )
    }
}

// CREATE AUTHENTICATION ANONIMOUS WITHOUT FIREBASE UI. DIRECTLY IN ONCREATE

//private lateinit var auth: FirebaseAuth
//
//override fun onCreate(savedInstanceState: Bundle?) {
//    super.onCreate(savedInstanceState)
//    setContentView(R.layout.activity_main)
//
//    database = Firebase.database.reference
//
//    auth = FirebaseAuth.getInstance()
//
//    if (auth.currentUser == null) {
//        auth.signInAnonymously()
//            .addOnCompleteListener(this) { task ->
//                if (task.isSuccessful) {
//                    val user = auth.currentUser
//                    // Sign in success, update UI with the signed-in user's information
//                    Log.d("autentication_log", "signInAnonymously:success")
//                } else {
//                    // If sign in fails, display a message to the user.
//                    Log.w("autentication_log", "signInAnonymously:failure", task.exception)
//                    Toast.makeText(
//                        baseContext, "Authentication failed.",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            }
//    }
// fun for read element in db
//val postListener = object : ValueEventListener {
//    override fun onDataChange(dataSnapshot: DataSnapshot) {
//        for (ds in dataSnapshot.child("users").children) {
//            val username = ds.getValue(User::class.java)
//            Log.d("TAG", username.toString())
//        }
//    }
//
//    override fun onCancelled(databaseError: DatabaseError) {
//        // Getting Post failed, log a message
//        Log.w("TAG", "loadPost:onCancelled", databaseError.toException())
//        // ...
//    }
//}
