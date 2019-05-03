package pouxateam.pouxa;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
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
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class Event extends AppCompatActivity {

    private String store, storekey;
    private ImageButton icsearch, icimage, ichome, iclike, icsetting;
    private ListView list;
    private TextView tvtitle;
    EventListAdapter listAdapter;
    ArrayList<String> storename = new ArrayList<>();
    ArrayList<String> event = new ArrayList<>();
    ArrayList<String> eventname = new ArrayList<>();
    ArrayList<String> eventtime = new ArrayList<>();
    ArrayList<String> eventurl = new ArrayList<>();
    Intent intent = new Intent();
    private RelativeLayout rlBackgroundPanel = null;
    private BitmapDrawable bmpDrawImg = null;
    String url_search;
    private RequestQueue requestQueue;
    Gson gson = new Gson();

    //以圖搜圖
    CreatePHash p;
    String imagecode;
    private final static int CAMERA = 66;
    private final static int PHOTO = 99;
    final ArrayList<String> choose = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FontsOverride.setDefaultFont(this, "MONOSPACE", "fonts/typeface.otf");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        fnSetBackground(); // 呼叫設置背景函數
        tvtitle = (TextView) findViewById(R.id.tvtitle);

        //取得前一個Activity傳過來的Bundle物件，當作搜尋資料庫的keywords
        Bundle bundle = getIntent().getExtras();
        store = bundle.get("storename").toString();
        //中文關鍵字需要先做utf編碼
        try {
            storekey = URLEncoder.encode(store, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        url_search = "http://163.13.201.111/searchactivity.php?keystorename=" + storekey;

        list = (ListView) findViewById(R.id.list);
        icsearch = (ImageButton) findViewById(R.id.icsearch);
        icimage = (ImageButton) findViewById(R.id.icimage);
        choose.add("拍攝照片");
        choose.add("從相簿選取");
        choose.add("取消");
        ichome = (ImageButton) findViewById(R.id.ichome);
        iclike = (ImageButton) findViewById(R.id.iclike);
        icsetting = (ImageButton) findViewById(R.id.icsetting);


        requestQueue = Volley.newRequestQueue(this);
        new LoadIdioms().execute();

        ImageButton[] ibtnres = {icsearch, icimage, ichome, iclike, icsetting};
        for (ImageButton ib : ibtnres) {
            ib.setOnClickListener(ibtnOnClickListener);
        }

        p = new CreatePHash();

    }

    //設置背景函數
    public void fnSetBackground() {
        // 先取得RelativeLayout
        rlBackgroundPanel = (RelativeLayout) findViewById(R.id.activity_event);
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
            final ArrayList<EventResultIdioms> allData = new ArrayList<>();
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url_search, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        System.out.println("url_search：" + url_search);
                        //System.out.println("Respone：" + response);
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
                            allData.add(ii);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        for (int i = 0; i < allData.size(); i++) {
                            storename.add(allData.get(i).getStorename());
                            event.add(allData.get(i).getEventname());
                            eventurl.add(allData.get(i).getEventurl());
                        }
                        tvtitle.setText(storename.get(0));
                        getRealData(event);          //修正活動名稱跟時間在一起的問題
                        listAdapter = new EventListAdapter(getApplicationContext(), eventname, eventtime);
                        list.setAdapter(listAdapter);
                    } catch (Exception ee) {
                        ee.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(Event.this, "請檢查網路連線", Toast.LENGTH_LONG).show();
                }
            });

            requestQueue.add(stringRequest);
            return "Success";
            // Building Parameters
        }

        protected void onPostExecute(String success){

            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    System.out.println("OPEN："+eventurl.get(position));
                    Uri uri = Uri.parse(eventurl.get(position));
                    Intent browser = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(browser);
                }
            });
        }
    }

    //修正時間和名稱在一起的問題
    private void getRealData(ArrayList<String> event){
        for( int i = 0 ; i < event.size() ; i++){
            String s = event.get(i);
            System.out.println("event:"+event.get(i));
            //新增時間到eventtime
            eventtime.add(s.substring(s.indexOf("\n")+1,s.length()));
            System.out.println("eventtime:"+eventtime.get(i));
            //新增活動名稱到eventname
            eventname.add(i,s.substring(0,s.indexOf("\n")));
            System.out.println("eventname2:"+eventname.get(i));
        }
    }

    private View.OnClickListener ibtnOnClickListener = new View.OnClickListener() {

        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.icsearch:
                    intent.setClass(Event.this, Search.class);
                    startActivity(intent);
                    break;
                case R.id.icimage:
                    new AlertDialog.Builder(Event.this)
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
                    intent.setClass(Event.this, MainActivity.class);
                    startActivity(intent);
                    break;
                case R.id.iclike:
                    intent.setClass(Event.this, Like.class);
                    startActivity(intent);
                    break;
                case R.id.icsetting:
                    intent.setClass(Event.this, Setting.class);
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

                passintent.setClass(Event.this, SearchResult.class);
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

    @Override
    protected  void onPause(){
        super.onPause();
        System.gc();
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

class EventResultIdioms {

    private String storenameall;
    private String activityname;
    private String activityurl;

    public EventResultIdioms() {
    }

    public String getStorename() {

        return storenameall;
    }

    public String getEventname() {

        return activityname;
    }


    public String getEventurl() {
        return activityurl;
    }

}

class EventListAdapter extends BaseAdapter {

    Context context;
    ArrayList<String> eventname;
    ArrayList<String> eventtime;

    private static LayoutInflater inflater = null;

    public EventListAdapter(Context context, ArrayList<String> eventname, ArrayList<String> eventtime) {
        // TODO Auto-generated constructor stub
        this.context = context;
        this.eventname = eventname;
        this.eventtime = eventtime;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return eventname.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return eventname.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View vi = convertView;
        if (vi == null)
            vi = inflater.inflate(R.layout.event_list_style, null);
        Typeface font = Typeface.createFromAsset(context.getAssets(), "fonts/typeface.otf");

        TextView name = (TextView) vi.findViewById(R.id.name);
        name.setText(eventname.get(position));
        name.setTypeface(font);
        name.setSelected(true);
        name.setTextSize(25);
        TextView time = (TextView) vi.findViewById(R.id.time);
        time.setText(eventtime.get(position));
        time.setTypeface(font);
        time.setSelected(true);
        time.setTextSize(23);
        return vi;
    }
}
