package pouxateam.pouxa;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
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
import java.util.Random;

public class WeatherResult extends AppCompatActivity {


    //UI
    private TextView city, area, morningtep, nighttep, morning, night, recommand;
    private ImageButton icsearch, icimage, ichome, iclike, icsetting, imgrecommand;
    Intent intent = new Intent();
    private ViewGroup parent;
    private RelativeLayout rlBackgroundPanel = null;
    private BitmapDrawable bmpDrawImg = null;

    //以圖搜圖
    CreatePHash p;
    String imagecode;
    private final static int CAMERA = 66;
    private final static int PHOTO = 99;
    final ArrayList<String> choose = new ArrayList<>();
    //資料庫
    String citykey, areakey, dayskey;
    String[] result = new String[8];
    private static String url_search = "http://163.13.201.111/searchareaweather.php";
    private RequestQueue requestQueue;
    Gson gson = new Gson();
    Trend trend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FontsOverride.setDefaultFont(this, "MONOSPACE", "fonts/typeface.otf");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_result);
        fnSetBackground(); // 呼叫設置背景函數

        city = (TextView) findViewById(R.id.txcity);
        area = (TextView) findViewById(R.id.txarea);
        morningtep = (TextView) findViewById(R.id.txmorningtep);
        nighttep = (TextView) findViewById(R.id.txnighttep);
        morning = (TextView) findViewById(R.id.txmorning);
        night = (TextView) findViewById(R.id.txnight);

        requestQueue = Volley.newRequestQueue(this);
        //取得前一個Activity傳過來的Bundle物件，當作搜尋資料庫的keywords
        Bundle bundle = getIntent().getExtras();
        citykey = bundle.get("city").toString();
        areakey = bundle.get("area").toString();
        dayskey = bundle.get("days").toString();

        trend = new Trend(this);
        new LoadIdioms().execute();
        recommand = (TextView) findViewById(R.id.txrecommand);
        imgrecommand = (ImageButton) findViewById(R.id.imgrecommand);
        imgrecommand.setOnClickListener(imgOnClickListener);


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
        // 取得RelativeLayout
        rlBackgroundPanel = (RelativeLayout) findViewById(R.id.activity_weather_result);
        // 取得該張圖片，並放置在變數bmpDrawImg
        bmpDrawImg = new BitmapDrawable(getResources().openRawResource(+R.drawable.bg_all));
        // 最後就是設定圖片
        rlBackgroundPanel.setBackgroundDrawable(bmpDrawImg);
    }

    class LoadIdioms extends AsyncTask<String, String, String> {
        /**
         * getting Idioms from url
         */
        protected String doInBackground(String... args) {
            final ArrayList<WeatherResultIdioms> allData = new ArrayList<>();
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
                            WeatherResultIdioms ii = gson.fromJson(reader, WeatherResultIdioms.class);
                            //加進陣列清單裡
                            allData.add(ii);
                        }

                        result[0] = allData.get(0).getCity();
                        result[1] = allData.get(0).getArea();
                        result[2] = allData.get(0).getMorninglowtemp();
                        result[3] = allData.get(0).getMorninghightemp();
                        result[4] = allData.get(0).getMorning();
                        result[5] = allData.get(0).getNightlowtemp();
                        result[6] = allData.get(0).getNighthightemp();
                        result[7] = allData.get(0).getNight();

                        for (int i = 0; i < 8; i++) {
                            System.out.println("Result：" + result[i]);
                        }


                        city.setText(result[0]);
                        area.setText(result[1]);
                        morningtep.setText("白天溫度：" + result[2] + "~" + result[3] + " °C");
                        morning.setText(result[4]);
                        nighttep.setText("晚上溫度：" + result[5] + "~" + result[6] + " °C");
                        night.setText(result[7]);

                        getRecommand();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(WeatherResult.this, error.toString(), Toast.LENGTH_LONG).show();
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

    private void getRecommand() {
        final int temp = (Integer.parseInt(result[2]) + Integer.parseInt(result[3])) / 2;
        final String[] hotItem = {"短袖", "短褲", "背心", "裙子"};
        final String[] coldItem = {"長袖", "長褲", "外套"};
        final Random random = new Random();
        trend.getTrendData(new Trend.VolleyCallback() {
            public void onSuccess(String[] trendArray) {
                String s;
                if (temp > 27) {
                    s = "<font color=\"#000047\">今天穿上</font> " +
                            "<font color=\"#74D3AE\">"+trendArray[random.nextInt(10)] + hotItem[random.nextInt(4)]+"</font>" +
                            "<font color=\"#000047\"> 吧！</font>";
                    recommand.setText(Html.fromHtml(s));
                } else {
                    s = "<font color=\"#000047\">今天穿上</font> " +
                            "<font color=\"#74D3AE\">"+trendArray[random.nextInt(10)] + coldItem[random.nextInt(3)]+"</font>" +
                            "<font color=\"#00047\"> 吧！</font>";
                    recommand.setText(Html.fromHtml(s));
                }
            }
        });
    }

    private View.OnClickListener imgOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {

            imgrecommand.setVisibility(View.INVISIBLE);
            recommand.setVisibility(View.VISIBLE);
        }
    };

    private View.OnClickListener ibtnOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.icsearch:
                    intent.setClass(WeatherResult.this, Search.class);
                    startActivity(intent);
                    break;
                case R.id.icimage:
                    new AlertDialog.Builder(WeatherResult.this)
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
                    intent.setClass(WeatherResult.this, MainActivity.class);
                    startActivity(intent);
                    break;
                case R.id.iclike:
                    intent.setClass(WeatherResult.this, Like.class);
                    startActivity(intent);
                    break;
                case R.id.icsetting:
                    intent.setClass(WeatherResult.this, Setting.class);
                    startActivity(intent);
                    break;
            }
        }
    };

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

                passintent.setClass(WeatherResult.this, SearchResult.class);
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

class WeatherResultIdioms {

    private String city;
    private String area;
    private String days;
    private String morninghightemp;
    private String morninglowtemp;
    private String morning;
    private String nighthightemp;
    private String nightlowtemp;
    private String night;

    public WeatherResultIdioms() {
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

    public String getMorninghightemp() {
        return morninghightemp;
    }

    public String getMorninglowtemp() {
        return morninglowtemp;
    }

    public String getMorning() {
        return morning;
    }

    public String getNighthightemp() {
        return nighthightemp;
    }

    public String getNightlowtemp() {
        return nightlowtemp;
    }

    public String getNight() {
        return night;
    }

}

