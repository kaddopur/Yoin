package com.adegle.yoin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class YoinActivity extends Activity {
	private String jsonString;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		jsonString = getJSONFromURL("http://yoinadegle.appspot.com/get_videos?order=view");
		Log.e("123", jsonString);

	}

	private String getJSONFromURL(String string) {
		String responseString = "";
		
		try {

			URL url = new URL("http://yoinadegle.appspot.com/get_videos?order=view");
			BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
			responseString = reader.readLine();
		} catch (MalformedURLException e) {
			e.printStackTrace();
			Toast.makeText(this, "MalformedURLException", Toast.LENGTH_SHORT).show();
		} catch (IOException e) {
			e.printStackTrace();
			Toast.makeText(this, "IOException", Toast.LENGTH_SHORT).show();
		}

		return responseString;
	}

}