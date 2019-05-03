package pouxateam.pouxa;

import android.content.Context;
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
import java.util.ArrayList;

/**
 * Created by Vivian on 2016/9/2.
 */
public class ImageSearch {
    private Context context;


    private String url_search_pic;

    public ImageSearch(Context context) {
        this.context = context;
    }


    public void getImageSearch(final VolleyCallback callback) {
        final RequestQueue requestQueue = Volley.newRequestQueue(context);
        final Gson gson = new Gson();

        String keyword = "蕾絲";
        //中文關鍵字需要先做utf編碼
        try {
            keyword = URLEncoder.encode(keyword, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        url_search_pic = "http://163.13.201.111/searchproduct.php?searchtype=productname&keyword="+keyword;
        final ArrayList<SearchResultIdioms> imageResult = new ArrayList<>();

        StringRequest strReq = new StringRequest(url_search_pic, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    System.out.println("url_search_pic：" + url_search_pic);
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
                        SearchResultIdioms ii = gson.fromJson(reader, SearchResultIdioms.class);
                        //加進陣列清單裡
                        imageResult.add(ii);
                    }

                    callback.onSuccess(imageResult);
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
        void onSuccess(ArrayList<SearchResultIdioms> imageResult);
    }
}
/*
class SearchResultIdioms {
    private String id;
    private String storenameall;
    private String producturl;
    private String productname;
    private String productprice;
    private String storescore;
    private String productpictureurl;
    private String productchoose;

    public SearchResultIdioms() {
    }

    public String getId() {
        return id;
    }

    public String getStorename() {
        return storenameall;
    }

    public String getProducttitle() {
        return productname;
    }

    public String getProducturl() {
        return producturl;
    }

    public String getProductprice() {
        return productprice;
    }

    public String getStorescore() {
        return storescore;
    }

    public String getProductpictureurl() {
        return productpictureurl;
    }

    public String getProductchoose() {
        return productchoose;
    }


}*/