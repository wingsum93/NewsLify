package com.crushtech.newslify.ui.util

import okhttp3.Cache

class Constants {
    companion object {
        const val API_KEY = "37cade18ac8b43178465a5e8eb3337f9"
        const val BASE_URL = "https://newsapi.org"
        const val SEARCH_NEWS_TIME_DELAY = 1000L
        const val QUERY_PAGE_SIZE = 20
        const val SHIMMER_ITEM_NUMBER = 10
        const val PRIVACY_POLICY = "http://www.crushtech.unaux.com/privacypolicy/?i=1"
        const val CACHE_SIZE = 10 * 1024 * 1024 // 10MB
    }
}