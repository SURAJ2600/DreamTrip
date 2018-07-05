package learncode.example.com.dreamtrip.Activities

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.*
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_register_screen_.*
import learncode.example.com.dreamtrip.ApiCient
import learncode.example.com.dreamtrip.R
import learncode.example.com.dreamtrip.Utility.Util
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import java.util.*


@SuppressLint("Registered")
class RegisterScreen_Activity : AppCompatActivity() {

    /*Variable declaration*/

    var mFirstname: EditText? = null
    var mLastname: EditText? = null
    var mPassword: EditText? = null
    var mConfirmPassword: EditText? = null
    var mEmailaddress: EditText? = null
    var mMobilenumber: EditText? = null
    var mDob: TextView? = null

    var mGender: String = ""

    val TAG = RegisterScreen_Activity::class.java.simpleName
    private var mContext: Context? = null

    val mGenderArray = arrayOf("Select Gender", "Male", "Female")
    var mProgressDialog: Dialog? = null
    val RetrofitClient = ApiCient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_register_screen_)

        mContext = this@RegisterScreen_Activity


        /*Initilaize views*/

        Init();

        //Adapter for spinner Gender
        val adapter = ArrayAdapter<String>(this, R.layout.customtextview_spinner, mGenderArray)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        user_genderspinner.adapter = adapter

        //item selected listener for spinner
        user_genderspinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                mGender = mGenderArray[p2]
                //   Toast.makeText(mContext,""+mGender,Toast.LENGTH_SHORT).show()
            }

        }

        /**
         *
         * Get basic detail from user and submit a values
         *
         *
         * */
        btn_register.setOnClickListener(View.OnClickListener {


            if (mFirstname?.getText().toString().trim().length == 0) {
                Toast.makeText(mContext, getString(R.string.enter_first_name), Toast.LENGTH_SHORT).show()

            } else if (mLastname?.getText().toString().trim().length == 0) {
                Toast.makeText(mContext, getString(R.string.enter_lastname), Toast.LENGTH_SHORT).show()

            } else if (mEmailaddress?.getText().toString().trim().length == 0) {
                Toast.makeText(mContext, getString(R.string.enteremail), Toast.LENGTH_SHORT).show()

            } else if (!Util.isValidEmail(mEmailaddress?.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.enter_email), Toast.LENGTH_SHORT).show()

            } else if (mPassword?.getText().toString().trim().length < 6) {
                Toast.makeText(mContext, getString(R.string.enter_pwd), Toast.LENGTH_SHORT).show()

            } else if (mConfirmPassword?.getText().toString().trim().length == 0) {
                Toast.makeText(mContext, getString(R.string.pwd_lenth), Toast.LENGTH_SHORT).show()

            } else if (mConfirmPassword == mPassword) {
                Toast.makeText(mContext, getString(R.string.pwd_mismatch), Toast.LENGTH_SHORT).show()

            } else if (mMobilenumber?.getText().toString().trim().length == 0) {
                Toast.makeText(mContext, getString(R.string.enter_mob_num), Toast.LENGTH_SHORT).show()

            } else if (mMobilenumber?.getText().toString().trim().length < 8) {
                Toast.makeText(mContext, "" + mGender, Toast.LENGTH_SHORT).show()
                Toast.makeText(mContext, getString(R.string.min_number), Toast.LENGTH_SHORT).show()


            } else if (mGender.equals(mGenderArray[0])) {
                Toast.makeText(mContext, getString(R.string.select_gender), Toast.LENGTH_SHORT).show()

            } else if (mDob?.getText().toString().trim().length == 0) {
                Toast.makeText(mContext, getString(R.string.dob), Toast.LENGTH_SHORT).show()

            } else {

                try {

                    if (Util.isConnected(mContext as RegisterScreen_Activity)) {


                        mProgressDialog = Util.ShowProgressView(mContext as RegisterScreen_Activity)
                        mProgressDialog?.show()
                        try {


                            val Request = JsonObject()
                            Request.addProperty("fname", mFirstname?.getText().toString().trim())
                            Request.addProperty("lname", mLastname?.getText().toString().trim())
                            Request.addProperty("email", mEmailaddress?.getText().toString().trim())
                            Request.addProperty("mobile", mMobilenumber?.getText().toString().trim())
                            Request.addProperty("password", mPassword?.getText().toString().trim())
                            Request.addProperty("cpassword", mConfirmPassword?.getText().toString().trim())
                            Request.addProperty("dob", mDob?.getText().toString().trim())
                            Request.addProperty("gender", mGender)
                            var networkrequest = ApiCient.create().UserRegisteration(Request)

                            networkrequest.enqueue(object : retrofit2.Callback<JsonElement> {

                                override fun onResponse(call: Call<JsonElement>?, response: Response<JsonElement>?) {

                                    if (response != null) {


                                        var responce_object = JSONObject(response.body().toString())

                                        if (responce_object.getString("iserror").equals("Yes")) {

                                            mProgressDialog?.dismiss()

                                            Toast.makeText(mContext, "" + responce_object.getString("data"), Toast.LENGTH_SHORT)
                                                    .show()
                                        } else {
                                            mProgressDialog?.dismiss()

                                            val mIntent = Intent(mContext, LoginActivity::class.java)
                                            mIntent.putExtra("username", mEmailaddress?.getText().toString().trim())
                                            mIntent.putExtra("password", mPassword?.getText().toString().trim())
                                            startActivity(mIntent)
                                            Toast.makeText(mContext, "" + responce_object.getString("message"), Toast.LENGTH_SHORT)
                                                    .show()
                                        }


                                    }

                                }

                                override fun onFailure(call: Call<JsonElement>?, t: Throwable?) {
                                    mProgressDialog?.dismiss()
                                    Toast.makeText(mContext, getString(R.string.failed), Toast.LENGTH_SHORT)
                                            .show()
                                }


                            })
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    } else {
                        Toast.makeText(mContext, getString(R.string.no_internet), Toast.LENGTH_SHORT)
                                .show()
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }


        })

        mDob?.setOnClickListener(View.OnClickListener { view ->

            SetDateOfBirth();
        })




        signin_textview.setOnClickListener(View.OnClickListener {

            finish()
        })


    }

    private fun SetDateOfBirth() {

        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)


        val dpd = DatePickerDialog(mContext, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            // Display Selected date in textbox
            mDob?.setText("" + dayOfMonth + "-" + monthOfYear + "-" + year)


        }, year, month, day)
        dpd.show()
    }

    private fun Init() {

        mFirstname = findViewById<EditText>(R.id.user_firstname) as EditText
        mLastname = findViewById<EditText>(R.id.user_lastname) as EditText
        mEmailaddress = findViewById<EditText>(R.id.user_email) as EditText
        mPassword = findViewById<EditText>(R.id.user_password) as EditText
        mConfirmPassword = findViewById<EditText>(R.id.user_confirmpassword) as EditText
        mMobilenumber = findViewById<EditText>(R.id.user_mobile) as EditText
        mDob = findViewById<TextView>(R.id.user_dob) as TextView


    }


}
