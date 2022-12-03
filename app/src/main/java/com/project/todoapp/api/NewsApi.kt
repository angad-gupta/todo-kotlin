package com.project.todoapp.api

import com.project.todoapp.models.Article
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

data class NewsDataFromJson(
    val articles: List<Article>,
    val status: String,
    val totalResults: Int
)

interface NewsApi {

    @GET("top-headlines")
    fun getTopHeadlines(
        @Query("country")
        countryCode: String = "ca",
        @Query("page")
        pageNumber: Int = 1,
        @Query("pageSize")
        pageSize: Int = 20,
        @Query("apiKey")
        apiKey: String = "79450571692043539c613d261df462b2"
    ): Call<NewsDataFromJson>
}