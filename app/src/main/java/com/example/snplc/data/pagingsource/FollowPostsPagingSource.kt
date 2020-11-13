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
            val chunks = follows.chunked(10)
            val resultList = mutableListOf<Post>()
            var curPage = params.key
            chunks.forEach { chunk ->
                curPage = params.key ?: db.collection("posts")
                    .whereIn("authorUid", chunk)
                    .orderBy("date", Query.Direction.DESCENDING)
                    .get()
                    .await()
                val parsedPage = curPage!!.toObjects(Post::class.java)
                    .onEach { post ->
                        val user = db.collection("users")
                            .document(post.authorUid).get().await().toObject(User::class.java)!!
                        post.authorProfilePictureUrl = user.profilePictureUrl
                        post.authorUsername = user.username
                        post.isLiked = uid in post.likedBy
                    }
                resultList.addAll(parsedPage)
            }


            val lastDocumentSnapshot = curPage!!.documents[curPage!!.size() - 1]

            val nextPage = db.collection("posts")
                .whereIn("authorUid", if(chunks.isNotEmpty()) chunks[0] else listOf())
                .orderBy("date", Query.Direction.DESCENDING)
                .startAfter(lastDocumentSnapshot)
                .get()
                .await()

            LoadResult.Page(
                resultList,
                null,
                nextPage
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}