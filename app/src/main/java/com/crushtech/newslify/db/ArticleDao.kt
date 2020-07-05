package com.crushtech.newslify.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.crushtech.newslify.models.Article

@Dao
interface ArticleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(article: Article): Long
    //returns the id of the article that was inserted

    @Query("SELECT * FROM articles")
    fun getAllArticle(): LiveData<List<Article>>

    @Delete
    suspend fun delete(article: Article)
}