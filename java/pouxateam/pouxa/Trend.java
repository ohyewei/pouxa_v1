package pouxateam.pouxa;

import android.app.Application;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vivian on 2016/8/9.
 */
public class Trend extends AppCompatActivity {


    private Context context;
    private String[] trendArray;

    public Trend(Context context) {
        this.context = context;
    }


    public void getTrendData(final VolleyCallback callback) {
        final RequestQueue requestQueue = Volley.newRequestQueue(context);
        final Gson gson = new Gson();

        final ArrayList<TrendResultIdioms> allData = new ArrayList<>();
        StringRequest strReq = new StringRequest("http://163.13.201.111/getTrend.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
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
                        TrendResultIdioms ii = gson.fromJson(reader, TrendResultIdioms.class);
                        //加進陣列清單裡
                        allData.add(ii);
                    }

                    trendArray = new String[10];
                    for (int i = 0; i < allData.size(); i++) {
                        trendArray[i] = allData.get(i).getTrend();
                    }
                    callback.onSuccess(trendArray);
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
        void onSuccess(String[] trendArray);
    }

}

class TrendResultIdioms {

    private String trend;


    public TrendResultIdioms() {
    }

    public String getTrend() {

        return trend;
    }


}
