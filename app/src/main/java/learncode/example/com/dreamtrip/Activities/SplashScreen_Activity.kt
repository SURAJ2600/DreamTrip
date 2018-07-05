package learncode.example.com.dreamtrip.Activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import learncode.example.com.dreamtrip.R
import learncode.example.com.dreamtrip.Utility.SessionManager
import learncode.example.com.dreamtrip.Utility.Util


class SplashScreen_Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContentView(R.layout.activity_main)


        getHadler()

    }



    private fun getHadler() {

        Handler().postDelayed({

            if (SessionManager.getSession(Util.session_email, this@SplashScreen_Activity).length == 0) {
                val mainIntent = Intent(this, LoginActivity::class.java)
                startActivity(mainIntent)
                finish()
            } else {
                val mainIntent = Intent(this, HomeScreen_Activity::class.java)
                startActivity(mainIntent)
                finish()
            }

        }, 3000)


    }
}