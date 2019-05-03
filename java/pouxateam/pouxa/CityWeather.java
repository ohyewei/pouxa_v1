package pouxateam.pouxa;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by Vivian on 2016/9/2.
 */
public class CityWeather extends AppCompatActivity {
    private Context context;
    private CityWeatherResultIdioms cityWeatherResultIdioms;
    SharedPreferences prefs;
    private RequestQueue requestQueue;
    Gson gson = new Gson();


    private String strCity, strDate, url_search, morning, night;

    public CityWeather(Context context) {
        this.context = context;
    }


    public void getCityWeatherData(final VolleyCallback callback) {
        final RequestQueue requestQueue = Volley.newRequestQueue(context);
        final Gson gson = new Gson();
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        strCity = prefs.getString("city", "臺北市");
        SimpleDateFormat sDateFormat = new SimpleDateFormat("MM/dd");
        strDate = sDateFormat.format(new java.util.Date());
        try {
            strCity = URLEncoder.encode(strCity, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        url_search = "http://163.13.201.111/searchcityweather.php?keycity=" + strCity +"&keydate="+strDate;
        final ArrayList<CityWeatherResultIdioms> allData = new ArrayList<>();

        StringRequest strReq = new StringRequest(url_search, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    System.out.println("CityWeather url_search："+url_search);
                    System.out.println("CityWeather Respone：" + response);
                    JsonReader reader = new JsonReader(new StringReader(response));
                    //解開第一層Object
                    reader.beginObject();
                    //取得標題
                    String title = reader.nextName();   //idioms
                    //解開第二層陣列
                    reader.beginArray();
                    //輪詢
                    while (reader.hasNext()) {
                        //取得每列的欄位
                        CityWeatherResultIdioms ii = gson.fromJson(reader, CityWeatherResultIdioms.class);
                        //加進陣列清單裡
                        allData.add(ii);
                    }

                    System.out.println("All Data morning："+allData.get(0).getMorning());
                    callback.onSuccess(allData);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error instanceof NoConnectionError) {
                    Toast.makeText(context,"請檢查網路連線",Toast.LENGTH_SHORT).show();
                }
                System.out.println("vollet error：" + error.toString());
            }
        });
        requestQueue.add(strReq);

    }

    public interface VolleyCallback {
        void onSuccess(ArrayList<CityWeatherResultIdioms> allData);
    }
}

class CityWeatherResultIdioms {

    private String morning;
    private String night;

    public CityWeatherResultIdioms() {
    }

    public String getMorning() {
        return morning;
    }

    public String getNight() {
        return night;
    }


}

