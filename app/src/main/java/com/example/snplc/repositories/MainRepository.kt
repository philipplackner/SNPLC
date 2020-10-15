package com.example.snplc.repositories

import android.net.Uri
import com.example.snplc.other.Resource

interface MainRepository {

    suspend fun createPost(imageUri: Uri, text: String): Resource<Any>
}