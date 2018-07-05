package learncode.example.com.dreamtrip.Utility;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SessionManager {

	public static void saveSession(String key, String value, Context context){
		Editor editor = context.getSharedPreferences("KEY", Activity.MODE_PRIVATE).edit();
		editor.putString(key, value);
		editor.commit();
	}
	
	
	public static String getSession(String key, Context context){
		SharedPreferences pref = context.getSharedPreferences("KEY", Activity.MODE_PRIVATE);
		return pref.getString(key, "");
	}
	
	public static void ClearSession(Context context){
		Editor editor = context.getSharedPreferences("KEY", Activity.MODE_PRIVATE).edit();
		editor.clear();
		editor.commit();
	}

	public static void ClearSessionKey(String key, Context context){
		Editor editor = context.getSharedPreferences(key, Activity.MODE_PRIVATE).edit();
		editor.clear();
		editor.commit();
	}



}
