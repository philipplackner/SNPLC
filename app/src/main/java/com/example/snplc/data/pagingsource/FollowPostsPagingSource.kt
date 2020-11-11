package com.example.snplc.data.pagingsource

import androidx.paging.PagingSource
import com.example.snplc.data.entities.Post
import com.example.snplc.data.entities.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await

class FollowPostsPagingSource(
    private val db: FirebaseFirestore
) : PagingSource<QuerySnapshot, Post>() {

    var firstLoad = true
    lateinit var follows: List<String>

    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, Post> {
        return try {
            val uid = FirebaseAuth.getInstance().uid!!
            if(firstLoad) {
                follows = db.collection("users")
                    .document(uid)
                    .get()
                    .await()
                    .toObject(User::class.java)
                    ?.follows ?: listOf()
                firstLoad = false
            }
            val curPage = params.key ?: db.collection("posts")
                .whereIn("authorUid", follows)
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .await()

            val lastDocumentSnapshot = curPage.documents[curPage.size() - 1]

            val nextPage = db.collection("posts")
                .whereIn("authorUid", follows)
                .orderBy("date", Query.Direction.DESCENDING)
                .startAfter(lastDocumentSnapshot)
                .get()
                .await()

            LoadResult.Page(
                curPage.toObjects(Post::class.java).onEach { post ->
                    val user = db.collection("users").document(uid).get().await().toObject(User::class.java)!!
                    post.authorProfilePictureUrl = user.profilePictureUrl
                    post.authorUsername = user.username
                    post.isLiked = uid in post.likedBy
                },
                null,
                nextPage
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}