package com.depravo.kursach_client

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import java.io.IOException
import android.widget.ArrayAdapter
import com.example.myclient.Retrofit.IUploadAPI
import com.example.myclient.Retrofit.RetrofitClient
import retrofit2.Callback


class MainActivity : AppCompatActivity() {
    private var editText: EditText? = null
    private var button: Button? = null
    private var listView: ListView? = null
    private var okHttpClient: OkHttpClient? = null
    lateinit var mService: IUploadAPI
    private val apiUpload: IUploadAPI
        get() = RetrofitClient.client.create(IUploadAPI::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mService = apiUpload
        editText = findViewById<EditText>(R.id.dummy_text)
        listView = findViewById<ListView>(R.id.listView)
        button = findViewById<Button>(R.id.dummy_send)
        okHttpClient = OkHttpClient()
        button?.setOnClickListener(View.OnClickListener {
            val dummyText = editText?.getText().toString()
            val body = MultipartBody.Part.createFormData("sample", dummyText)
            Thread(Runnable {
                mService.uploadFile(body)
                    .enqueue(object: Callback<String> {

                        override fun onResponse(
                            call: retrofit2.Call<String>,
                            response: retrofit2.Response<String>
                        ) {
                            Toast.makeText(this@MainActivity, "LIFE IS GOOD!!!!", Toast.LENGTH_SHORT).show()
                        }

                        override fun onFailure(call: retrofit2.Call<String>, t: Throwable) {

                            Toast.makeText(this@MainActivity, t.message, Toast.LENGTH_SHORT).show()
                        }

                    })
            }).start()
            /*val dummyText = editText?.getText().toString()

            // we add the information we want to send in
            // a form. each string we want to send should
            // have a name. in our case we sent the
            // dummyText with a name 'sample'
            val formbody: RequestBody = FormBody.Builder()
                .add("sample", dummyText)
                .build()

            // while building request
            // we give our form
            // as a parameter to post()
            val request = Request.Builder().url("http://192.168.100.10:8080/rec")
                .post(formbody)
                .build()
            okHttpClient!!.newCall(request).enqueue(object : Callback {
                override fun onFailure(
                    call: Call,
                    e: IOException
                ) {
                    runOnUiThread {
                        Toast.makeText(
                            applicationContext,
                            "server down",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    var text = response.body!!.string()
                    runOnUiThread {
                        val res = parseText(text).toTypedArray()
                        val adapter: ArrayAdapter<*>
                        adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_list_item_1, res)

                        listView!!.adapter = adapter
                    }
                }
            })*/
        })
    }

    fun parseText(text:String): MutableList<String> {
        val words : Array<String> = emptyArray()
        val res = text.substring(1, text.length - 1)
        var parts = res.split(", ").toMutableList()
        for(i in 0..(parts.size - 1)) {
            parts[i] = parts[i].substring(1, parts[i].length - 1)
        }
        return parts
    }
}
