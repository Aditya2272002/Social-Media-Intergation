package com.example.fetchinfo

import android.R.attr
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import org.json.JSONObject
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import androidx.core.app.ActivityCompat.startActivityForResult
import android.R.attr.data
import android.app.AlertDialog
import android.net.Uri
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity



import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.squareup.picasso.Picasso
import org.w3c.dom.Text
import androidx.annotation.NonNull
import com.google.android.gms.tasks.OnCompleteListener
import kotlin.system.exitProcess


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    private val RC_SIGN_IN = 123
    var flag = 1
    private lateinit var callBackManager: CallbackManager

    lateinit var loginButtonGle:SignInButton
    lateinit var loginButtonFB:LoginButton
    lateinit var signOutGle:Button
    lateinit var intro:ImageView

    lateinit var name: TextView
    lateinit var textName: TextView
    lateinit var profileImage: ImageView
    lateinit var email: TextView
    lateinit var dob: TextView
    lateinit var gender: TextView
    lateinit var friendsCount: TextView
    lateinit var textemail: TextView
    lateinit var textdob: TextView
    lateinit var textgender: TextView
    lateinit var textfriendsCount: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Google SignIn

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        val mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        val account = GoogleSignIn.getLastSignedInAccount(this)

        loginButtonFB = findViewById(R.id.login_button_fb)
        loginButtonGle = findViewById(R.id.loginButtonGle)
        loginButtonGle.setSize(SignInButton.SIZE_WIDE)
        loginButtonGle.visibility = View.VISIBLE
        loginButtonFB.visibility = View.VISIBLE

        name = findViewById(R.id.name)
        textName = findViewById(R.id.textName)
        email = findViewById(R.id.email)
        textemail = findViewById(R.id.textEmail)
        dob = findViewById(R.id.dob)
        textdob = findViewById(R.id.textDOB)
        gender = findViewById(R.id.gender)
        textgender = findViewById(R.id.textGender)
        friendsCount = findViewById(R.id.friendsCount)
        textfriendsCount = findViewById(R.id.textFriendCount)
        profileImage = findViewById(R.id.profileImage)
        signOutGle = findViewById(R.id.signOutGle)
        intro = findViewById(R.id.intro)

        name.visibility = View.GONE
        textName.visibility = View.GONE
        email.visibility = View.GONE
        textemail.visibility = View.GONE
        dob.visibility = View.GONE
        textdob.visibility = View.GONE
        gender.visibility = View.GONE
        textgender.visibility = View.GONE
        friendsCount.visibility = View.GONE
        textfriendsCount.visibility = View.GONE
        profileImage.visibility = View.GONE
        signOutGle.visibility = View.GONE

        loginButtonGle.setOnClickListener {
            val signInIntent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
            flag = 0
        }

        //FaceBook SignIn
        if(flag !=0){
            loginButtonFB.setOnClickListener {

                loginButtonFB.setReadPermissions(
                    listOf(
                        "email", "public_profile", "user_gender", "user_birthday",
                        "user_friends"
                    )
                )
                callBackManager = CallbackManager.Factory.create()

                LoginManager.getInstance()
                    .registerCallback(callBackManager, object : FacebookCallback<LoginResult> {
                        override fun onSuccess(result: LoginResult?) {

                            val graphRequest =
                                GraphRequest.newMeRequest(result?.accessToken) { `obj`, _ ->

                                    try {
                                        if (obj != null) {
                                            if (obj.has("id")) {
                                                loginButtonGle.visibility = View.GONE
                                                intro.visibility = View.GONE
                                                name.visibility = View.VISIBLE
                                                textName.visibility = View.VISIBLE
                                                email.visibility = View.VISIBLE
                                                textemail.visibility = View.VISIBLE
                                                dob.visibility = View.VISIBLE
                                                textdob.visibility = View.VISIBLE
                                                gender.visibility = View.VISIBLE
                                                textgender.visibility = View.VISIBLE
                                                friendsCount.visibility = View.VISIBLE
                                                textfriendsCount.visibility = View.VISIBLE
                                                profileImage.visibility = View.VISIBLE

                                                name.text = obj.getString("name")
                                                val url = JSONObject(obj.getString("picture"))
                                                    .getJSONObject("data").getString("url").toString()
                                                Picasso.get()
                                                    .load(url)
                                                    .into(profileImage)
                                                email.text = obj.getString("email")
                                                dob.text = obj.getString("birthday")
                                                gender.text = obj.getString("gender")
                                                friendsCount.text = obj.getJSONObject("friends")
                                                    .getJSONObject("summary").getString("total_count")
                                            }
                                        }
                                    } catch (e: Exception) {

                                    }
                                }
                            val param = Bundle()
                            param.putString(
                                "fields",
                                "name,email,friends,birthday,gender,id,picture.type(large)"
                            )
                            graphRequest.parameters = param
                            graphRequest.executeAsync()
                        }

                        override fun onCancel() {
                            TODO("Not yet implemented")
                        }

                        override fun onError(error: FacebookException?) {
                            TODO("Not yet implemented")
                        }

                    })
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
        else {
            callBackManager.onActivityResult(requestCode, resultCode, data)
        }

    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount = completedTask.getResult(ApiException::class.java)

            // Signed in successfully, show authenticated UI.
            loginButtonGle.visibility = View.GONE
            loginButtonFB.visibility = View.GONE
            intro.visibility = View.GONE
            textName.visibility = View.VISIBLE
            name.visibility = View.VISIBLE
            email.visibility = View.VISIBLE
            textemail.visibility = View.VISIBLE
            profileImage.visibility = View.VISIBLE
            signOutGle.visibility = View.VISIBLE

            name.text = account.displayName
            email.text = account.email
            val uri:Uri = account.photoUrl
            Picasso.get().load(uri).into(profileImage)

            signOutGle.setOnClickListener {
                signOut()
            }
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            loginButtonGle.visibility = View.VISIBLE
            textName.visibility = View.GONE
            name.visibility = View.GONE
            email.visibility = View.GONE
            textemail.visibility = View.GONE
        }
    }

    private fun signOut() {
        val builder = AlertDialog.Builder(this)
        //set title for alert dialog
        builder.setTitle("Sign Out ?")
        //set message for alert dialog
        builder.setMessage("Tap Yes to sign-out and close the App!")
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        //performing positive action
        builder.setPositiveButton("Yes"){dialogInterface, which ->
            Toast.makeText(applicationContext,"Succesfully SignOut!", Toast.LENGTH_LONG).show()
           quitApp()
        }
        //performing cancel action
        builder.setNeutralButton("Cancel"){dialogInterface , which ->
            Toast.makeText(applicationContext,"clicked cancel\n operation cancel",Toast.LENGTH_LONG).show()
        }
        //performing negative action
        builder.setNegativeButton("No"){dialogInterface, which ->
            Toast.makeText(applicationContext,"clicked No",Toast.LENGTH_LONG).show()
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.show()
    }
    private fun quitApp() {
        this@MainActivity.finish()
        exitProcess(0)
    }
}