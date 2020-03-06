package com.example.howlstagram.navigation

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.howlstagram.R
import com.example.howlstagram.navigation.model.DataContent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_add_photo.*
import java.text.SimpleDateFormat
import java.util.*

class AddPhotoActivity : AppCompatActivity() {
    var PICK_IMAGE_FROM_ALBUM = 0
    var storage: FirebaseStorage? = null
    var photoUri: Uri? = null

    // 유저의 정보를 가져올 수 있도록 이라고 강의에서 말했음.
    var auth: FirebaseAuth? = null
    var firestore: FirebaseFirestore? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_photo)

        // Initiate storage
        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Open the album
        var photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        startActivityForResult(photoPickerIntent, PICK_IMAGE_FROM_ALBUM)

        // add image upload event
        addphoto_btn_upload.setOnClickListener(){
            contentUpload()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == PICK_IMAGE_FROM_ALBUM) {
            // 갤러리에서 선택한 항목에 대한 처리. 여기서는 addphoto_image 가 선택한 사진으로 적용되도록 했음
            if(resultCode == Activity.RESULT_OK){
                photoUri = data?.data
                addphoto_image.setImageURI(photoUri)
            } else {
                finish()
            }
        }
    }

    // 이건 파이어베이스 서버에 사진을 저장하는 코드. + 저장된 상태의 정보를 firestore DB에 저장
    fun contentUpload() {
        var timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        var imageFileName = "IMAGE_${timestamp}_.png"

        var storageRef = storage?.reference?.child("images")?.child(imageFileName)
        storageRef?.putFile(photoUri!!)?.addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener {
                var dataContent = DataContent()

                dataContent.imageUri = it.toString()
                dataContent.explain = addphoto_edit_explain.text.toString()
                dataContent.timestamp = System.currentTimeMillis()
                dataContent.uid = auth?.currentUser?.uid
                dataContent.userId = auth?.currentUser?.email

                firestore?.collection("images")?.document()?.set(dataContent)
                setResult(Activity.RESULT_OK)
                finish()
            }
        }

    }
}
