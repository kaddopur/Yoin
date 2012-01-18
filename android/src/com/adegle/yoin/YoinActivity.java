package com.adegle.yoin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class YoinActivity extends Activity {
	private String jsonString;

	private ListView listView;
	private ImageView img01;
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
		img01 = (ImageView) findViewById(R.id.youtube_img);
		SimpleAdapter adapter = new SimpleAdapter(this, getData(),
				R.layout.list_item, new String[] { "youtube_img", "title",
						"uploader", "view" }, new int[] { R.id.youtube_img,
						R.id.title, R.id.uploader, R.id.view });
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> a, View v, int position,
					long id) {
				touch();
			}
		});
		jsonString = getJSONFromURL("http://yoinadegle.appspot.com/get_videos?order=view");
		Log.e("123", jsonString);
	}

	private ArrayList<HashMap<String, String>> getData() {
		ArrayList<HashMap<String, String>> arrayList = new ArrayList<HashMap<String, String>>();
		for (int i = 0; i <= 10; i++) 
		{
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("youtube_img", "");// 標題
			map.put("title", "標題：");// 標題
			map.put("uploader", "上傳者：");// 上傳者
			map.put("view", "觀看人氣：");// 觀看人數
			arrayList.add(map);
//			try {
////				URL url = new URL("http://img.youtube.com/vi/B0CdELwhab4/2.jpg");
////				URLConnection conn = url.openConnection();
////
////				HttpURLConnection httpConn = (HttpURLConnection) conn;
////				httpConn.setRequestMethod("GET");
////				httpConn.connect();
////
////				if (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
////					InputStream inputStream = httpConn.getInputStream();
////
////					Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
////					inputStream.close();
////					
////					img01.setImageBitmap(bitmap);
////				}
//
//				
//			} catch (Exception e) {
//				// TODO: handle exception
//			}

		}

		// try
		// {
		// StringBuffer sb= new StringBuffer();
		// String url = "http://yoinadegle.appspot.com/get_videos?order=view";
		// String body = getContent(url);
		// JSONArray array = new JSONArray(body);
		// for(int i = 0;i<=array.length();i++)
		// {
		// JSONObject obj = array.getJSONObject(i);
		// HashMap<String, Object> map = new HashMap<String, Object>();
		// map.put("title", "標題" + sb.append(obj.getString("good")));// 標題
		// map.put("uploader", "上傳者" + sb.append(obj.getString("good")));// 上傳者
		// map.put("view", "觀看人數" + sb.append(obj.getString("good")));// 觀看人數
		// arrayList.add(map);
		// }
		//
		// } catch (Exception e) {
		// // TODO: handle exception
		// }
		return arrayList;
	}

	private void touch() {

		Uri uri = Uri.parse("http://www.youtube.com/v/dBiBsBY98uE");
		Intent it = new Intent(Intent.ACTION_VIEW, uri);
		startActivity(it);
	}

	private String getContent(String url) throws Exception {
		StringBuilder sb = new StringBuilder();
		HttpClient client = new DefaultHttpClient();
		HttpParams httpParams = client.getParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, 3000);
		HttpConnectionParams.setSoTimeout(httpParams, 5000);
		HttpResponse response = client.execute(new HttpGet(url));
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					entity.getContent()));
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			reader.close();
		}
		return sb.toString();
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