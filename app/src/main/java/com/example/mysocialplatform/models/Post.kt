package com.example.mysocialplatform.models

import com.google.firebase.firestore.PropertyName

data class Post(
    var description: String = "",
    @get:PropertyName("image_url") @set:PropertyName("image_url") var image_url: String = "",
    @get:PropertyName("creation_time_ms") @set:PropertyName("creation_time_ms")var creation_time_ms: Long =0,
    var User:User? = null
)