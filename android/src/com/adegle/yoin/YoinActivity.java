package com.adegle.yoin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.PushService;

public class YoinActivity extends Activity {
	private String jsonString;
	ArrayList<HashMap<String, Object>> arrayList = new ArrayList<HashMap<String, Object>>();

	private ListView listView;
	private Bitmap bitmap;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Parse.initialize(this, "0wvSw382QmOSDfFfLY9cjEwzjdCD0BkdWn8bus6j",
				"dlOrxUpPqPVALHFkwq2ys5QdROp63SDAQim7BISU");
		PushService.subscribe(this, "", YoinActivity.class, R.drawable.noticon);

		findViews();
		setListeners();

	}

	private void setListeners() {
		SimpleAdapter adapter = new SimpleAdapter(this, getData(),
				R.layout.list_item, new String[] { "youtube_img", "title",
						"uploader", "view" }, new int[] { R.id.youtube_img,
						R.id.title, R.id.uploader, R.id.view });
		adapter.setViewBinder(new ViewBinder() {
			public boolean setViewValue(View view, Object data,
					String textRepresentation) 
			{
				if (view instanceof ImageView && data instanceof Bitmap) {
					ImageView iv = (ImageView) view;
					iv.setImageBitmap((Bitmap) data);
					return true;
				} else
					return false;
			}
		});
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> a, View v, int position,
					long id) {
				Uri uri = Uri.parse("http://www.youtube.com/v/"
						+ arrayList.get(position).get("vid"));
				Intent it = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(it);
			}
		});
	}

	private void findViews() {
		listView = (ListView) findViewById(R.id.listView1);
	}

	private ArrayList<HashMap<String, Object>> getData() {
		jsonString = getJSONFromURL("http://yoinadegle.appspot.com/get_videos?order=view");
		try {
			JSONArray array = new JSONArray(jsonString);
			for (int i = 0; i < array.length(); i++) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				JSONObject obj = array.getJSONObject(i);
				try {
					URL url = new URL("http://img.youtube.com/vi/"+obj.getString("vid") +"/2.jpg");
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					InputStream is = conn.getInputStream();
					bitmap = BitmapFactory.decodeStream(is);
					map.put("youtube_img", bitmap);// 標題
					map.put("title", obj.getString("title"));// 標題
					map.put("uploader", obj.getString("uploader"));// 上傳者
					map.put("view", obj.getString("view"));// 觀看人數
					map.put("vid", obj.getString("vid"));
					arrayList.add(map);
					
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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