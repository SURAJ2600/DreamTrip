package learncode.example.com.dreamtrip.Adapter

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.admin2base.myapplication.utils.AppUtils
import com.squareup.picasso.Picasso
import learncode.example.com.dreamtrip.Activities.DestinationdetailScreen_Activity
import learncode.example.com.dreamtrip.DataModelClass.DestinationItem
import learncode.example.com.dreamtrip.R
import learncode.example.com.dreamtrip.Utility.GPSTracker
import java.util.*






/**
 * Created by vadivel on 2/7/18.
 */

class DestinationAdapter(internal var activity: Activity, internal var Destinationlist: ArrayList<DestinationItem>) : RecyclerView.Adapter<DestinationAdapter.ViewHolder>() {


    var mPos: Int = 0;
    var mDistance: String = "";

    private val REQUEST_PHONE_CALL = 23


    override fun getItemCount(): Int {
        return Destinationlist.size;
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {


        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.destination_listitem, parent, false)

        return ViewHolder(itemView)


    }



    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        var destination_model = Destinationlist.get(holder.adapterPosition)


        holder.txt_place?.setText("" + destination_model.title)
        holder.txt_description?.setText("" + destination_model.description)
        holder.txt_phone?.setText("Contact :" + destination_model.contact)
        holder.txt_location_disctane?.setText("Distance :" + destination_model.latitude)

        var gpsTracker = GPSTracker(activity);

        if (gpsTracker.getIsGPSTrackingEnabled()) {
           try {
               var destination_lat: Double = destination_model.latitude.toDouble();
               var destination_longi: Double = destination_model.longitude.toDouble();

               AppUtils.instance.debugLog("LOCATION","DDD"+gpsTracker.latitude+":"+gpsTracker.longitude)

               AppUtils.instance.debugLog("LOCATION","DDD"+destination_lat+":"+destination_longi)


               var destination_location =  Location("");
               destination_location.setLatitude(destination_lat);
               destination_location.setLongitude(destination_longi);

               var source_location =  Location("");

               source_location.setLatitude(gpsTracker.latitude);
               source_location.setLongitude(gpsTracker.longitude);

               var distanceInMeters = source_location.distanceTo(destination_location)/1000
                AppUtils.instance.debugLog("mVal","distanceInMeters"+distanceInMeters)
               holder.txt_location_disctane?.setText("" + distanceInMeters+" KM")
               mDistance=""+distanceInMeters+"KM"

           }catch (e:Exception){
               e.printStackTrace()
               AppUtils.instance.debugLog("LOCATION","DDD"+e)
           }

        } else {
            gpsTracker.showSettingsAlert();
        }
        try {
            Picasso.get().load(destination_model.image)
                    .error( R.mipmap.ic_splashimage )
                    .placeholder( R.drawable.progressanimation ).into(holder.img_destination);

        } catch (e: Exception) {
            e.printStackTrace()
        }

        holder.txt_phone.setOnClickListener(View.OnClickListener {

            mPos = holder.adapterPosition;
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(activity, "Please enable Cell phone permission", Toast.LENGTH_LONG).show()
            } else {
                mCall(holder.adapterPosition)
            }


        })


    }



    fun mCall(adapterPosition: Int) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),REQUEST_PHONE_CALL);
        }
        else
        {
            val callIntent = Intent(Intent.ACTION_CALL)
            callIntent.data = Uri.parse("tel:" + Destinationlist.get(adapterPosition).contact)
            activity.startActivity(callIntent)
        }


    }



    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var txt_place: TextView
        var txt_description: TextView
        var txt_location_disctane: TextView
        var txt_phone: TextView

        var img_destination: ImageView

        var destinationitem_mainlayout:LinearLayout


        init {

            txt_place = itemView.findViewById<TextView>(R.id.txt_place) as TextView
            txt_description = itemView.findViewById<TextView>(R.id.txt_dexcription) as TextView
            txt_location_disctane = itemView.findViewById<TextView>(R.id.txt_distance) as TextView
            txt_phone = itemView.findViewById<TextView>(R.id.txt_phonenumber) as TextView
            img_destination = itemView.findViewById<ImageView>(R.id.img_destination) as ImageView

            destinationitem_mainlayout=itemView.findViewById<LinearLayout>(R.id.destination_mainlayout) as LinearLayout

            img_destination.setOnClickListener(View.OnClickListener {
                mCallDetail(adapterPosition);

            })
            txt_description.setOnClickListener(View.OnClickListener {
                mCallDetail(adapterPosition);

            })


            txt_place.setOnClickListener(View.OnClickListener {
                mCallDetail(adapterPosition);

            })

            val metrics = activity.getResources().getDisplayMetrics()


// Gets the layout params that will allow you to resize the layout
            val params = destinationitem_mainlayout.getLayoutParams()
// Changes the height and width to the specified *pixels*
            params.height = metrics.heightPixels/4
            destinationitem_mainlayout.setLayoutParams(params)

        }


    }

    fun mCallDetail(adapterPosition: Int) {
        val intent = Intent(activity, DestinationdetailScreen_Activity::class.java)
        intent.putExtra("distance",""+mDistance)
        intent.putExtra("destination_object", Destinationlist.get(adapterPosition))
        activity.startActivity(intent)
    }


    @Override
    fun onRequestPermissionsResult(requestCode: Int,
                                   permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_PHONE_CALL -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                     mCall(mPos)

                } else {

                }
                return
            }
        }
    }

}


