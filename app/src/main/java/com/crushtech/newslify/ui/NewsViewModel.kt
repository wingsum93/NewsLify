package com.crushtech.newslify.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.*
import android.net.NetworkCapabilities.*
import android.os.Build
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.crushtech.newslify.R
import com.crushtech.newslify.models.Article
import com.crushtech.newslify.models.NewsResponse
import com.crushtech.newslify.repository.NewsRepository
import com.crushtech.newslify.ui.util.Resource
import com.muddzdev.styleabletoastlibrary.StyleableToast
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException
import java.util.*

class NewsViewModel(
    app: Application,
    private val newsRepository: NewsRepository
) : AndroidViewModel(app) {

    val businessNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var businessNewsPage = 1
    private var businessNewsResponse: NewsResponse? = null

    val technologyNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var technologyNewsPage = 1
    private var technologyNewsResponse: NewsResponse? = null

    val healthNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var healthNewsPage = 1
    private var healthNewsResponse: NewsResponse? = null

    val allBreakingNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var allBreakingNewsPage = 1
    private var allBreakingNewsResponse: NewsResponse? = null

    val specificNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var specificNewsPage = 1
    private var specificNewsResponse: NewsResponse? = null
    val specificNews1: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    private var specificNewsResponse1: NewsResponse? = null
    val specificNews2: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    private var specificNewsResponse2: NewsResponse? = null
    val specificNews3: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    private var specificNewsResponse3: NewsResponse? = null
    val specificNews4: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    private var specificNewsResponse4: NewsResponse? = null
    val specificNews5: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    private var specificNewsResponse5: NewsResponse? = null
    val specificNews6: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    private var specificNewsResponse6: NewsResponse? = null

    val sportNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var sportNewsPage = 1
    private var sportNewsResponse: NewsResponse? = null

    val scienceNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var scienceNewsPage = 1
    private var scienceNewsResponse: NewsResponse? = null

    val entertainmentNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var entertainmentNewsPage = 1
    private var entertainmentNewsResponse: NewsResponse? = null


    val searchNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var searchNewsPage = 1
    var searchNewsResponse: NewsResponse? = null

    init {
//        val getCountryCode =
//            app.applicationContext.getSharedPreferences("myprefs", Context.MODE_PRIVATE)
//        val countryIsoCode = getCountryCode.getString("countryIsoCode", "us")
        try {


            getTechnologyNews("us", "technology")
            getHealthNews("us", "health")
            getSpecificNews6("wsj.com")
            getSpecificNews5("cnbc.com")
            getSpecificNews4("espn.com")
            getSpecificNews3("reuters.com")
            getSpecificNews2("techcrunch.com")
            getSpecificNews1("cnn.com")
            getSpecificNews("bbc.com")
        } catch (e: Exception) {
        }
    }


    fun getBusinessNews(countryCode: String, category: String) = viewModelScope.launch {
        safeBusinessNewsCall(countryCode, category)
    }

    private suspend fun safeBusinessNewsCall(countryCode: String, category: String) {
        businessNews.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response =
                    newsRepository.getBusinessNews(countryCode, category, technologyNewsPage)
                businessNews.postValue(handleBusinessNewsResponse(response))

                //check if the api has data for the selected country if not, set default to US
                if (response.isSuccessful && response.body()?.articles.isNullOrEmpty()) {
                    businessNews.postValue(Resource.Loading())
                    val retryResponse = newsRepository.getBusinessNews(
                        "us",
                        "business", businessNewsPage
                    )
                    businessNews.postValue(handleBusinessNewsResponse(retryResponse))
                }
            } else {
                businessNews.postValue(Resource.Error("No internet connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> businessNews.postValue(Resource.Error("Network Failure"))
                else -> businessNews.postValue(Resource.Error("conversion error"))
            }
        }
    }

    private fun handleBusinessNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                businessNewsPage++
                if (businessNewsResponse == null) {
                    businessNewsResponse = resultResponse
                } else {
                    val oldArticles = businessNewsResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(businessNewsResponse ?: resultResponse)

            }
        }
        return Resource.Error(response.message())
    }


    fun getTechnologyNews(countryCode: String, category: String) = viewModelScope.launch {
        safeTechnologyNewsCall(countryCode, category)
    }

    private suspend fun safeTechnologyNewsCall(countryCode: String, category: String) {
        technologyNews.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response =
                    newsRepository.getTechnologyNews(countryCode, category, businessNewsPage)
                technologyNews.postValue(handleTechnologyNewsResponse(response))

            } else {
                technologyNews.postValue(Resource.Error("No internet connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> technologyNews.postValue(Resource.Error("Network Failure"))
                else -> technologyNews.postValue(Resource.Error("conversion error"))
            }
        }
    }

    private fun handleTechnologyNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                technologyNewsPage++
                if (technologyNewsResponse == null) {
                    technologyNewsResponse = resultResponse
                } else {
                    val oldArticles = technologyNewsResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(technologyNewsResponse ?: resultResponse)

            }
        }
        return Resource.Error(response.message())
    }


    fun getHealthNews(countryCode: String, category: String) = viewModelScope.launch {
        safeHealthNewsCall(countryCode, category)
    }

    private suspend fun safeHealthNewsCall(countryCode: String, category: String) {
        healthNews.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response =
                    newsRepository.getHealthNews(countryCode, category, healthNewsPage)
                healthNews.postValue(handleHealthNewsResponse(response))

            } else {
                healthNews.postValue(Resource.Error("No internet connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> healthNews.postValue(Resource.Error("Network Failure"))
                else -> healthNews.postValue(Resource.Error("conversion error"))
            }
        }
    }

    private fun handleHealthNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                healthNewsPage++
                if (healthNewsResponse == null) {
                    healthNewsResponse = resultResponse
                } else {
                    val oldArticles = healthNewsResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(healthNewsResponse ?: resultResponse)

            }
        }
        return Resource.Error(response.message())
    }


    fun getSpecificNews(source: String) = viewModelScope.launch {
        safeSpecificNewsCall(source)
    }


    private suspend fun safeSpecificNewsCall(source: String) {
        specificNews.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response =
                    newsRepository.getNewsFromSpecificSource(source, specificNewsPage)
                specificNews.postValue(handleSpecificNewsResponse(response))
            } else {
                specificNews.postValue(Resource.Error("No internet connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> specificNews.postValue(Resource.Error("Network Failure"))
                else -> specificNews.postValue(Resource.Error("conversion error"))
            }
        }
    }

    private fun handleSpecificNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                specificNewsPage++
                if (specificNewsResponse == null) {
                    specificNewsResponse = resultResponse
                } else {
                    val oldArticles = specificNewsResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(specificNewsResponse ?: resultResponse)

            }
        }
        return Resource.Error(response.message())
    }


    fun getSpecificNews1(source: String) = viewModelScope.launch {
        safeSpecificNewsCall1(source)
    }


    private suspend fun safeSpecificNewsCall1(source: String) {
        specificNews1.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response =
                    newsRepository.getNewsFromSpecificSource(source, specificNewsPage)
                specificNews1.postValue(handleSpecificNewsResponse1(response))
            } else {
                specificNews1.postValue(Resource.Error("No internet connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> specificNews1.postValue(Resource.Error("Network Failure"))
                else -> specificNews1.postValue(Resource.Error("conversion error"))
            }
        }
    }

    private fun handleSpecificNewsResponse1(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                specificNewsPage++
                if (specificNewsResponse1 == null) {
                    specificNewsResponse1 = resultResponse
                } else {
                    val oldArticles = specificNewsResponse1?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(specificNewsResponse1 ?: resultResponse)

            }
        }
        return Resource.Error(response.message())
    }


    fun getSpecificNews2(source: String) = viewModelScope.launch {
        safeSpecificNewsCall2(source)
    }


    private suspend fun safeSpecificNewsCall2(source: String) {
        specificNews2.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response =
                    newsRepository.getNewsFromSpecificSource(source, specificNewsPage)
                specificNews2.postValue(handleSpecificNewsResponse2(response))
            } else {
                specificNews2.postValue(Resource.Error("No internet connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> specificNews2.postValue(Resource.Error("Network Failure"))
                else -> specificNews2.postValue(Resource.Error("conversion error"))
            }
        }
    }

    private fun handleSpecificNewsResponse2(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                specificNewsPage++
                if (specificNewsResponse2 == null) {
                    specificNewsResponse2 = resultResponse
                } else {
                    val oldArticles = specificNewsResponse2?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(specificNewsResponse2 ?: resultResponse)

            }
        }
        return Resource.Error(response.message())
    }


    fun getSpecificNews3(source: String) = viewModelScope.launch {
        safeSpecificNewsCall3(source)
    }


    private suspend fun safeSpecificNewsCall3(source: String) {
        specificNews3.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response =
                    newsRepository.getNewsFromSpecificSource(source, specificNewsPage)
                specificNews3.postValue(handleSpecificNewsResponse3(response))
            } else {
                specificNews3.postValue(Resource.Error("No internet connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> specificNews3.postValue(Resource.Error("Network Failure"))
                else -> specificNews3.postValue(Resource.Error("conversion error"))
            }
        }
    }

    private fun handleSpecificNewsResponse3(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                specificNewsPage++
                if (specificNewsResponse3 == null) {
                    specificNewsResponse3 = resultResponse
                } else {
                    val oldArticles = specificNewsResponse3?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(specificNewsResponse3 ?: resultResponse)

            }
        }
        return Resource.Error(response.message())
    }


    fun getSpecificNews4(source: String) = viewModelScope.launch {
        safeSpecificNewsCall4(source)
    }


    private suspend fun safeSpecificNewsCall4(source: String) {
        specificNews4.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response =
                    newsRepository.getNewsFromSpecificSource(source, specificNewsPage)
                specificNews4.postValue(handleSpecificNewsResponse4(response))
            } else {
                specificNews4.postValue(Resource.Error("No internet connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> specificNews4.postValue(Resource.Error("Network Failure"))
                else -> specificNews4.postValue(Resource.Error("conversion error"))
            }
        }
    }

    private fun handleSpecificNewsResponse4(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                specificNewsPage++
                if (specificNewsResponse4 == null) {
                    specificNewsResponse4 = resultResponse
                } else {
                    val oldArticles = specificNewsResponse4?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(specificNewsResponse4 ?: resultResponse)

            }
        }
        return Resource.Error(response.message())
    }


    fun getSpecificNews5(source: String) = viewModelScope.launch {
        safeSpecificNewsCall5(source)
    }


    private suspend fun safeSpecificNewsCall5(source: String) {
        specificNews5.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response =
                    newsRepository.getNewsFromSpecificSource(source, specificNewsPage)
                specificNews5.postValue(handleSpecificNewsResponse5(response))
            } else {
                specificNews5.postValue(Resource.Error("No internet connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> specificNews5.postValue(Resource.Error("Network Failure"))
                else -> specificNews5.postValue(Resource.Error("conversion error"))
            }
        }
    }

    private fun handleSpecificNewsResponse5(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                specificNewsPage++
                if (specificNewsResponse5 == null) {
                    specificNewsResponse5 = resultResponse
                } else {
                    val oldArticles = specificNewsResponse5?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(specificNewsResponse5 ?: resultResponse)

            }
        }
        return Resource.Error(response.message())
    }

    fun getSpecificNews6(source: String) = viewModelScope.launch {
        safeSpecificNewsCall6(source)
    }


    private suspend fun safeSpecificNewsCall6(source: String) {
        specificNews6.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response =
                    newsRepository.getNewsFromSpecificSource(source, specificNewsPage)
                specificNews6.postValue(handleSpecificNewsResponse6(response))
            } else {
                specificNews6.postValue(Resource.Error("No internet connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> specificNews6.postValue(Resource.Error("Network Failure"))
                else -> specificNews6.postValue(Resource.Error("conversion error"))
            }
        }
    }

    private fun handleSpecificNewsResponse6(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                specificNewsPage++
                if (specificNewsResponse6 == null) {
                    specificNewsResponse6 = resultResponse
                } else {
                    val oldArticles = specificNewsResponse6?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(specificNewsResponse6 ?: resultResponse)

            }
        }
        return Resource.Error(response.message())
    }


    fun getEntertainmentNews(countryCode: String, category: String, context: Context) =
        viewModelScope.launch {
            safeEntertainmentNewsCall(countryCode, category, context)
        }

    private suspend fun safeEntertainmentNewsCall(
        countryCode: String,
        category: String,
        context: Context
    ) {
        entertainmentNews.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = newsRepository.getEntertainmentNews(
                    countryCode,
                    category,
                    entertainmentNewsPage
                )
                entertainmentNews.postValue(handleEntertainmentNewsResponse(response))

                //check if the api has data for the selected country if not, set default to US
                if (response.isSuccessful && response.body()?.articles.isNullOrEmpty()) {
                    StyleableToast.makeText(
                        context, "no news found for the selected country",
                        R.style.customToast
                    ).show()

                    StyleableToast.makeText(
                        context, " setting default to US",
                        R.style.customToast
                    ).show()

                    StyleableToast.makeText(
                        context, "you can reset country in settings",
                        R.style.customToast
                    ).show()
                    entertainmentNews.postValue(Resource.Loading())
                    val retryResponse = newsRepository.getEntertainmentNews(
                        "us",
                        "entertainment", entertainmentNewsPage
                    )
                    entertainmentNews.postValue(handleEntertainmentNewsResponse(retryResponse))
                }
            } else {
                entertainmentNews.postValue(Resource.Error("No internet connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> entertainmentNews.postValue(Resource.Error("Network Failure"))
                else -> entertainmentNews.postValue(Resource.Error("conversion error"))
            }
        }
    }

    private fun handleEntertainmentNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                entertainmentNewsPage++
                if (entertainmentNewsResponse == null) {
                    entertainmentNewsResponse = resultResponse
                } else {
                    val oldArticles = entertainmentNewsResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(entertainmentNewsResponse ?: resultResponse)

            }
        }
        return Resource.Error(response.message())

    }


    fun getSportNews(countryCode: String, category: String) = viewModelScope.launch {
        safeSportNewsCall(countryCode, category)
    }

    private suspend fun safeSportNewsCall(countryCode: String, category: String) {
        sportNews.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = newsRepository.getSportNews(countryCode, category, sportNewsPage)
                sportNews.postValue(handleSportNewsResponse(response))

                if (response.isSuccessful && response.body()?.articles.isNullOrEmpty()) {
                    sportNews.postValue(Resource.Loading())
                    val retryResponse = newsRepository.getSportNews(
                        "us",
                        "sport", sportNewsPage
                    )
                    sportNews.postValue(handleSportNewsResponse(retryResponse))
                }
            } else {
                sportNews.postValue(Resource.Error("No internet connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> sportNews.postValue(Resource.Error("Network Failure"))
                else -> sportNews.postValue(Resource.Error("conversion error"))
            }
        }
    }


    private fun handleSportNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                sportNewsPage++
                if (sportNewsResponse == null) {
                    sportNewsResponse = resultResponse
                } else {
                    val oldArticles = sportNewsResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(sportNewsResponse ?: resultResponse)

            }
        }
        return Resource.Error(response.message())
    }


    fun getScienceNews(countryCode: String, category: String) = viewModelScope.launch {
        safeScienceNewsCall(countryCode, category)
    }

    private suspend fun safeScienceNewsCall(countryCode: String, category: String) {
        scienceNews.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = newsRepository.getScienceNews(countryCode, category, scienceNewsPage)
                scienceNews.postValue(handleScienceNewsResponse(response))

                if (response.isSuccessful && response.body()?.articles.isNullOrEmpty()) {
                    scienceNews.postValue(Resource.Loading())
                    val retryResponse = newsRepository.getScienceNews(
                        "us",
                        "science", scienceNewsPage
                    )
                    scienceNews.postValue(handleBusinessNewsResponse(retryResponse))
                }
            } else {
                scienceNews.postValue(Resource.Error("No internet connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> scienceNews.postValue(Resource.Error("Network Failure"))
                else -> scienceNews.postValue(Resource.Error("conversion error"))
            }
        }
    }


    private fun handleScienceNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                scienceNewsPage++
                if (scienceNewsResponse == null) {
                    scienceNewsResponse = resultResponse
                } else {
                    val oldArticles = scienceNewsResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(scienceNewsResponse ?: resultResponse)

            }
        }
        return Resource.Error(response.message())
    }

    fun getAllBreakingNews(countryCode: String) = viewModelScope.launch {
        safeAllBreakingNewsCall(countryCode)
    }

    private suspend fun safeAllBreakingNewsCall(countryCode: String) {
        allBreakingNews.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = newsRepository.getAllBreakingNews(countryCode, allBreakingNewsPage)
                allBreakingNews.postValue(handleAllBreakingNewsResponse(response))

                if (response.isSuccessful && response.body()?.articles.isNullOrEmpty()) {
                    allBreakingNews.postValue(Resource.Loading())
                    val retryResponse = newsRepository.getAllBreakingNews(
                        "us",
                        allBreakingNewsPage
                    )
                    allBreakingNews.postValue(handleBusinessNewsResponse(retryResponse))
                }
            } else {
                allBreakingNews.postValue(Resource.Error("No internet connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> allBreakingNews.postValue(Resource.Error("Network Failure"))
                else -> allBreakingNews.postValue(Resource.Error("conversion error"))
            }
        }
    }

    private fun handleAllBreakingNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                allBreakingNewsPage++
                if (allBreakingNewsResponse == null) {
                    allBreakingNewsResponse = resultResponse
                } else {
                    val oldArticles = allBreakingNewsResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(allBreakingNewsResponse ?: resultResponse)

            }
        }
        return Resource.Error(response.message())
    }


    fun getSearchNews(searchQuery: String) = viewModelScope.launch {
        safeSearchNewsCall(searchQuery)
    }

    private suspend fun safeSearchNewsCall(searchQuery: String) {
        searchNews.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = newsRepository.searchNews(searchQuery, searchNewsPage)
                searchNews.postValue(handleSearchNewsResponse(response))
            } else {
                searchNews.postValue(Resource.Error("No internet connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> searchNews.postValue(Resource.Error("Network Failure"))
                else -> searchNews.postValue(Resource.Error("conversion error"))
            }
        }
    }

    private fun handleSearchNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let {
                searchNewsPage++
                if (searchNewsResponse == null) {
                    searchNewsResponse = it
                } else {
                    val oldArticle = searchNewsResponse?.articles
                    val newArticle = it.articles
                    oldArticle?.addAll(newArticle)
                }
                return Resource.Success(searchNewsResponse ?: it)
            }
        }
        return Resource.Error(response.message())
    }


    fun saveArticle(article: Article) = viewModelScope.launch {
        newsRepository.upsert(article)
    }

    fun deleteArticle(article: Article) = viewModelScope.launch {
        newsRepository.deleteArticle(article)
    }

    fun getSavedNews() = newsRepository.getSavedNews()

    suspend fun deleteAllArticles() = newsRepository.deleteAllArticles()

    private fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<NewsApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities =
                connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false

            return when {
                capabilities.hasTransport(TRANSPORT_WIFI) -> true
                capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
                capabilities.hasTransport(TRANSPORT_VPN) -> true
                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo?.run {
                return when (type) {
                    TYPE_WIFI -> true
                    TYPE_MOBILE -> true
                    TYPE_ETHERNET -> true
                    TYPE_VPN -> true
                    else -> false
                }
            }
        }
        return false
    }
}