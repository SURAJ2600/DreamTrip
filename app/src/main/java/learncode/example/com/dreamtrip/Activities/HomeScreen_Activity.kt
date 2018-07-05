package learncode.example.com.dreamtrip.Activities

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.example.admin2base.myapplication.utils.AppUtils
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.squareup.picasso.Picasso
import learncode.example.com.dreamtrip.Adapter.DestinationAdapter
import learncode.example.com.dreamtrip.ApiCient
import learncode.example.com.dreamtrip.DataModelClass.DestinationItem
import learncode.example.com.dreamtrip.R
import learncode.example.com.dreamtrip.Utility.SessionManager
import learncode.example.com.dreamtrip.Utility.Util
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response



class HomeScreen_Activity : AppCompatActivity() {
    /*Variable declaration*/


    val TAG = HomeScreen_Activity::class.java.simpleName
    val RetrofitClient = ApiCient

    var mDestinationlist = ArrayList<DestinationItem>()

    var adapter: DestinationAdapter? = null

    var mProfile:ImageView?=null
    var mBack_btn:ImageView?=null
    var mTitle:TextView?=null

    var edit_profile:ImageView?=null

    var mRecylerView: RecyclerView? = null
    private var mContext: Context? = null

    var mProgressDialog: Dialog? = null
    //Permision code that will be checked in the method onRequestPermissionsResult
    private val LOCATION_PERMISIION_CODE = 23


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_home_screen_)
        mContext = this@HomeScreen_Activity

        Init()
        mTitle?.text = getString(R.string.home)

        edit_profile?.visibility=View.GONE
        try {
            Picasso.get().load(SessionManager.getSession(Util.session_user_image,mContext))
                    .placeholder(R.mipmap.ic_profile).into(mProfile);

        }
        catch (e:Exception)
        {
            e.printStackTrace()
        }

        /**
         *
         * Getting list of places
         *
         *
         * */

        if(isReadStorageAllowed()){
            //If permission is already having then showing the toast
             //Existing the method with return

            GetListAttractionSpot();

        }
        else{
            requestStoragePermission();
        }

        //If the app has not the permission then asking for the permission



        /**
         *
         * Redirect to the profile page
         *
         *
         * */
        mProfile?.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        })


        mBack_btn?.setOnClickListener(View.OnClickListener {
            finish();
        })

        mRecylerView?.layoutManager = LinearLayoutManager(mContext)
        // recylerview_Auctionbook?.setNestedScrollingEnabled(false);
        adapter = DestinationAdapter(mContext as HomeScreen_Activity, mDestinationlist)

        mRecylerView?.adapter = adapter


    }

    private fun Init() {

        mProfile = findViewById<ImageView>(R.id.profile)
        mBack_btn = findViewById<ImageView>(R.id.back_btn)
        mTitle = findViewById<TextView>(R.id.mTitle)
        edit_profile=findViewById<ImageView>(R.id.edit_profile)
        mRecylerView = findViewById<RecyclerView>(R.id.recyler_destination) as RecyclerView


    }

    /**
     *
     *Getting place list using RESTAPI
     *
     *
     * */
    private fun GetListAttractionSpot() {

        try {
            if (Util.isConnected(mContext as HomeScreen_Activity)) {
                mProgressDialog=Util.ShowProgressView(mContext as HomeScreen_Activity)
                mProgressDialog?.show()
                val Request = JsonObject()
                Request.addProperty("email", SessionManager.getSession(Util.session_email, mContext))


                var network_request = RetrofitClient.create().GetListAttractionDestination(Request)
                network_request.enqueue(object : Callback<JsonElement> {
                    override fun onResponse(call: Call<JsonElement>?, response: Response<JsonElement>?) {

                        if (response != null) {
                            var jsonobject = JSONObject(response.body().toString())
                            AppUtils.instance.debugLog("-----", "RES==" + jsonobject)


                            if (jsonobject.getString("iserror").equals("No")) {
                                mProgressDialog?.dismiss()

                                if (jsonobject.has("data")) {
                                    var dataarray = jsonobject.getJSONArray("data")

                                    var gson = Gson()

                                    if (dataarray.length() != 0) {

                                        mDestinationlist.clear()
                                        for (i in 0..dataarray.length() - 1) {

                                            var deetination_object = dataarray.getJSONObject(i).toString()

                                            var destination_model = gson.fromJson(deetination_object, DestinationItem::class.java)
                                            mDestinationlist.add(destination_model)

                                        }

                                        mRecylerView?.adapter?.notifyDataSetChanged()


                                    }


                                }


                            }


                        } else {

                        }

                    }

                    override fun onFailure(call: Call<JsonElement>?, t: Throwable?) {

                        if (t != null) {
                            t.printStackTrace()
                        };
                    }

                })
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun isReadStorageAllowed(): Boolean {
        //Getting the permission status
        val result = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)

        //If permission is granted returning true
        return if (result == PackageManager.PERMISSION_GRANTED) true else false

        //If permission is not granted returning false
    }

    //Requesting permission
    private fun requestStoragePermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }

        //And finally ask for the permission
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISIION_CODE)
    }

    //This method will be called when the user will tap on allow or deny
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {

        //Checking the request code of our request
        if (requestCode == LOCATION_PERMISIION_CODE) {

            //If permission is granted
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                GetListAttractionSpot();
              } else {
                GetListAttractionSpot();
               }
        }
    }


}
