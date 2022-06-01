package com.razmadze.currencyexchange

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.razmadze.currencyexchange.models.AllData
import okhttp3.*
import java.io.IOException
import java.util.concurrent.CountDownLatch


class JSONHelper {
    private val client = OkHttpClient()
    private var allData: AllData? = null

    internal fun deserializeJSON(strURL: String): AllData? {
        val request = Request.Builder()
            .url(strURL)
            .build()

        val countDownLatch = CountDownLatch(1)
        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    Log.e("INFO", "onResponse is successful")
                    val jsonResponse = response.body?.string()?.drop(1)?.dropLast(1)
                    try {
                        allData = Gson().fromJson(jsonResponse, AllData::class.java)
                    } catch (exc: JsonSyntaxException) {
                        Log.e("ERROR", exc.message.toString())
                    }
                }
                countDownLatch.countDown()
            }
            override fun onFailure(call: Call, e: IOException) {
                Log.e("INFO", "failure happened while getting request.")
                countDownLatch.countDown()
            }
        })
        countDownLatch.await()
        return allData
    }
}