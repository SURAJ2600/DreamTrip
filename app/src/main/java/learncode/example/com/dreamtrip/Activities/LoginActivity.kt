package learncode.example.com.dreamtrip.Activities

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_login_screen_.*
import learncode.example.com.dreamtrip.ApiCient
import learncode.example.com.dreamtrip.DataModelClass.User
import learncode.example.com.dreamtrip.R
import learncode.example.com.dreamtrip.Utility.SessionManager
import learncode.example.com.dreamtrip.Utility.Util
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    /*Variable declaration*/

    var mUsername: EditText? = null
    var mPassword: EditText? = null
    var mLogin_button: Button? = null
    val TAG = LoginActivity::class.java.simpleName
    val RetrofitClient = ApiCient
    private var mContext: Context? = null
    var mProgressDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContentView(R.layout.activity_login_screen_)
        mContext = this@LoginActivity


        /* Intializee the view from layouts*/
        Init();

        GetIntent();

/**
 *
 * Validate a input data and Submit the form
 *
 *
 * */
        mLogin_button?.setOnClickListener(View.OnClickListener {


            if (mUsername?.getText().toString().trim().length == 0) {

                Toast.makeText(mContext, getString(R.string.enter_email), Toast.LENGTH_SHORT).show()
            } else if (!Util.isValidEmail(mUsername?.getText().toString().trim())) {
                Toast.makeText(mContext, "Enter a valid email", Toast.LENGTH_SHORT).show()

            } else if (mPassword?.getText().toString().trim().length == 0) {
                Toast.makeText(mContext, getString(R.string.enter_pwd), Toast.LENGTH_SHORT).show()

            } else {


                try {
                    if (Util.isConnected(mContext as LoginActivity)) {
                        mProgressDialog=Util.ShowProgressView(mContext as LoginActivity)
                        mProgressDialog?.show()
                        val Request = JsonObject()
                        Request.addProperty("email", mUsername?.getText().toString().trim())
                        Request.addProperty("password", mPassword?.getText().toString().trim())
                        var networkrequest = ApiCient.create().UserLogin(Request)

                        networkrequest.enqueue(object : retrofit2.Callback<JsonElement> {

                            override fun onResponse(call: Call<JsonElement>?, response: Response<JsonElement>?) {

                                if (response != null) {
                                    mProgressDialog?.dismiss()
                                     var mResponse = JSONObject(response.body().toString())

                                    if (mResponse.getString("iserror").equals("Yes")) {
                                        Toast.makeText(mContext, "" + mResponse.getString("message"), Toast.LENGTH_SHORT)
                                                .show()
                                    } else {

                                        if (mResponse.has("data")) {
                                            var dataarray = mResponse.getJSONArray("data")
                                            var gson = Gson()

                                            if (dataarray.length() != 0) {
                                                for (i in 0..dataarray.length() - 1) {
                                                    var user_object = dataarray.getJSONObject(i).toString()

                                                    var user = gson.fromJson(user_object, User::class.java)


                                                    SessionManager.saveSession(Util.session_user_id, "" + user.user_id, mContext)
                                                    SessionManager.saveSession(Util.session_user, "" + user, mContext)

                                                    SessionManager.saveSession(Util.session_email, "" + user?.email, mContext)
                                                    SessionManager.saveSession(Util.session_user_image,""+user?.userimage,mContext)
                                                    val mIntent = Intent(mContext, HomeScreen_Activity::class.java)
                                                    startActivity(mIntent)
                                                    finish()

                                                }


                                            }


                                        }
                                    }


                                }

                            }

                            override fun onFailure(call: Call<JsonElement>?, t: Throwable?) {
                            }


                        })
                    } else {
                        Toast.makeText(mContext, "Please connect to intenet ", Toast.LENGTH_SHORT)
                                .show()
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }


        })



        register_user.setOnClickListener(View.OnClickListener {

            val mIntent = Intent(mContext, RegisterScreen_Activity::class.java)
            startActivity(mIntent)
        })

    }

    private fun GetIntent() {

        var intent=intent

        if(intent.hasExtra("username")) {

            intent.getStringExtra("username")
            intent.getStringExtra("password")

            mUsername?.setText("" + intent.getStringExtra("username"))
            mPassword?.setText("" + intent.getStringExtra("password"))
        }
        else{

        }

    }




    /***
     * Init views
     * */
    private fun Init() {


        mUsername = findViewById<EditText>(R.id.user_name) as EditText

        mPassword = findViewById<EditText>(R.id.user_password) as EditText

        mLogin_button = findViewById<Button>(R.id.btn_login)

    }


}
