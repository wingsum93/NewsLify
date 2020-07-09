package com.crushtech.newslify.api

import com.crushtech.newslify.models.NewsResponse
import com.crushtech.newslify.ui.util.Constants.Companion.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApi {
    //get business breaking news
    @GET("v2/top-headlines")
    suspend fun getBusinessNews(
        @Query("country")
        countryCode: String = "us",
        @Query("category")
        category: String = "business",
        @Query("page")
        pageNumber: Int = 1,
        @Query("apiKey")
        apiKey: String = API_KEY
    ): Response<NewsResponse>

    //get  sport news
    @GET("v2/top-headlines")
    suspend fun getSportNews(
        @Query("country")
        countryCode: String = "us",
        @Query("category")
        category: String = "sport",
        @Query("page")
        pageNumber: Int = 1,
        @Query("apiKey")
        apiKey: String = API_KEY
    ): Response<NewsResponse>

    //get entertainment breaking news
    @GET("v2/top-headlines")
    suspend fun getEntertainmentNews(
        @Query("country")
        countryCode: String = "us",
        @Query("category")
        category: String = "entertainment",
        @Query("page")
        pageNumber: Int = 1,
        @Query("apiKey")
        apiKey: String = API_KEY
    ): Response<NewsResponse>

    //get science breaking news
    @GET("v2/top-headlines")
    suspend fun getScienceNews(
        @Query("country")
        countryCode: String = "us",
        @Query("category")
        category: String = "science",
        @Query("page")
        pageNumber: Int = 1,
        @Query("apiKey")
        apiKey: String = API_KEY
    ): Response<NewsResponse>

    //get technology breaking news
    @GET("v2/top-headlines")
    suspend fun getTechnologyNews(
        @Query("country")
        countryCode: String = "us",
        @Query("category")
        category: String = "technology",
        @Query("page")
        pageNumber: Int = 1,
        @Query("apiKey")
        apiKey: String = API_KEY
    ): Response<NewsResponse>

    @GET("v2/top-headlines")
    suspend fun getAllBreakingNews(
        @Query("country")
        countryCode: String = "us",
        @Query("page")
        pageNumber: Int = 1,
        @Query("apiKey")
        apiKey: String = API_KEY
    ): Response<NewsResponse>

    //get searched query
    @GET("v2/everything")
    suspend fun searchForNews(
        @Query("q")
        searchQuery: String,
        @Query("page")
        pageNumber: Int = 1,
        @Query("apiKey")
        apiKey: String = API_KEY
    ): Response<NewsResponse>

    //get news from specific source
    @GET("/v2/everything")
    suspend fun newsSources(
        @Query("domains")
        source: String = "techcrunch.com",
        @Query("page")
        pageNumber: Int = 1,
        @Query("apiKey")
        apiKey: String = API_KEY
    ): Response<NewsResponse>
}