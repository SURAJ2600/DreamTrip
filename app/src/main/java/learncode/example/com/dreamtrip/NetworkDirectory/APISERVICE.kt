package learncode.example.com.dreamtrip

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part


/**
 * Created by suraj on 23/5/18.
 */

interface APISERVICE {

    /*API END POINT*/


    @POST("checklogin")
    fun UserLogin(@Body body: JsonObject): Call<JsonElement>


    @POST("registerUser")
    fun UserRegisteration(@Body body: JsonObject): Call<JsonElement>


    @Multipart
    @POST("updateUser")
    fun UpdateUserDetails(@Part("id") id: RequestBody,
                          @Part("fname") firstname: RequestBody,
                          @Part("lname") lastname: RequestBody,
                          @Part("gender") gender: RequestBody,
                          @Part("dob") dob: RequestBody,
                          @Part("email") email: RequestBody,
                          @Part("mobile") mobile: RequestBody,
                          @Part image: MultipartBody.Part): Call<JsonElement>


    @POST("listAttractions")
    fun GetListAttractionDestination(@Body body: JsonObject): Call<JsonElement>

    @POST("getUser")
    fun GetUserById(@Body body: JsonObject): Call<JsonElement>

}