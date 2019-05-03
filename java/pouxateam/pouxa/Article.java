package pouxateam.pouxa;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import java.util.ArrayList;
public class Article extends AppCompatActivity {

    private ListView list;
    private TextView tvtitle;
    ArticleListAdapter listAdapter;
    ArrayList<String> sttitle = new ArrayList<>();
    ArrayList<String> sturl = new ArrayList<>();
    private ImageButton icsearch, icimage, ichome, iclike, icsetting;
    Intent intent = new Intent();
    private RelativeLayout rlBackgroundPanel = null;
    private BitmapDrawable bmpDrawImg = null;
    String url_search;
    String bloggerkey;
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
        setContentView(R.layout.activity_article);

        fnSetBackground(); // 呼叫設置背景函數


        //取得前一個Activity傳過來的Bundle物件，當作搜尋資料庫的keywords
        Bundle bundle = getIntent().getExtras();
        bloggerkey = bundle.get("blogger").toString();
        url_search = "http://163.13.201.111/searchblogger.php?keyblogger=" + bloggerkey;

        list = (ListView) findViewById(R.id.list);
        icsearch = (ImageButton) findViewById(R.id.icsearch);
        icimage = (ImageButton) findViewById(R.id.icimage);
        choose.add("拍攝照片");
        choose.add("從相簿選取");
        choose.add("取消");
        ichome = (ImageButton) findViewById(R.id.ichome);
        iclike = (ImageButton) findViewById(R.id.iclike);
        icsetting = (ImageButton) findViewById(R.id.icsetting);

        tvtitle = (TextView) findViewById(R.id.tvtitle);
        setTvtitle();

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
        rlBackgroundPanel = (RelativeLayout) findViewById(R.id.activity_article);
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


            final ArrayList<ArticleResultIdioms> allData = new ArrayList<>();
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url_search, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        System.out.println("url_search：" + url_search);
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
                            ArticleResultIdioms ii = gson.fromJson(reader, ArticleResultIdioms.class);
                            //加進陣列清單裡
                            allData.add(ii);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e){
                        e.printStackTrace();
                    }

                    try{
                        for (int i = 0; i < allData.size(); i++) {
                            sttitle.add(allData.get(i).getTitle());
                            sturl.add(allData.get(i).getURL());
                        }

                        listAdapter = new ArticleListAdapter(Article.this, sttitle);
                        list.setAdapter(listAdapter);
                    }catch(Exception ee){
                        ee.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(Article.this, "請檢查網路連線", Toast.LENGTH_LONG).show();
                }
            });

            requestQueue.add(stringRequest);
            return "Success";
            // Building Parameters
        }

        protected void onPostExecute(String success){

            //list監聽點擊事件，開啟瀏覽器
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    System.out.println("OPEN："+sturl.get(position));
                    Uri uri = Uri.parse(sturl.get(position));
                    Intent browser = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(browser);
                }
            });
        }


    }

    private View.OnClickListener ibtnOnClickListener = new View.OnClickListener() {

        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.icsearch:
                    intent.setClass(Article.this, Search.class);
                    break;
                case R.id.icimage:
                    new AlertDialog.Builder(Article.this)
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
                    intent.setClass(Article.this, MainActivity.class);
                    break;
                case R.id.iclike:
                    intent.setClass(Article.this, Like.class);
                    break;
                case R.id.icsetting:
                    intent.setClass(Article.this, Setting.class);
                    break;
            }
        }
    };

    protected void setTvtitle() {
        Thread thread = new Thread(new Runnable() {
            Drawable drawable = null;
            String titlename = "";
            @Override
            public void run() {
                switch (bloggerkey) {
                    case "ananchen":
                        titlename = "小安";
                        drawable = getResources().getDrawable(R.drawable.blog_ananchen);
                        break;
                    case "benshee":
                        titlename = "蘇花猴";
                        drawable = getResources().getDrawable(R.drawable.blog_benshee);
                        break;
                    case "eyes":
                        titlename = "77涵";
                        drawable = getResources().getDrawable(R.drawable.blog_eyes);
                        break;
                    case "millyq":
                        titlename = "米粒Q";
                        drawable = getResources().getDrawable(R.drawable.blog_millyq);
                        break;
                    case "stellahyc":
                        titlename = "史黛拉";
                        drawable = getResources().getDrawable(R.drawable.blog_stellahyc);
                        break;
                    case "tramy":
                        titlename = "崔咪";
                        drawable = getResources().getDrawable(R.drawable.blog_tramy);
                        break;
                    case "youwin":
                        titlename = "金老佛爺";
                        drawable = getResources().getDrawable(R.drawable.blog_youwin);
                        break;
                    case "loislinlin":
                        titlename = "露易絲";
                        drawable = getResources().getDrawable(R.drawable.blog_loislinlin);
                        break;
                    case "rockyrocket":
                        titlename = "娃娃yumi";
                        drawable = getResources().getDrawable(R.drawable.blog_rockyrocket);
                        break;
                    case "sophiefish":
                        titlename = "蘇菲魚";
                        drawable = getResources().getDrawable(R.drawable.blog_sophiefish);
                        break;
                    case "hannn":
                        titlename = "Amigo";
                        drawable = getResources().getDrawable(R.drawable.blog_hannn);
                        break;
                    case "cuterosalind":
                        titlename = "蘋果";
                        drawable = getResources().getDrawable(R.drawable.blog_cuterosalind);
                        break;
                    case "lsberryw":
                        titlename = "小草莓小姐";
                        drawable = getResources().getDrawable(R.drawable.blog_lsberryw);
                        break;
                    case "mavismay":
                        titlename = "哈哈梅微絲";
                        drawable = getResources().getDrawable(R.drawable.blog_mavismay);
                        break;
                    case "michellebuy":
                        titlename = "米雪兒";
                        drawable = getResources().getDrawable(R.drawable.blog_michellebuy);
                        break;
                    case "zoey":
                        titlename = "機器娃娃丁小雨";
                        drawable = getResources().getDrawable(R.drawable.blog_zoey);
                        break;
                }
                //建立一個ImageSpan元件並帶入要插入的圖片
                drawable.setBounds(0, 0, 150, 150);             //設置圖片大小
                ImageSpan mImageSpan = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);

                //建立一個SpannableString元件並帶入要顯示的文字字串
                SpannableString mSpannableString = new SpannableString(" " + titlename);

                //插入mImageSpan圖片，並指定在字串裡的第22個位置到23個位置進行插入 (總字串長度為23，圖片插入位置為22-23的位置)
                mSpannableString.setSpan(mImageSpan, 0, 1, 0);

                //將組合後的文字圖片放入TextView裡
                tvtitle.setText(mSpannableString);
            }
        });
        thread.start();
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

                passintent.setClass(Article.this, SearchResult.class);
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


class ArticleResultIdioms {

    private String titlenameall;
    private String titleurlall;

    public ArticleResultIdioms() {}

    public String getTitle() {
        return titlenameall;
    }

    public String getURL() {
        return titleurlall;
    }


}

class ArticleListAdapter extends BaseAdapter {

    Context context;
    ArrayList<String> sttitle;

    private static LayoutInflater inflater = null;

    public ArticleListAdapter(Context context, ArrayList<String> sttitle) {
        // TODO Auto-generated constructor stub
        this.context = context;
        this.sttitle = sttitle;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return sttitle.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return sttitle.get(position);
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
            vi = inflater.inflate(R.layout.article_list_style, null);
        Typeface font = Typeface.createFromAsset(context.getAssets(), "fonts/typeface.otf");

        TextView atitle = (TextView) vi.findViewById(R.id.articletitle);
        atitle.setText(sttitle.get(position));
        atitle.setTypeface(font);
        atitle.setSelected(true);
        atitle.setTextSize(23);
        TextView elements = (TextView) vi.findViewById(R.id.elements);
        elements.setTextSize(20);
        switch(position){
            case 0:
                elements.setText("#綁帶   #流蘇");
                break;
            case 1:
                elements.setText("#綁帶   #流蘇");
                break;
            case 2:
                elements.setText("#流蘇");
                break;
            case 3:
                elements.setText("#針織   #綁帶");
                break;
            case 4:
                elements.setText("#綁帶");
                break;
            case 5:
                elements.setText("");
                break;
            case 6:
                elements.setText("");
                break;
            case 7:
                elements.setText("");
                break;
            case 8:
                elements.setText("#雪紡");
                break;
        }

        return vi;
    }
}
