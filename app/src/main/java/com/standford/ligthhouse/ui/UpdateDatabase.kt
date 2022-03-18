package com.standford.ligthhouse.ui

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.standford.ligthhouse.R
import com.standford.ligthhouse.api.APIClient
import com.standford.ligthhouse.api.APIInterface
import com.standford.ligthhouse.db.SQLiteHelper
import com.standford.ligthhouse.model.ApiData
import com.standford.ligthhouse.model.Data
import com.standford.ligthhouse.utility.Util
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UpdateDatabase : AppCompatActivity() {
    var dbHelper: SQLiteHelper? = null
    var apiInterface: APIInterface? = null
    var totalPage: String? = "0"
    var nextPage: String? = "1"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_database)
        dbHelper = SQLiteHelper(this@UpdateDatabase)
        dbHelper!!.open()

        getAndSetData()
    }

    private fun getAndSetData() {

        Log.e("MainActivity", "getAndSetData()")

        Util.showProgress(this@UpdateDatabase, "Loading..." + nextPage)

        apiInterface = APIClient.client?.create(APIInterface::class.java)

        val call: Call<ApiData>? = apiInterface?.doGetAllData(nextPage!!, "100")
        call!!.enqueue(object : Callback<ApiData> {
            override fun onResponse(call: Call<ApiData>, response: Response<ApiData>) {
                if (response.body() != null) {
                    val templateData = response.body()
                    totalPage = templateData!!.data!!.lastPage
                    nextPage = templateData.data!!.nextPage
                    if (nextPage.equals(totalPage)) {
                        nextPage = "77"
                    }
                    var i: Int = 0
                    loadDataBaseLinkNew(templateData.data!!.data, i)

                } else {
                    Toast.makeText(
                        this@UpdateDatabase,
                        "Something went wrong",
                        Toast.LENGTH_LONG
                    ).show()

                }
            }

            override fun onFailure(call: Call<ApiData>, t: Throwable) {
                Util.dismissProgress()
                Log.e("MainActivity", "onFailure:" + t.message)
                Toast.makeText(
                    this@UpdateDatabase,
                    "onFailure:==>" + t.message,
                    Toast.LENGTH_LONG
                ).show()
                call.cancel()
            }
        })
    }

    fun loadDataBaseLinkNew(templateData: List<Data>?, i: Int) {
        try {
            if (i > templateData!!.size - 1) {
                if (nextPage!!.toInt() > totalPage!!.toInt()) {
                    setResult(RESULT_OK)
                    Util.dismissProgress()
                    finish()
                    return
                } else {
                    getAndSetData()
                }
            }
            val gson = Gson()

            dbHelper!!.stuffInsert(
                templateData[i].createdDate,
                templateData[i].id,
                templateData[i].profileId,
                templateData[i].identifier,
                templateData[i].topline,
                templateData[i].rank,
                templateData[i].score,
                templateData[i].country,
                templateData[i].language,
                gson.toJson(templateData[i].writeup).toString(),
                templateData[i].criteria.toString(),
                templateData[i].active,
                templateData[i].healthGuard,
                templateData[i].locale
            )

            loadDataBaseLinkNew(templateData, i + 1)
        } catch (ex: Exception) {
            Log.i("TAG", "json Exception->${ex.message}")
            ex.printStackTrace()
        }
    }
}