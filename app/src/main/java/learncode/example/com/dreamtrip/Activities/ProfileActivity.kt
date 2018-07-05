package learncode.example.com.dreamtrip.Activities

import android.Manifest
import android.Manifest.permission.CAMERA
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.support.annotation.RequiresApi
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.*
import com.example.admin2base.myapplication.utils.AppUtils
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.squareup.picasso.Picasso
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.activity_register_screen_.*
import learncode.example.com.dreamtrip.ApiCient
import learncode.example.com.dreamtrip.R
import learncode.example.com.dreamtrip.Utility.SessionManager
import learncode.example.com.dreamtrip.Utility.Util
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.util.*


class ProfileActivity : AppCompatActivity() {
    /*Variable declaration*/

    var mFirstname: EditText? = null
    var mLastname: EditText? = null
    var mEmailaddress: EditText? = null
    var mMobilenumber: EditText? = null
    var mDob: TextView? = null
    var textview_logout: TextView? = null
    var mGender: String = "Male"
    var mBack_btn: ImageView? = null

    var mTitle: TextView? = null
    var btn_Submit: TextView? = null
    var mProfile: ImageView? = null

    var img_profileimage: ImageView? = null

    var edit_profile: ImageView? = null
    private var mFile: File? = null

    private val REQUEST_READ_PERMISSION = 114
    private val REQUEST_CAMERA_PERMISSION = 115
    private val SELECT_PHOTO = 1001
    private val SELECT_CAMERA = 1002
    private var imageUri: Uri? = null
    private var thumbnail: Bitmap? = null
    private var imageurl = ""
    var mProgressDialog: Dialog? = null
    private var image_param_string = ""

    val TAG = RegisterScreen_Activity::class.java.simpleName
    private var mContext: Context? = null

    val mGenderArray = arrayOf("Select Gender", "Male", "Female")

    val RetrofitClient = ApiCient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_profile_screen_)
        mContext = this@ProfileActivity
        Init()


        GetUserById()

        edit_profile?.visibility = View.VISIBLE
        mProfile?.visibility = View.GONE

        btn_Submit?.visibility = View.GONE
        mTitle?.text = getString(R.string.profile)


        EnabledViews(false)



        mBack_btn?.setOnClickListener(View.OnClickListener {
            finish();
        })

        edit_profile?.setOnClickListener { v ->

            EnabledViews(true)
            btn_Submit?.visibility = View.VISIBLE
            mFirstname?.requestFocus()


        }

        img_profileimage?.setOnClickListener(View.OnClickListener { v ->

            mPickImage()

        })

        //Adapter for spinner
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
         * Edit profile
         *
         *
         * */
        btn_Submit?.setOnClickListener(View.OnClickListener {

            if (mFile == null) {
                Toast.makeText(mContext, getString(R.string.choose_profile), Toast.LENGTH_SHORT).show()

            } else if (mFirstname?.getText().toString().trim().length == 0) {
                Toast.makeText(mContext, getString(R.string.enter_first_name), Toast.LENGTH_SHORT).show()

            } else if (mLastname?.getText().toString().trim().length == 0) {
                Toast.makeText(mContext, getString(R.string.enter_lastname), Toast.LENGTH_SHORT).show()

            } else if (mEmailaddress?.getText().toString().trim().length == 0) {
                Toast.makeText(mContext, getString(R.string.enteremail), Toast.LENGTH_SHORT).show()

            } else if (!Util.isValidEmail(mEmailaddress?.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.enter_email), Toast.LENGTH_SHORT).show()

            } else if (mMobilenumber?.getText().toString().trim().length == 0) {
                Toast.makeText(mContext, getString(R.string.enter_mob_num), Toast.LENGTH_SHORT).show()

            } else if (mMobilenumber?.getText().toString().trim().length < 8) {
                Toast.makeText(mContext, "" + mGender, Toast.LENGTH_SHORT).show()
                Toast.makeText(mContext, getString(R.string.lenth_of_mob), Toast.LENGTH_SHORT).show()


            } else if (mGender.equals(mGenderArray[0])) {
                Toast.makeText(mContext, getString(R.string.select_gender), Toast.LENGTH_SHORT).show()

            } else if (mDob?.getText().toString().trim().length == 0) {
                Toast.makeText(mContext, getString(R.string.enter_dob), Toast.LENGTH_SHORT).show()

            } else {

                try {

                    if (Util.isConnected(mContext as ProfileActivity)) {

                        mProgressDialog = Util.ShowProgressView(mContext as ProfileActivity)
                        mProgressDialog?.show()

                        try {
                            var id =
                                    RequestBody.create(MediaType.parse("multipart/form-data"), SessionManager.getSession(Util.session_user_id, mContext));
                            // adding another part within the multipart request
                            var firstname =
                                    RequestBody.create(MediaType.parse("multipart/form-data"), mFirstname?.getText().toString().trim());
                            var lastname =
                                    RequestBody.create(MediaType.parse("multipart/form-data"), mLastname?.getText().toString().trim());
                            var email =
                                    RequestBody.create(MediaType.parse("multipart/form-data"), mEmailaddress?.getText().toString().trim());
                            var dob =
                                    RequestBody.create(MediaType.parse("multipart/form-data"), mDob?.getText().toString().trim());
                            var mobile =
                                    RequestBody.create(MediaType.parse("multipart/form-data"), mMobilenumber?.getText().toString().trim());
                            var gender =
                                    RequestBody.create(MediaType.parse("multipart/form-data"), mGender);

                            var requestFile =
                                    RequestBody.create(MediaType.parse("multipart/form-data"), mFile);

                            // MultipartBody.Part is used to send also the actual file name
                            var body =
                                    MultipartBody.Part.createFormData("file", mFile?.getName(), requestFile);


                            var networkrequest = ApiCient.create().UpdateUserDetails(id, firstname, lastname, gender, dob, email, mobile, body)

                            networkrequest.enqueue(object : retrofit2.Callback<JsonElement> {

                                override fun onResponse(call: Call<JsonElement>?, response: Response<JsonElement>?) {


                                    if (response != null) {
                                        mProgressDialog?.dismiss()
                                        mFile = null
                                        var responce_object = JSONObject(response.body().toString())
                                        AppUtils.instance.debugLog("RESPONCE FROM PROFILE UPDATE", "------------>" + responce_object)


                                        if (responce_object.getString("iserror").equals("No")) {
                                            Toast.makeText(mContext, responce_object.getString("message"), Toast.LENGTH_SHORT)
                                                    .show()

                                        } else {
                                            Toast.makeText(mContext, getString(R.string.update_error), Toast.LENGTH_SHORT)
                                                    .show()
                                        }
                                        SessionManager.saveSession(Util.session_email, "" + mEmailaddress?.getText().toString().trim(), mContext)
                                        val mIntent = Intent(mContext, HomeScreen_Activity::class.java)
                                        startActivity(mIntent)
                                        finish()
                                    }


                                }

                                override fun onFailure(call: Call<JsonElement>?, t: Throwable?) {
                                    Toast.makeText(mContext, getString(R.string.update_error), Toast.LENGTH_SHORT)
                                            .show()
                                }


                            })
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else {
                        Toast.makeText(mContext, getString(R.string.connect_internet), Toast.LENGTH_SHORT)
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


        /**
         *
         * Logout the User
         *
         *
         * */


        textview_logout!!.setOnClickListener({
            val builder: AlertDialog.Builder
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert)
            } else {
                builder = AlertDialog.Builder(this)
            }
            builder.setTitle("")
                    .setMessage("Are you sure you want to logout?")
                    .setPositiveButton(android.R.string.yes, DialogInterface.OnClickListener { dialog, which ->
                        // continue with delete
                        SessionManager.ClearSession(this);
                        val mIntent = Intent(mContext, LoginActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(mIntent)
                        finish()
                    })
                    .setNegativeButton(android.R.string.no, DialogInterface.OnClickListener { dialog, which ->
                        // do nothingdialog
                        dialog.dismiss()
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show()
        })


    }

    private fun EnabledViews(b: Boolean) {
        mFirstname?.setEnabled(b)
        mLastname?.setEnabled(b)
        mEmailaddress?.setEnabled(b)
        mMobilenumber?.setEnabled(b)
        mDob?.setEnabled(b)
        mTitle?.setEnabled(b)
        img_profileimage?.setEnabled(b)
    }

    /****
     *
     * Get User Profile data
     * */

    private fun GetUserById() {

        if (Util.isConnected(mContext as ProfileActivity)) {
            mProgressDialog = Util.ShowProgressView(mContext as ProfileActivity)
            mProgressDialog?.show()

            val Request = JsonObject()
            Request.addProperty("id", SessionManager.getSession(Util.session_user_id, mContext))


            var network_request = RetrofitClient.create().GetUserById(Request)


            network_request.enqueue(object : Callback<JsonElement> {


                override fun onResponse(call: Call<JsonElement>?, response: Response<JsonElement>?) {
                    var mResponse = JSONObject(response!!.body().toString())
                    if (mResponse.has("data")) {
                        mProgressDialog?.dismiss()
                        var dataarray = mResponse.getJSONArray("data")
                        var gson = Gson()

                        AppUtils.instance.debugLog(TAG,"DDDD"+mResponse)
                        if (dataarray.length() != 0) {

                            mFirstname!!.setText("" + dataarray.getJSONObject(0).getString("fname"))
                            mLastname!!.setText("" + dataarray.getJSONObject(0).getString("lname"))
                            mEmailaddress!!.setText("" + dataarray.getJSONObject(0).getString("email"))
                            mDob!!.setText("" + dataarray.getJSONObject(0).getString("dob"))
                            mMobilenumber!!.setText("" + dataarray.getJSONObject(0).getString("mobile"))

                            if (dataarray.getJSONObject(0).getString("gender").equals("Male")) {
                                user_genderspinner.setSelection(1);
                                mGender = "Male"
                            } else {
                                mGender = "Female"
                                user_genderspinner.setSelection(2);
                            }

                            try {

                                Picasso.get().load(dataarray.getJSONObject(0).getString("image"))
                                        .placeholder(R.mipmap.ic_profile).into(img_profileimage);
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }

                            SessionManager.saveSession(Util.session_email,dataarray.getJSONObject(0).getString("email"),mContext)
                            SessionManager.saveSession(Util.session_email,dataarray.getJSONObject(0).getString("email"),mContext)
                            SessionManager.saveSession(Util.session_user_image,dataarray.getJSONObject(0).getString("image"),mContext)
                        }

                    }
                }
                override fun onFailure(call: Call<JsonElement>?, t: Throwable?) {
                    if (t != null) {
                        t.printStackTrace()
                    }

                }

            })

        } else {
            Toast.makeText(mContext, "Please enable Internet", Toast.LENGTH_SHORT)
                    .show()
        }
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
        mMobilenumber = findViewById<EditText>(R.id.user_mobile) as EditText
        mDob = findViewById<TextView>(R.id.user_dob) as TextView
        textview_logout = findViewById<TextView>(R.id.textview_logout) as TextView
        mBack_btn = findViewById<ImageView>(R.id.back_btn)
        mTitle = findViewById<TextView>(R.id.mTitle)
        btn_Submit = findViewById<TextView>(R.id.btn_Submit)
        mProfile = findViewById<ImageView>(R.id.profile)
        edit_profile = findViewById<ImageView>(R.id.edit_profile)

        img_profileimage = findViewById<ImageView>(R.id.img_profileimage)

    }

    fun mPickImage() {

        val saveDialog = android.app.AlertDialog.Builder(this)
        saveDialog.setTitle("Pick Image")
        saveDialog.setMessage("Choose image from")
        saveDialog.setPositiveButton("GALLERY") { dialog, which -> askForPermissionGallery() }

        saveDialog.setNegativeButton("CAMERA") { dialog, which -> askForCameraPermission() }
        saveDialog.show()
    }

    internal fun openGallery() {
        val i = Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(i, SELECT_PHOTO)

    }

    internal fun openCamera() {
        /*  Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, SELECT_CAMERA);*/
        var values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera")
        imageUri = contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(intent, SELECT_CAMERA)

    }

    fun hasPermissions(): Boolean {

        val result = ContextCompat.checkSelfPermission(this@ProfileActivity, WRITE_EXTERNAL_STORAGE)
        val result1 = ContextCompat.checkSelfPermission(this@ProfileActivity, CAMERA)

        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED

    }


    private fun requestPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(WRITE_EXTERNAL_STORAGE, CAMERA), REQUEST_CAMERA_PERMISSION)


    }


    private fun askForCameraPermission() {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (!hasPermissions()) {

                requestPermission()
            } else {
                openCamera()

            }
        } else {

            openCamera()
        }


    }

    private fun askForPermissionGallery() {//

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        REQUEST_READ_PERMISSION)
                return
            } else {
                openGallery()
            }
        } else {

            openGallery()
        }
    }


    private fun getRealPathFromURI(contentURI: Uri): String {
        val result: String
        val cursor = contentResolver.query(contentURI, null, null, null, null)
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.path
        } else {
            cursor.moveToFirst()
            val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            result = cursor.getString(idx)
            cursor.close()
        }



        return result
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            REQUEST_CAMERA_PERMISSION ->

                if (grantResults.size > 0) {

                    val locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    val cameraAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED

                    if (locationAccepted && cameraAccepted)
                        openCamera()
                    else {

                        /*  Snackbar.make(mView, "Permission Denied, You cannot access the write and camera permission.", Snackbar.LENGTH_LONG).show()
  */
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)) {
                                showMessageOKCancel("You need to allow access to both the permissions",
                                        DialogInterface.OnClickListener { dialog, which ->
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(arrayOf(WRITE_EXTERNAL_STORAGE, CAMERA),
                                                        REQUEST_CAMERA_PERMISSION)
                                            }
                                        })
                                return
                            }
                        } else {
                            if (shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)) {
                                showMessageOKCancel("You need to allow access to both the permissions",
                                        DialogInterface.OnClickListener { dialog, which ->
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(arrayOf(WRITE_EXTERNAL_STORAGE, CAMERA),
                                                        REQUEST_CAMERA_PERMISSION)
                                            }
                                        })
                                return
                            }
                        }

                    }
                }


            REQUEST_READ_PERMISSION -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery()
            } else {
            }
        }

    }


    private fun showMessageOKCancel(message: String, okListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(this@ProfileActivity)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SELECT_PHOTO && resultCode == Activity.RESULT_OK && data != null && data.data != null) {

            val selectedImage = data.data
            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
            val cursor = contentResolver.query(selectedImage!!, filePathColumn, null, null, null) ?: return

            cursor.moveToFirst()

            val columnIndex = cursor.getColumnIndex(filePathColumn[0])
            val filePath = cursor.getString(columnIndex)
            cursor.close()
            image_param_string = "" + filePath

            img_profileimage?.setImageBitmap(BitmapFactory.decodeFile(filePath));

            val mFile1 = File(filePath)
            Log.e("**********", "!!FILE SIZE==>" + mFile1.length() / 1024)


            try {
                mFile = Compressor(this).compressToFile(mFile1)

                Log.e("**********", "!!FILE SIZE==>" + mFile?.length()!! / 1024)
            } catch (e: IOException) {
                e.printStackTrace()
                mFile = File(filePath)
            }


        } else {
            assert(data != null)



            try {
                thumbnail = MediaStore.Images.Media.getBitmap(
                        contentResolver, imageUri)

                imageurl = getRealPathFromURI(imageUri!!)

                val mFile1 = File(imageurl)
                Log.e("**********", "!!FILE 1 SIZE==>" + mFile1.length() / 1024)
                try {
                    mFile = Compressor(this).compressToFile(mFile1)

                    Log.e("**********", "!!FILE SIZE==>" + mFile?.length()!! / 1024)
                } catch (e: IOException) {
                    e.printStackTrace()
                    mFile = File(imageurl)
                }




                image_param_string = "" + imageurl


            } catch (e: Exception) {
                e.printStackTrace()

            }


        }

    }

}
