package learncode.example.com.dreamtrip

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit



/**
 * Created by Suraj on 23/5/18.
 */

class ApiCient private  constructor() {


    companion object {
        fun getAPIurl(): String {

          return "http://mccollinsmedia.com/myproject/service/";
        }
        fun GetClient(): OkHttpClient {


            return OkHttpClient.Builder()
                    .connectTimeout(120, TimeUnit.SECONDS)
                    .writeTimeout(120, TimeUnit.SECONDS)

                    .readTimeout(120, TimeUnit.SECONDS)
                    .build()
        }

        fun create(): APISERVICE {

            val retrofit = Retrofit.Builder()
                    .client(GetClient())
                    .baseUrl(getAPIurl())
                    .addConverterFactory(GsonConverterFactory.create(getGson()))
                    .build()

            return retrofit.create(APISERVICE::class.java)
        }

        fun getGson(): Gson {
            return   GsonBuilder()
                    .setLenient()
                    .create();
        }
    }
}
