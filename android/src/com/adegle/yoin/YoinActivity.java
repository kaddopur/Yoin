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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

import com.parse.Parse;
import com.parse.PushService;

public class YoinActivity extends Activity {
	private String jsonString;
	ArrayList<HashMap<String, String>> arrayList = new ArrayList<HashMap<String, String>>();

	private ListView listView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Parse.initialize(this, "0wvSw382QmOSDfFfLY9cjEwzjdCD0BkdWn8bus6j", "dlOrxUpPqPVALHFkwq2ys5QdROp63SDAQim7BISU");
		PushService.subscribe(this, "", YoinActivity.class, R.drawable.noticon);
		
		findViews();
		setListeners();

	}

	private void setListeners() {
		SimpleAdapter adapter = new SimpleAdapter(this, getData(), R.layout.list_item, new String[] { "youtube_img", "title", "uploader", "view" }, new int[] { R.id.youtube_img, R.id.title, R.id.uploader, R.id.view });
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> a, View v, int position, long id) {
				Uri uri = Uri.parse("http://www.youtube.com/v/" + arrayList.get(position).get("vid"));
				Intent it = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(it);
			}
		});
	}

	private void findViews() {
		listView = (ListView) findViewById(R.id.listView1);
	}

	private ArrayList<HashMap<String, String>> getData() {
		jsonString = getJSONFromURL("http://yoinadegle.appspot.com/get_videos?order=view");
		try {
			JSONArray array = new JSONArray(jsonString);
			for(int i=0; i<array.length(); i++){
				HashMap<String, String> map = new HashMap<String, String>();
				JSONObject obj = array.getJSONObject(i);
				map.put("youtube_img", "xxx");// 標題
				map.put("title", obj.getString("title"));// 標題
				map.put("uploader", obj.getString("uploader"));// 上傳者
				map.put("view", obj.getString("view"));// 觀看人數
				map.put("vid", obj.getString("vid"));
				arrayList.add(map);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return arrayList;
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
			BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
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