package learncode.example.com.dreamtrip.Activities

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.squareup.picasso.Picasso
import learncode.example.com.dreamtrip.DataModelClass.DestinationItem
import learncode.example.com.dreamtrip.R
import learncode.example.com.dreamtrip.Utility.SessionManager
import learncode.example.com.dreamtrip.Utility.Util


class DestinationdetailScreen_Activity : AppCompatActivity() {

    var mDestination_img:ImageView?=null

    var mDestination_number:TextView?=null
    var mDestination_distance:TextView?=null
    var mDestination_timing:TextView?=null
    var mDestination_title:TextView?=null
    var mDestination_noverview:TextView?=null
    var mDestination_model:DestinationItem?=null
    var mProfile:ImageView?=null
    var mBack_btn :ImageView?=null
    var mTitle:TextView?=null
    private var mContext: Context? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContentView(R.layout.activity_destinationdetail_screen_)

        mContext = this@DestinationdetailScreen_Activity
        Inint();

        mTitle?.text = getString(R.string.detail_page)
        try {
            Picasso.get().load(SessionManager.getSession(Util.session_user_image,mContext))
                    .placeholder(R.mipmap.ic_profile).into(mProfile);

        }
        catch (e:Exception)
        {
            e.printStackTrace()
        }


        var intent=intent;

        if(intent.hasExtra("destination_object"))

        {

            mDestination_model=intent.getParcelableExtra("destination_object")
            try {
                Picasso.get().load(mDestination_model?.image).into(mDestination_img);
            }
            catch (e:Exception)
            {
                e.printStackTrace()
            }
            mTitle?.text = ""+mDestination_model?.title
            mDestination_number?.setText("Contact :"+mDestination_model?.contact)
            mDestination_distance?.setText("Distance :"+intent.getStringExtra("distance"))
            mDestination_timing?.setText(""+mDestination_model?.timing)
            mDestination_title?.setText(""+mDestination_model?.title)
            mDestination_noverview?.setText(""+mDestination_model?.description)


            mDestination_img?.setOnClickListener(View.OnClickListener {
                mDestination_noverview?.performClick()
            })

            mDestination_title?.setOnClickListener(View.OnClickListener {
                mDestination_noverview?.performClick()
            })

            mDestination_noverview?.setOnClickListener(View.OnClickListener {

                try {
                    val myIntent = Intent(Intent.ACTION_VIEW, Uri.parse(mDestination_model?.sitelink))
                    startActivity(myIntent)
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(this, "No application can handle this request." + " Please install a webbrowser", Toast.LENGTH_LONG).show()
                    e.printStackTrace()
                }

            })

        }

        mProfile?.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        })


        mBack_btn?.setOnClickListener(View.OnClickListener {
            finish();
        })

    }

    private fun Inint() {

        mDestination_img=findViewById<ImageView>(R.id.img_place) as ImageView
        mDestination_number=findViewById<TextView>(R.id.textview_tnumber) as TextView
        mDestination_distance=findViewById<TextView>(R.id.textview_distance) as TextView
        mDestination_timing=findViewById<TextView>(R.id.textview_timing) as TextView
        mDestination_title=findViewById<TextView>(R.id.textview_original_title) as TextView
        mDestination_noverview=findViewById<TextView>(R.id.textview_overview) as TextView
        mProfile = findViewById<ImageView>(R.id.profile)
        mBack_btn = findViewById<ImageView>(R.id.back_btn)
        mTitle = findViewById<TextView>(R.id.mTitle)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }


}
