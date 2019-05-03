package pouxateam.pouxa;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class Like extends AppCompatActivity {

    private ListView listView;
    private TextView tvtitle;
    Favorites favorites;
    private MyAdapter listAdapter;
    private RelativeLayout rlBackgroundPanel = null;
    private BitmapDrawable bmpDrawImg = null;
    private ImageButton icsearch, icimage, ichome, iclike, icsetting;
    Intent intent = new Intent();
    final ArrayList<String> choose = new ArrayList<>();
    ArrayList<FavoriteItem> favoritesArray;
    boolean isDelete = false;
    boolean isClear = false;

    //資料庫
    String url_search;
    String searchtype;
    String keyword;
    private RequestQueue requestQueue;
    Gson gson = new Gson();

    //以圖搜圖
    CreatePHash p;
    String imagecode;
    private final static int CAMERA = 66;
    private final static int PHOTO = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FontsOverride.setDefaultFont(this, "MONOSPACE", "fonts/typeface.otf");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_like);
        fnSetBackground();


        listView = (ListView) findViewById(R.id.list_like);
        tvtitle = (TextView) findViewById(R.id.tvtitle);
        setTvtitle();

        favorites = new Favorites();
        favoritesArray = favorites.getFavorites(Like.this);

        try{
            favorites = new Favorites();
            favoritesArray = favorites.getFavorites(Like.this);
            if(favoritesArray != null){
                listAdapter = new MyAdapter(this,R.layout.favorites_list_style,favorites.getFavorites(Like.this));
                listView.setAdapter(listAdapter);
            }else{
                Toast.makeText(this,"尚未有收藏項目",Toast.LENGTH_SHORT).show();
            }
            //list監聽點擊事件，開啟瀏覽器
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    FavoriteItem favoriteItem = listAdapter.getItem(position);
                    searchtype = "productname";
                    keyword = favoriteItem.getName();
                    //中文關鍵字需要先做utf編碼
                    try {
                        keyword = URLEncoder.encode(keyword, "utf-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    url_search = "http://163.13.201.111/searchproduct.php?searchtype=" + searchtype + "&keyword=" + keyword;
                    requestQueue = Volley.newRequestQueue(getApplicationContext());
                    new LoadIdioms().execute();
                }
            });
        }catch(Exception e){
            Toast.makeText(this,"沒有收藏項目",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        findViewById(R.id.clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                favorites.clearFavorite(Like.this);
                listAdapter.clear();
                listAdapter.notifyDataSetChanged();
                Toast.makeText(Like.this,"清空我的最愛", Toast.LENGTH_SHORT).show();
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
    //連接資料庫
    class LoadIdioms extends AsyncTask<String, String, String> {
        /**
         * getting Idioms from url
         */
        ArrayList<DetailIdioms> allData = new ArrayList<>();

        protected String doInBackground(String... args) {

            StringRequest stringRequest = new StringRequest(Request.Method.GET, url_search, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        System.out.println("url_search：" + url_search);

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
                            DetailIdioms ii = gson.fromJson(reader, DetailIdioms.class);
                            //加進陣列清單裡
                            allData.add(ii);
                        }
                        final Intent intent = new Intent(Like.this, Product.class);
                        intent.putExtra("ppicture", allData.get(0).getProductpictureurl());
                        intent.putExtra("pname", allData.get(0).getProducttitle());
                        intent.putExtra("sname", allData.get(0).getStorename());
                        intent.putExtra("pprice", allData.get(0).getProductprice());
                        String pchoose = allData.get(0).getProductchoose();
                        if (allData.get(0).getProductchoose().length() > 0) {
                            pchoose = pchoose.substring(1).replace("\n", "/");
                        } else {
                            pchoose = "單一規格";
                        }
                        intent.putExtra("pchoose", pchoose);
                        intent.putExtra("purl", allData.get(0).getProducturl());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        // Start Product Class
                        startActivity(intent);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(Like.this, error.toString(), Toast.LENGTH_LONG).show();
                }
            });

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(stringRequest);
            return "Success";
            // Building Parameters
        }

        protected void onPostExecute(String success) {


        }
    }

    class MyAdapter extends ArrayAdapter<FavoriteItem> {

        ArrayList<FavoriteItem> favoritesArray;

        public MyAdapter(Context context, int textViewResourceId, ArrayList<FavoriteItem> objects){

            super(context,textViewResourceId,objects);
            this.favoritesArray = objects;
        }
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            LayoutInflater myInflater = getLayoutInflater();
            convertView = myInflater.inflate(R.layout.favorites_list_style, parent,false);

            ImageView picture = (ImageView) convertView.findViewById(R.id.img_pdt_pic);
            TextView name = (TextView) convertView.findViewById(R.id.txt_pdt_name);
            ImageButton delete = (ImageButton) convertView.findViewById(R.id.btn_delete);


            Bitmap bitmap = decodeBase64(favoritesArray.get(position).getPicture());
            Drawable drawable = new BitmapDrawable(getResources(), bitmap);
            picture.setImageDrawable(drawable);
            picture.setBackgroundResource(R.color.transparent);
            name.setText(favoritesArray.get(position).getName());
            name.setSelected(true);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    new AlertDialog.Builder(Like.this)
                            .setTitle("")
                            .setMessage("確認刪除？")
                            .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    favorites.removeFavorite(Like.this,favoritesArray.get(position));
                                    favoritesArray.remove(position);
                                    listAdapter.notifyDataSetChanged();
                                    Toast.makeText(Like.this,"刪除", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton("否", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .show();
                }
            });
            return convertView;
        }

        @Override
        public FavoriteItem getItem(int position) {
            return favoritesArray.get(position);
        }
    }

    public Bitmap decodeBase64(String input)
    {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    public void fnSetBackground() {
        // 先取得RelativeLayout
        rlBackgroundPanel = (RelativeLayout) findViewById(R.id.activity_like);
        // 取得該張圖片，並放置在變數bmpDrawImg
        bmpDrawImg = new BitmapDrawable(getResources().openRawResource(+R.drawable.bg_all));
        // 最後就是設定圖片
        rlBackgroundPanel.setBackgroundDrawable(bmpDrawImg);
    }

    protected void setTvtitle() {
        //建立一個ImageSpan元件並帶入要插入的圖片
        Drawable drawable = getResources().getDrawable(R.drawable.btn_heart_click);
        drawable.setBounds(0, 0, 144, 144);             //設置圖片大小
        ImageSpan mImageSpan = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);

        //建立一個SpannableString元件並帶入要顯示的文字字串
        SpannableString mSpannableString = new SpannableString(" 我的收藏");

        //插入mImageSpan圖片，並指定在字串裡的第22個位置到23個位置進行插入 (總字串長度為23，圖片插入位置為22-23的位置)
        mSpannableString.setSpan(mImageSpan, 0, 1, 0);

        //將組合後的文字圖片放入TextView裡
        tvtitle.setText(mSpannableString);
    }

    //icon換頁監聽
    private View.OnClickListener ibtnOnClickListener = new View.OnClickListener() {

        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.icsearch:
                    intent.setClass(Like.this, Search.class);
                    startActivity(intent);
                    break;
                case R.id.icimage:
                    new AlertDialog.Builder(Like.this)
                            .setItems(choose.toArray(new String[choose.size()]), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int position) {
                                    if(choose.get(position).equals("拍攝照片")){
                                        //開啟相機
                                    }

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
                    intent.setClass(Like.this, MainActivity.class);
                    startActivity(intent);
                    break;
                case R.id.iclike:
                    intent.setClass(Like.this, Like.class);
                    startActivity(intent);
                    break;
                case R.id.icsetting:
                    intent.setClass(Like.this, Setting.class);
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

                passintent.setClass(Like.this, SearchResult.class);
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
}

class DetailIdioms {
    private String storenameall;
    private String producturl;
    private String productname;
    private String productprice;
    //private String storescore
    private String productpictureurl;
    private String productchoose;

    public DetailIdioms() {
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

    public String getProductpictureurl() {
        return productpictureurl;
    }

    public String getProductchoose() {
        return productchoose;
    }
}


