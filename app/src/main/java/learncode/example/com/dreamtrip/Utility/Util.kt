package learncode.example.com.dreamtrip.Utility

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.text.TextUtils
import android.util.Patterns
import android.view.LayoutInflater
import learncode.example.com.dreamtrip.R


/**
 * Created by vadivel on 2/7/18.
 */

class Util private constructor() {


    companion object {

        var session_email: String = "email"
        var session_user: String = "user"
        var session_user_id: String = "user_id"
        var session_user_image: String = "user_image"



        fun ShowProgressView(mCtx: Context): Dialog {

            val factory = LayoutInflater.from(mCtx)
            val DialogView = factory.inflate(R.layout.progressview_layout, null)
            val main_dialog = Dialog(mCtx)
            main_dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            main_dialog.setCanceledOnTouchOutside(true)
            main_dialog.setCancelable(true)
            main_dialog.setContentView(DialogView)

            return main_dialog
        }






        fun isValidEmail(target: CharSequence): Boolean {
            return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()


        }

        @SuppressLint("MissingPermission")
        fun isConnected(context: Context): Boolean {
            val cm = context
                    .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = cm.activeNetworkInfo

            return activeNetwork != null && activeNetwork.isConnectedOrConnecting
        }
    }
}
