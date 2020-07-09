package com.crushtech.newslify.repository

import com.crushtech.newslify.api.RetrofitInstance
import com.crushtech.newslify.db.ArticleDatabase
import com.crushtech.newslify.models.Article

class NewsRepository(
    private val db: ArticleDatabase
) {
    suspend fun getBusinessNews(countryCode: String, category:String, pageNumber: Int) =
        RetrofitInstance.api.getBusinessNews(countryCode, category, pageNumber)

    suspend fun getSportNews(countryCode: String, category:String, pageNumber: Int) =
        RetrofitInstance.api.getSportNews(countryCode, category, pageNumber)

    suspend fun getEntertainmentNews(countryCode: String, category:String, pageNumber: Int) =
        RetrofitInstance.api.getEntertainmentNews(countryCode, category, pageNumber)

    suspend fun getScienceNews(countryCode: String, category: String, pageNumber: Int) =
        RetrofitInstance.api.getScienceNews(countryCode, category, pageNumber)

    suspend fun getAllBreakingNews(countryCode: String, pageNumber: Int) =
        RetrofitInstance.api.getAllBreakingNews(countryCode, pageNumber)

    suspend fun searchNews(searchQuery: String, pageNumber: Int) =
        RetrofitInstance.api.searchForNews(searchQuery, pageNumber)

    suspend fun getNewsFromSpecificSource(source: String, pageNumber: Int) =
        RetrofitInstance.api.searchForNews(source, pageNumber)

    suspend fun upsert(article: Article) = db.getArticleDao().upsert(article)

    fun getSavedNews() = db.getArticleDao().getAllArticle()
    suspend fun deleteArticle(article: Article) = db.getArticleDao().delete(article)
}