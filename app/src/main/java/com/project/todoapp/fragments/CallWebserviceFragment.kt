package com.project.todoapp.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.project.todoapp.MainActivity
import com.project.todoapp.R
import com.project.todoapp.adapter.NewsAdapter
import com.project.todoapp.api.NewsApi
import com.project.todoapp.api.NewsDataFromJson
import com.project.todoapp.models.Article
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import java.util.ArrayList


/**
 * A simple [Fragment] subclass.
 * Use the [CallWebserviceFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CallWebserviceFragment : Fragment(){
    private lateinit var fcontext: Context
    private lateinit var fview: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view: View = inflater.inflate(R.layout.fragment_call_webservice, container, false);

        view.findViewById<MaterialButton>(R.id.callWebserviceBtn).setOnClickListener { v ->
            callWebService()
        }

        (activity as MainActivity?)!!.toolbar.title = "Web Service"
        fcontext = container!!.context
        fview = view

        return view
    }

    private fun callWebService() {
        var jsonDataText = fview.findViewById<TextView>(R.id.jsonDataText)
        jsonDataText.text = "Loading..."
        var call = Retrofit.Builder()
            .baseUrl("https://newsapi.org/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create<NewsApi>().getTopHeadlines(pageSize = 1)

        call.enqueue(object: Callback<NewsDataFromJson> {

            override fun onResponse(call: Call<NewsDataFromJson>, response: Response<NewsDataFromJson>) {

                if (response.isSuccessful) {

                    val body = response.body()

                    val gson = GsonBuilder().setPrettyPrinting().create()
                    val prettyJsonString = gson.toJson(body, NewsDataFromJson::class.java)
                    jsonDataText.text = prettyJsonString

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
                    jsonDataText.text = "Error!"
                }
            }

            override fun onFailure(call: Call<NewsDataFromJson>, t: Throwable) {
                //handle error here
                Snackbar.make(fview, t.localizedMessage as String, Snackbar.LENGTH_SHORT).show()
                jsonDataText.text = "Error!"
            }
        })
    }

}