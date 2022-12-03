package com.project.todoapp.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.project.todoapp.MainActivity
import com.project.todoapp.R
import com.project.todoapp.adapter.NewsAdapter
import com.project.todoapp.api.NewsApi
import com.project.todoapp.api.NewsDataFromJson
import com.project.todoapp.models.Article
import org.json.JSONException
import org.json.JSONObject
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Use the [NewsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class NewsFragment : Fragment() {
    private lateinit var fcontext: Context
    private lateinit var recyclerView: RecyclerView
    private var newsAdapter: NewsAdapter? = null
    private var newsList = ArrayList<Article>()
    private lateinit var fview: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view: View = inflater.inflate(R.layout.fragment_news, container, false);

        (activity as MainActivity?)!!.toolbar.title = "News"
        fcontext = container!!.context
        recyclerView = view.findViewById(R.id.recyclerViewNews)
        fview = view
        getNewsList()
        return view
    }

    override fun onResume() {
        super.onResume()
        if (newsAdapter != null) {
            getNewsList()
            newsAdapter!!.notifyDataSetChanged()
        }
    }

    private fun getNewsList() {

        var call = Retrofit.Builder()
            .baseUrl("https://newsapi.org/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create<NewsApi>().getTopHeadlines()

        call.enqueue(object : Callback<NewsDataFromJson> {
            override fun onResponse(
                call: Call<NewsDataFromJson>,
                response: Response<NewsDataFromJson>
            ) {

                if (response.isSuccessful) {

                    val body = response.body()

                    if (body != null) {
                        val tempNewsList = ArrayList<Article>()
                        for (article in body.articles)
                            tempNewsList.add(article)

                        newsList = tempNewsList
                    }

                } else {

                    val jsonObj: JSONObject?

                    try {
                        jsonObj = response.errorBody()?.string()?.let { JSONObject(it) }
                        if (jsonObj != null) {
                            Log.d("JSONException", "" + jsonObj.getString("message"))
                            Snackbar.make(fview, jsonObj.getString("message"), Snackbar.LENGTH_SHORT).show()
                        }
                    } catch (e: JSONException) {
                        Log.d("JSONException", "" + e.message)
                        Snackbar.make(fview, "" + e.message, Snackbar.LENGTH_SHORT).show()
                    }
                }

                recyclerView.layoutManager = LinearLayoutManager(
                    fcontext,
                    LinearLayoutManager.VERTICAL,
                    false
                )
                newsAdapter = NewsAdapter(newsList)

                recyclerView.adapter = newsAdapter
            }

            override fun onFailure(call: Call<NewsDataFromJson>, t: Throwable) {

                Snackbar.make(fview, t.localizedMessage as String, Snackbar.LENGTH_SHORT).show()
                Log.d("err_msg", "msg" + t.localizedMessage)
            }
        })


    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment NewsFragment.
         */

    }
}