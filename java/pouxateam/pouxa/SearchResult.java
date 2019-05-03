package pouxateam.pouxa;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.yyydjk.library.DropDownMenu;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchResult extends AppCompatActivity {

    @BindView(R.id.dropDownMenu)
    DropDownMenu mDropDownMenu;
    private String headers[] = {"價格", "上衣", "下身", "外套"};
    private List<View> popupViews = new ArrayList<>();

    private FilterDropDownAdapter priceAdapter;
    private FilterDropDownAdapter topAdapter;
    private FilterDropDownAdapter bottomAdapter;
    private FilterDropDownAdapter jacketAdapter;

    ArrayList<String> productname = new ArrayList<>();
    ArrayList<String> productprice = new ArrayList<>();

    private String prices[] = {"不限", "$500以下", "$501-$1000", "$1001-1500", "$1500以上"};
    private String tops[] = {"不限", "T恤", "襯衫", "背心", "洋裝", "毛衣"};
    private String bottoms[] = {"不限", "短褲", "長褲", "裙子"};
    private String jackets[] = {"不限", "棉質", "針織", "罩衫"};
    private String keypricelow;
    private String keypricehigh;
    private String keytype1;
    private String keytype2;

    ListView contentView;
    ResultViewAdapter adapter;
    LinearLayout layout;

    TextView element1, element2, element3;

    String url_search;
    String searchtype;
    String keyword;

    private RequestQueue requestQueue;
    Gson gson = new Gson();
    private DropDownMenu rlBackgroundPanel = null;
    private BitmapDrawable bmpDrawImg = null;

    ArrayList<SearchResultIdioms> filterData = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FontsOverride.setDefaultFont(this, "MONOSPACE", "fonts/typeface.otf");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        fnSetBackground(); // 呼叫設置背景函數
        layout = (LinearLayout) findViewById(R.id.dropDownMenu);
        ButterKnife.bind(this);

        //取得前一個Activity傳過來的Bundle物件，當作搜尋資料庫的keywords
        Bundle bundle = getIntent().getExtras();
        searchtype = bundle.get("searchtype").toString();
        keyword = bundle.get("keyword").toString();
        //中文關鍵字需要先做utf編碼
        try {
            keyword = URLEncoder.encode(keyword, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        url_search = "http://163.13.201.111/searchproduct.php?searchtype=" + searchtype + "&keyword=" + keyword;
        System.out.println("outside url_search：" + url_search);
        requestQueue = Volley.newRequestQueue(this);
        new LoadIdioms().execute();

        // Pass results to ResultViewAdapter Class
        contentView = new ListView(this);
        initView();
    }

    //設置背景函數
    public void fnSetBackground() {
        // 先取得RelativeLayout
        rlBackgroundPanel = (DropDownMenu) findViewById(R.id.dropDownMenu);
        // 取得該張圖片，並放置在變數bmpDrawImg
        bmpDrawImg = new BitmapDrawable(getResources().openRawResource(+R.drawable.bg_all));
        // 最後就是設定圖片
        rlBackgroundPanel.setBackgroundDrawable(bmpDrawImg);
    }

    class LoadIdioms extends AsyncTask<String, String, String> {
        /**
         * getting Idioms from url
         */
        ArrayList<SearchResultIdioms> allData = new ArrayList<>();

        protected String doInBackground(String... args) {

            StringRequest stringRequest = new StringRequest(Request.Method.GET, url_search, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        System.out.println("inside url_search：" + url_search);

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
                            allData.add(ii);
                        }

                        if (searchtype.equals("picturecode")) {

                            ImageSearch imageSearch = new ImageSearch(getBaseContext());
                            imageSearch.getImageSearch(new ImageSearch.VolleyCallback() {
                                public void onSuccess(ArrayList<SearchResultIdioms> imageResult) {
                                    for (SearchResultIdioms i : imageResult) {
                                        allData.add(i);
                                    }
                                    filterData.addAll(allData);
                                    adapter = new ResultViewAdapter(getBaseContext(), filterData);
                                    // Binds the Adapter to the ListView
                                    contentView.setAdapter(adapter);
                                }
                            });
                        }else{
                            filterData.addAll(allData);
                            adapter = new ResultViewAdapter(getBaseContext(), filterData);
                            // Binds the Adapter to the ListView
                            contentView.setAdapter(adapter);
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(SearchResult.this, error.toString(), Toast.LENGTH_LONG).show();
                }
            });

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            requestQueue.add(stringRequest);
            return "Success";
            // Building Parameters
        }

        protected void onPostExecute(String success) {


            //list監聽點擊事件，開啟瀏覽器
            /*contentView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    System.out.println("OPEN："+filterData.get(position).getProducturl());
                    Uri uri = Uri.parse(filterData.get(position).getProducturl());
                    Intent browser = new Intent(Intent.ACTION_VIEW, uri);
                    browser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(browser);
                }
            });*/
        }


    }

    protected void initView() {
        //init price menu
        final ListView priceView = new ListView(this);
        priceAdapter = new FilterDropDownAdapter(this, Arrays.asList(prices));
        priceView.setDividerHeight(0);
        priceView.setAdapter(priceAdapter);

        //init top menu
        final ListView topView = new ListView(this);
        topView.setDividerHeight(0);
        topAdapter = new FilterDropDownAdapter(this, Arrays.asList(tops));
        topView.setAdapter(topAdapter);

        //init bottom menu
        final ListView bottomView = new ListView(this);
        bottomView.setDividerHeight(0);
        bottomAdapter = new FilterDropDownAdapter(this, Arrays.asList(bottoms));
        bottomView.setAdapter(bottomAdapter);

        //init jacket
        final ListView jacketView = new ListView(this);
        jacketView.setDividerHeight(0);
        jacketAdapter = new FilterDropDownAdapter(this, Arrays.asList(jackets));
        jacketView.setAdapter(jacketAdapter);

        //init popupViews
        popupViews.add(priceView);
        popupViews.add(topView);
        popupViews.add(bottomView);
        popupViews.add(jacketView);

        //add item click event
        priceView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                priceAdapter.setCheckItem(position);
                mDropDownMenu.setTabText(position == 0 ? headers[0] : prices[position]);

                try {
                    switch (position) {

                        case 0:
                            keypricelow = "";
                            keypricehigh = "";
                            break;
                        case 1:
                            keypricelow = "0";
                            keypricehigh = "500";
                            break;
                        case 2:
                            keypricelow = "501";
                            keypricehigh = "1000";
                            break;
                        case 3:
                            keypricelow = "1001";
                            keypricehigh = "1500";
                            break;
                        case 4:
                            keypricelow = "1501";
                            keypricehigh = "5000";
                            break;
                    }
                    adapter.priceFilter(keypricelow, keypricehigh);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mDropDownMenu.closeMenu();
            }
        });

        topView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                topAdapter.setCheckItem(position);
                mDropDownMenu.setTabText(position == 0 ? headers[1] : tops[position]);
                try {
                    switch (position) {

                        case 0:
                            keytype1 = "";
                            keytype2 = "";
                            adapter.typeFilter(keytype1, keytype2, "OR");
                            break;
                        case 1:
                            keytype1 = "T恤";
                            keytype2 = "上衣";
                            adapter.typeFilter(keytype1, keytype2, "OR");
                            break;
                        case 2:
                            keytype1 = "襯衫";
                            keytype2 = "";
                            adapter.typeFilter(keytype1, keytype2, "AND");
                            break;
                        case 3:
                            keytype1 = "背心";
                            keytype2 = "無袖";
                            adapter.typeFilter(keytype1, keytype2, "OR");
                            break;
                        case 4:
                            keytype1 = "洋裝";
                            keytype2 = "";
                            adapter.typeFilter(keytype1, keytype2, "AND");
                            break;
                        case 5:
                            keytype1 = "毛衣";
                            keytype2 = "";
                            adapter.typeFilter(keytype1, keytype2, "AND");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mDropDownMenu.closeMenu();
            }
        });

        bottomView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                bottomAdapter.setCheckItem(position);
                mDropDownMenu.setTabText(position == 0 ? headers[2] : bottoms[position]);
                try {
                    switch (position) {

                        case 0:
                            keytype1 = "";
                            keytype2 = "";
                            break;
                        case 1:
                            keytype1 = "短褲";
                            keytype2 = "";
                            break;
                        case 2:
                            keytype1 = "長褲";
                            keytype2 = "";
                            break;
                        case 3:
                            keytype1 = "裙";
                            keytype2 = "";
                            break;
                    }

                    adapter.typeFilter(keytype1, keytype2, "AND");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mDropDownMenu.closeMenu();
            }
        });

        jacketView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                jacketAdapter.setCheckItem(position);
                mDropDownMenu.setTabText(position == 0 ? headers[3] : jackets[position]);

                try {
                    switch (position) {

                        case 0:
                            keytype1 = "";
                            keytype2 = "";
                            adapter.typeFilter(keytype1, keytype2, "AND");
                            break;
                        case 1:
                            keytype1 = "棉質";
                            keytype2 = "外套";
                            adapter.typeFilter(keytype1, keytype2, "AND");
                            break;
                        case 2:
                            keytype1 = "針織";
                            keytype2 = "外套";
                            adapter.typeFilter(keytype1, keytype2, "AND");
                            break;
                        case 3:
                            keytype1 = "罩衫";
                            keytype2 = "薄";
                            adapter.typeFilter(keytype1, keytype2, "OR");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mDropDownMenu.closeMenu();
            }
        });

        //init context view
        contentView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        //init dropdownview
        mDropDownMenu.setDropDownMenu(Arrays.asList(headers), popupViews, contentView);
    }


    @Override
    public void onBackPressed() {
        //退出activity前关闭菜单
        if (mDropDownMenu.isShowing()) {
            mDropDownMenu.closeMenu();
        } else {
            super.onBackPressed();
        }
    }

    //釋放空間
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 每個Drawable被加到VIEW上面都會產生一個callback，所以在recycle圖片之前，必須先把callback設成null
        // 設成null以後，背景圖片自然就會不見，就會變成黑的背景。bmpDrawImg的狀態就會是沒有被使用中。
        rlBackgroundPanel.getBackground().setCallback(null);
        // 先判斷bmpDrawImg 是否為null，如果不是null，且bmpDrawImg 還沒有被recycle的話就進行recycle
        if (null != bmpDrawImg && !bmpDrawImg.getBitmap().isRecycled()) {
            bmpDrawImg.getBitmap().recycle();
        }
        System.gc();
    }
}


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


}