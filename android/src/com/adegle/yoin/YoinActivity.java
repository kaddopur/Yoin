package com.adegle.yoin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class YoinActivity extends Activity {
	private String jsonString;

	private ListView listView;
	String page;
	String str = "";
	String url = "http://yoinadegle.appspot.com/get_videos?order=view";

	// private static final String IMG_URL =
	// "http://img.youtube.com/vi/fcmc807j304/2.jpg";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		listView = (ListView) findViewById(R.id.listView1);
		SimpleAdapter adapter = new SimpleAdapter(this, getData(),
				R.layout.list_item, new String[] { "youtube_img", "title",
						"uploader", "view" }, new int[] { R.id.youtube_img,
						R.id.title, R.id.uploader, R.id.view });
		listView.setAdapter(adapter);

		jsonString = getJSONFromURL("http://yoinadegle.appspot.com/get_videos?order=view");
		Log.e("123", jsonString);
	}

	private ArrayList<HashMap<String, Object>> getData() {
		ArrayList<HashMap<String, Object>> arrayList = new ArrayList<HashMap<String, Object>>();
		for (int i = 1; i <= 10; i++) {
			try {

				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("youtube_img", "");// 影片縮圖
				map.put("title", "標題" + i);// 標題
				map.put("uploader", "上傳者" + i);// 上傳者
				map.put("view", "觀看人數" + i);// 觀看人數
				arrayList.add(map);

			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		return arrayList;
	}

	private String getJSONFromURL(String string) {
		String responseString = "";
		try {
			URL url = new URL(
					"http://yoinadegle.appspot.com/get_videos?order=view");
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					url.openStream()));
			responseString = reader.readLine();

		} catch (MalformedURLException e) {
			e.printStackTrace();
			Toast.makeText(this, "MalformedURLException", Toast.LENGTH_SHORT)
					.show();
		} catch (IOException e) {
			e.printStackTrace();
			Toast.makeText(this, "IOException", Toast.LENGTH_SHORT).show();
		}

		return responseString;
	}

}