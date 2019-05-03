package pouxateam.pouxa;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
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
public class MiuStarEvent {

    private Context context;


    private String url_search;

    public MiuStarEvent(Context context) {
        this.context = context;
    }


    public void getMiuStarEvent(final VolleyCallback callback) {
        final RequestQueue requestQueue = Volley.newRequestQueue(context);
        final Gson gson = new Gson();

        url_search = "http://163.13.201.111/searchactivity.php?keystorename=Miu-star";
        final ArrayList<EventResultIdioms> miuData = new ArrayList<>();

        StringRequest strReq = new StringRequest(url_search, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    System.out.println("Miu url_search："+url_search);
                    System.out.println("Miu Respone：" + response);
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
                        EventResultIdioms ii = gson.fromJson(reader, EventResultIdioms.class);
                        //加進陣列清單裡
                        miuData.add(ii);
                    }

                    System.out.println("Miu Data："+miuData.get(0).getEventname());
                    callback.onSuccess(miuData);
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
        void onSuccess(ArrayList<EventResultIdioms> miuData);
    }
}
