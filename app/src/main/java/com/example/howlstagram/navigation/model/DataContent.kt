package com.example.howlstagram.navigation.model

data class DataContent(var explain: String? = null,
                       var imageUri: String? = null,
                       var uid: String?= null,
                       var userId: String? = null,
                       var timestamp: Long? = null,
                       var favoriteCount: Int = 0,
                       var favorites: Map<String, Boolean> = HashMap()) {
    data class Comment(var uid: String? = null,
                       var userId: String? = null,
                       var comment: String? = null,
                       var timestamp: Long? = null)
}