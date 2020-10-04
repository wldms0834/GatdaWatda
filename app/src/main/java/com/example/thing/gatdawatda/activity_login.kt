package com.example.thing.gatdawatda

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.annotation.NonNull
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.thing.gatdawatda.R.id.progress_bar
import com.example.thing.gatdawatda.R.id.tvAttraction
import com.example.thing.gatdawatda.home.activity_home
import com.example.thing.gatdawatda.mypage.activity_mypage.user_id
import com.example.thing.gatdawatda.mypage.activity_setup
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import io.reactivex.internal.util.HalfSerializer.onComplete
import kotlinx.android.synthetic.main.activity_login.*
import java.util.HashMap


class activity_login : AppCompatActivity(){


    private var firebaseFirestore: FirebaseFirestore? = null
    private val TAG = "LoginActivity"

    // Firebase Authentication 관리 클래스
    var auth : FirebaseAuth? = null;

    // GoogleLogin 관리 클래스
    var googleSignInClient: GoogleSignInClient? = null

    //GoogleLogin
    val GOOGLE_LOGIN_CODE = 9001 // Intent Request ID

    override fun onCreate(savedInstantState: Bundle?){
        super.onCreate(savedInstantState)
        setContentView(R.layout.activity_login)

        // Firebase 로그인 통합 관리하는 Object 만들기
        auth = FirebaseAuth.getInstance()

        //구글 로그인 옵션
        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        //구글 로그인 클래스를 만듬
        googleSignInClient = GoogleSignIn.getClient(this, gso)


        //구글 로그인 버튼 세팅
        google_sign_in_button.setOnClickListener { googleLogin() }
        //이메일 로그인 세팅
        email_login_button.setOnClickListener { emailLogin() }
        btRegister.setOnClickListener { register() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        firebaseFirestore = FirebaseFirestore.getInstance()

        println(resultCode)
        println(resultCode)
        println(resultCode)
        println(resultCode)
        println(resultCode)
        println(resultCode)
        println(resultCode)
        println(resultCode)
        println(resultCode)
        println(resultCode)
        println(resultCode)
        println(resultCode)
        println(resultCode)
        println(resultCode)
        println(resultCode)

        // 구글에서 승인된 정보를 가지고 오기
        if (requestCode == GOOGLE_LOGIN_CODE && resultCode == Activity.RESULT_OK) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)

            println(result.status.toString())
            if (result.isSuccess) {
                val account = result.signInAccount
                firebaseAuthWithGoogle(account!!)
            } else {
                progress_bar.visibility = View.GONE
            }
        }
    }

    fun googleLogin() {
        progress_bar.visibility = View.VISIBLE
        var signInIntent = googleSignInClient?.signInIntent
        startActivityForResult(signInIntent, GOOGLE_LOGIN_CODE)


    }


    fun storeFirestore(user_id: String) {
        // Toast.makeText(this, user_id, Toast.LENGTH_SHORT).show()
        val usermap = HashMap<String, Any>()
        usermap.put("image", "")
        usermap.put("like", 0)
        usermap.put("name", "")
        usermap.put("tempSpotNum", 0)
        usermap.put("tripNum", 0)
        usermap.put("istraveling", "")
        firebaseFirestore?.collection("Users")?.document(user_id)
                ?.set(usermap)
                ?.addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
                ?.addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
        startActivity(Intent(this, activity_setup::class.java))
        finish()
    }

    fun moveMainPage(user: FirebaseUser?) {

        // User is signed in
        if (user != null) {
            Toast.makeText(this, getString(R.string.signin_complete), Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, activity_home::class.java))
            finish()
        }
    }


    fun emailLogin() {

        if (email_edittext.text.toString().isNullOrEmpty() || password_edittext.text.toString().isNullOrEmpty()) {
            Toast.makeText(this, getString(R.string.signout_fail_null), Toast.LENGTH_SHORT).show()

        } else {

            progress_bar.visibility = View.VISIBLE
            signinEmail();

        }
    }

    fun signinEmail() {

        auth?.signInWithEmailAndPassword(email_edittext.text.toString(), password_edittext.text.toString())
                ?.addOnCompleteListener { task ->
                    progress_bar.visibility = View.GONE

                    if (task.isSuccessful) {
                        //로그인 성공 및 다음페이지 호출
                        moveMainPage(auth?.currentUser)
                    } else {
                        //로그인 실패
                        Toast.makeText(this,task.exception!!.message, Toast.LENGTH_SHORT).show()
                    }
                }

    }

    fun register() {
        startActivity(Intent(this, activity_login_register::class.java))
    }

    fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth?.signInWithCredential(credential)
                ?.addOnCompleteListener { task ->
                    progress_bar.visibility = View.GONE
                    if (task.isSuccessful) {
                        //다음페이지 호출
                        firebaseFirestore?.collection("Users")?.document(auth?.currentUser?.uid.toString())?.get()?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                if (task.getResult().exists()) {
                                    moveMainPage(auth?.currentUser)
                                } else {
                                    storeFirestore(auth?.currentUser?.uid.toString())
                                }
                            } else {
                                Log.d(TAG, "get failed with ", task.exception)
                            }
                        }
                    }
                }
    }

    override fun onStart() {
        super.onStart()

        //자동 로그인 설정
        moveMainPage(auth?.currentUser)

    }
}