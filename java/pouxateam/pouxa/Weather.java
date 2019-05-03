package pouxateam.pouxa;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Weather extends AppCompatActivity {

    //UI
    private TextView tvtitle;
    private Spinner days, city, area;
    private Button ok;
    ArrayAdapter<String> daysAdapter;
    ArrayAdapter<String> cityAdapter;
    ArrayAdapter<String> areaAdapter;
    private ImageButton icsearch, icimage, ichome, iclike, icsetting;
    private IbtnOnClickListener ibtnOnClickListener = new IbtnOnClickListener();
    Intent intent = new Intent();
    private RelativeLayout rlBackgroundPanel = null;
    private BitmapDrawable bmpDrawImg = null;

    //以圖搜圖
    CreatePHash p;
    String imagecode;
    private final static int CAMERA = 66;
    private final static int PHOTO = 99;
    final ArrayList<String> choose = new ArrayList<>();

    //資料庫
    private static String url_search = "http://163.13.201.111/searchareaweather.php";
    //直接填入城市選單選項
    String[] cityArray = {"基隆市", "臺北市", "新北市", "桃園市", "新竹市", "新竹縣", "苗栗縣", "臺中市", "彰化縣",
            "南投縣", "雲林縣", "嘉義市", "嘉義縣", "臺南市", "高雄市", "屏東縣", "臺東縣", "花蓮縣", "宜蘭縣",
            "澎湖縣", "金門縣", "連江縣"};
    //鄉鎮選單
    String[] areaArray;
    //本周日期選單
    String[] daysArray = new String[7];
    //search key value
    public String citykey;
    public String areakey = "";
    public String dayskey = "";
    private RequestQueue requestQueue;
    Gson gson = new Gson();


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        FontsOverride.setDefaultFont(this, "MONOSPACE", "fonts/typeface.otf");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        fnSetBackground(); // 呼叫設置背景函數

        //找到要設定的元件
        city = (Spinner) findViewById(R.id.spcity);
        area = (Spinner) findViewById(R.id.sparea);
        days = (Spinner) findViewById(R.id.spdays);
        ok = (Button) findViewById(R.id.btnok);
        tvtitle = (TextView) findViewById(R.id.tvtitle);
        setTvtitle();

        //使用cityAdapter把城市指定給city選單
        cityAdapter = new ArrayAdapter<>(Weather.this, R.layout.weather_spinner_style, cityArray);
        city.setAdapter(cityAdapter);

        requestQueue = Volley.newRequestQueue(this);
        city.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {

                //讀取城市下拉選單是選擇哪個城市
                citykey = parent.getSelectedItem().toString();

                //從資料庫抓出資料 (Loading idioms in Background Thread)
                new LoadIdioms().execute();
            }

            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        //按下OK後把選單內的內容傳給WeatherResult
        ok.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    //傳送參數到下一個Actitity
                    deliverToNextActitity();
                } catch (NullPointerException e) {
                }
            }
        });

        icsearch = (ImageButton) findViewById(R.id.icsearch);
        icimage = (ImageButton) findViewById(R.id.icimage);
        choose.add("拍攝照片");
        choose.add("從相簿選取");
        choose.add("取消");
        ichome = (ImageButton) findViewById(R.id.ichome);
        iclike = (ImageButton) findViewById(R.id.iclike);
        icsetting = (ImageButton) findViewById(R.id.icsetting);
        ImageButton[] ibtnres = {icsearch, icimage, ichome, iclike, icsetting};
        for (ImageButton ib : ibtnres) {
            ib.setOnClickListener(ibtnOnClickListener);
        }

        p = new CreatePHash();
    }

    //設置背景函數
    public void fnSetBackground() {
        // 先取得RelativeLayout
        rlBackgroundPanel = (RelativeLayout) findViewById(R.id.activity_weather);
        // 取得該張圖片，並放置在變數bmpDrawImg
        bmpDrawImg = new BitmapDrawable(getResources().openRawResource(+R.drawable.bg_all));
        // 最後就是設定圖片
        rlBackgroundPanel.setBackgroundDrawable(bmpDrawImg);
    }

    //設置標題
    protected void setTvtitle() {
        //建立一個ImageSpan元件並帶入要插入的圖片
        Drawable drawable = getResources().getDrawable(R.drawable.btn_weather);
        drawable.setBounds(0, 0, 144, 144);             //設置圖片大小
        ImageSpan mImageSpan = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);

        //建立一個SpannableString元件並帶入要顯示的文字字串
        SpannableString mSpannableString = new SpannableString(" 天氣");

        //插入mImageSpan圖片，並指定在字串裡的第22個位置到23個位置進行插入 (總字串長度為23，圖片插入位置為22-23的位置)
        mSpannableString.setSpan(mImageSpan, 0, 1, 0);

        //將組合後的文字圖片放入TextView裡
        tvtitle.setText(mSpannableString);

    }

    public void deliverToNextActitity() {

        //建立一個bundle物件，並將要傳遞的參數放到bundle裡
        Bundle bundle = new Bundle();
        bundle.putString("city", city.getSelectedItem().toString());
        bundle.putString("area", area.getSelectedItem().toString());
        bundle.putString("days", days.getSelectedItem().toString());
        Intent intent = new Intent();
        //設定下一個Actitity
        intent.setClass(this, WeatherResult.class);
        intent.putExtras(bundle);
        //開啟Activity
        startActivity(intent);
    }

    /**
     * Background Async Task to Load Idioms by making HTTP Request
     */
    class LoadIdioms extends AsyncTask<String, String, String> {
        /**
         * getting Idioms from url
         **/

        protected String doInBackground(String... args) {

            final ArrayList<WeatherIdioms> allData = new ArrayList<>();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url_search, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {

                        System.out.println("Respone：" + response);
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
                            WeatherIdioms ii = gson.fromJson(reader, WeatherIdioms.class);
                            //加進陣列清單裡
                            allData.add(ii);
                        }

                        //用迴圈跑出結果
                        areaArray = new String[(allData.size() / 7)];            //陣列大小是全部資料的七分之一
                        int count = 0;
                        for (int i = 0; i < areaArray.length; i++) {
                            //把所選城市下的鄉鎮放入areaArray
                            areaArray[i] = (allData.get(count).getArea());
                            count += 7;
                        }
                        for (int i = 0; i < 7; i++) {
                            //把所選城市下的鄉鎮放入daysArray
                            daysArray[i] = allData.get(i).getDays();
                        }

                        areaAdapter = new ArrayAdapter<String>(Weather.this, R.layout.weather_spinner_style, areaArray) {

                        };

                        //載入第二個下拉選單area
                        area.setAdapter(areaAdapter);
                        //產生daysAdapter，用的是daysArray
                        daysAdapter = new ArrayAdapter<>(Weather.this, R.layout.weather_spinner_style, daysArray);
                        //載入第三個下拉選單days
                        days.setAdapter(daysAdapter);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(Weather.this, error.toString(), Toast.LENGTH_LONG).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("keycity", citykey);
                    params.put("keyarea", areakey);
                    params.put("keydays", dayskey);
                    return params;
                }
            };
            requestQueue.add(stringRequest);
            return "Success";
        }

    }

    class IbtnOnClickListener implements View.OnClickListener {

        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.icsearch:
                    intent.setClass(Weather.this, Search.class);
                    startActivity(intent);
                    break;
                case R.id.icimage:
                    new AlertDialog.Builder(Weather.this)
                            .setItems(choose.toArray(new String[choose.size()]), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int position) {
                                    if(choose.get(position).equals("拍攝照片"))
                                        //開啟相機
                                        ;
                                    else if(choose.get(position).equals("從相簿選取")){
                                        //開啟相簿相片集，須由startActivityForResult且帶入requestCode進行呼叫，原因
                                        //為點選相片後返回程式呼叫onActivityResult
                                        Intent photointent = new Intent();
                                        photointent.setType("image/*");
                                        photointent.setAction(Intent.ACTION_GET_CONTENT);
                                        startActivityForResult(photointent, PHOTO);
                                    }
                                }
                            })
                            .show();
                    break;
                case R.id.ichome:
                    intent.setClass(Weather.this, MainActivity.class);
                    startActivity(intent);
                    break;
                case R.id.iclike:
                    intent.setClass(Weather.this, Like.class);
                    startActivity(intent);
                    break;
                case R.id.icsetting:
                    intent.setClass(Weather.this, Setting.class);
                    startActivity(intent);
                    break;
            }
        }

    }

    //拍照完畢或選取圖片後呼叫此函式
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //藉由requestCode判斷是否為開啟相機或開啟相簿而呼叫的，且data不為null
        if ((requestCode == CAMERA || requestCode == PHOTO) && data != null) {
            //取得照片路徑uri
            Uri uri = data.getData();
            ContentResolver cr = this.getContentResolver();

            try {
                //讀取照片，型態為Bitmap
                Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                // 產生圖片的PHashCode
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos);
                byte[] bitmapdata = bos.toByteArray();
                InputStream is = new ByteArrayInputStream(bitmapdata);
                try {
                    p.initCoefficients();
                    imagecode = p.getHash(is);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Intent passintent = new Intent();
                Bundle passbundle = new Bundle();

                passintent.setClass(Weather.this, SearchResult.class);
                //把圖片分析碼傳給SearchResult頁面
                passbundle.putString("searchtype", "picturecode");
                passbundle.putString("keyword", imagecode);
                //將Bundle物件傳給passintent
                passintent.putExtras(passbundle);
                startActivity(passintent);
                System.out.println("這張圖片的PHash：" + imagecode);

            } catch (FileNotFoundException e) {
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
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


class WeatherIdioms {

    private String city;
    private String area;
    private String days;

    public WeatherIdioms() {
    }

    public String getCity() {
        return city;
    }

    public String getArea() {
        return area;
    }

    public String getDays() {
        return days;
    }


}

