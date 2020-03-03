package com.example.howlstagram

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_login.*

// android textInputLayout -> material library 사용
// https://prince-mint.tistory.com/7 여기에 약간의 정보 있음

class LoginActivity : AppCompatActivity() {

    // 파이어베이스 권한 얻기
    var auth: FirebaseAuth? = null

    // 구글 로그인 (구글 로그인 기능을 이용하여 회원가입 없이 로그인 할려고 함) -> 이거 할려면 gradle에 play-services-auth 넣어야됨
    var googleSignInClient: GoogleSignInClient? = null

    // 인텐트로 넘길 코드. request code
    var GOOGLE_LOGIN_CODE = 9001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // 파이어베이스 권한. 로그인 및 회원가입 진행 시 이 권한을 이용
        auth = FirebaseAuth.getInstance()

        // 구글 로그인 옵션. 안드로이드 앱에 구글 로그인을 통합하려고 사용
        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // 나의 api키
            .requestEmail()     // 이메일을 받아옴
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)



        // 각종 로그인 리스너들
        email_login_button.setOnClickListener(){
            signinAndSignup()
        }

        google_login_button.setOnClickListener(){
            // 구글 로그인 1단계 -> 구글에서 로그인하기
            googleLogin()
        }


    }

    // 구글 로그인 창을 인텐트로 띄움
    fun googleLogin(){
        var signinIntent = googleSignInClient?.signInIntent
        startActivityForResult(signinIntent, GOOGLE_LOGIN_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == GOOGLE_LOGIN_CODE) {
            // 구글 로그인 2단계 -> 구글에서 넘겨주는 로그인 결과 값을 받아준다.
            var result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if(result.isSuccess) {
                var account = result.signInAccount      // 받아온 로그인 결과 값을 account화 시키고..
                firbaseAuthWithGoogle(account)          // 이 메서드에서 account의 토큰을 받아서 firebase에서의 인증정보로 만들어준다.
            }
        }
    }

    // 파이어베이스로 구글 로그인하기 3단계 -> 받은 로그인 결과 값으로 최종 로그인 해버리기
    fun firbaseAuthWithGoogle(account: GoogleSignInAccount?) {
        var credential = GoogleAuthProvider.getCredential(account?.idToken, null) // account의 토큰을 가져온다.
        auth?.signInWithCredential(credential)      // 파이어베이스 인증정보로 만들어준다.(task의 형태로 들어감) -> task가 뭐냐면, 처음 로그인 할 때 람다식 안에 사용한 it이 Task임
            ?.addOnCompleteListener {
                if(it.isSuccessful) {
                    // Login
                    moveMainPage(it.result?.user)
                } else {
                    // Show the error message
                    Toast.makeText(this, it.exception?.message, Toast.LENGTH_SHORT).show()
                }
            }
    }

    // sign in, sign up 버튼을 누르면 아이디가 존재할 시 접속, 아이디가 존재하지 않을 시 회원 가입을 해주는 기능
    fun signinAndSignup() {
        // createUser 말 그대로.. 우선 유저를 생성해 주려고 하는 역할
        auth?.createUserWithEmailAndPassword(email_edittext.text.toString(), password_edittext.text.toString())
            ?.addOnCompleteListener {
                if(it.isSuccessful) {
                    // Creating user account
                    moveMainPage(it.result?.user)
                } else {
                    // Login if you have account
                    signinEmail()
                }
                // 에러가 발생한다면 에러 메시지를 보여주는 내용을 추가해야 함.
            }
    }

    fun signinEmail() {
        auth?.signInWithEmailAndPassword(email_edittext.text.toString(), password_edittext.text.toString())
            ?.addOnCompleteListener {
                if(it.isSuccessful) {
                    // Login
                    moveMainPage(it.result?.user)
                } else {
                    // Show the error message
                    Toast.makeText(this, it.exception?.message, Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun moveMainPage(user: FirebaseUser?) {
        if(user != null) {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}
