package learncode.example.com.dreamtrip.DataModelClass

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by vadivel on 2/7/18.
 */

class DestinationItem(var title:String,var description:String,var image:String,var latitude:String,
                      var longitude:String,var contact:String,var timing:String,var sitelink:String) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(description)
        parcel.writeString(image)
        parcel.writeString(latitude)
        parcel.writeString(longitude)
        parcel.writeString(contact)
        parcel.writeString(timing)
        parcel.writeString(sitelink)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DestinationItem> {
        override fun createFromParcel(parcel: Parcel): DestinationItem {
            return DestinationItem(parcel)
        }

        override fun newArray(size: Int): Array<DestinationItem?> {
            return arrayOfNulls(size)
        }
    }
}



