package com.example.howlstagram

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_login.*

// android textInputLayout -> material library 사용
// https://prince-mint.tistory.com/7 여기에 약간의 정보 있음

class LoginActivity : AppCompatActivity() {

    var auth: FirebaseAuth? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth = FirebaseAuth.getInstance()
        email_login_button.setOnClickListener(){
            signinAndSignup()
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
