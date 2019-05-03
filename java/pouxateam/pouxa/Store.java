package pouxateam.pouxa;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
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

public class Store extends AppCompatActivity {

    private static String url_search = "http://163.13.201.111/getstorename.php";
    private RequestQueue requestQueue;
    Gson gson = new Gson();
    private TableLayout store_table;
    RelativeLayout rlBackgroundPanel = null;
    BitmapDrawable bmpDrawImg = null;
    ArrayList<String> storename = new ArrayList<>();
    Intent intent = new Intent();
    Bundle bundle = new Bundle();
    private TextView tvtitle;
    private ImageButton icsearch, icimage, ichome, iclike, icsetting;

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
        setContentView(R.layout.activity_store);
        tvtitle = (TextView) findViewById(R.id.tvtitle);
        setTvtitle();
        fnSetBackground(); // 呼叫設置背景函數
        store_table = (TableLayout) findViewById(R.id.store_table);
        store_table.setStretchAllColumns(true);
        requestQueue = Volley.newRequestQueue(this);
        new LoadIdioms().execute();

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
        rlBackgroundPanel = (RelativeLayout) findViewById(R.id.activity_store);
        // 取得該張圖片，並放置在變數bmpDrawImg
        bmpDrawImg = new BitmapDrawable(getResources().openRawResource(+R.drawable.bg_all));
        // 最後就是設定圖片
        rlBackgroundPanel.setBackgroundDrawable(bmpDrawImg);
    }

    protected void setTvtitle() {
        //建立一個ImageSpan元件並帶入要插入的圖片
        Drawable drawable = getResources().getDrawable(R.drawable.btn_event);
        drawable.setBounds(0, 0, 144, 144);             //設置圖片大小
        ImageSpan mImageSpan = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
        //建立一個SpannableString元件並帶入要顯示的文字字串
        SpannableString mSpannableString = new SpannableString(" 優惠活動店家");
        //插入mImageSpan圖片，並指定在字串裡的第22個位置到23個位置進行插入 (總字串長度為23，圖片插入位置為22-23的位置)
        mSpannableString.setSpan(mImageSpan, 0, 1, 0);
        //將組合後的文字圖片放入TextView裡
        tvtitle.setText(mSpannableString);
    }

    class LoadIdioms extends AsyncTask<String, String, String> {
        /**
         * getting Idioms from url
         */
        protected String doInBackground(String... args) {

            final TableLayout.LayoutParams row_layout = new TableLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            final TableRow.LayoutParams view_layout = new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);

            final ArrayList<StoreResultIdioms> allData = new ArrayList<>();

            StringRequest stringRequest = new StringRequest(url_search, new Response.Listener<String>() {
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
                            StoreResultIdioms ii = gson.fromJson(reader, StoreResultIdioms.class);
                            //加進陣列清單裡
                            allData.add(ii);
                        }

                        for (int i = 0; i < allData.size(); i++) {

                            storename.add(allData.get(i).getStoreName());


                            final TableRow tr = new TableRow(Store.this);
                            tr.setLayoutParams(row_layout);
                            tr.setGravity(Gravity.CENTER);
                            tr.setPadding(0, 50, 0, 50);


                            String imageurl = allData.get(i).getStorephoto();

                            final int TAG_IB = i;

                            System.out.println(imageurl);
                            // Retrieves an image specified by the URL, displays it in the UI.
                            ImageRequest imagerequest = new ImageRequest(imageurl,
                                    new Response.Listener<Bitmap>() {
                                        @Override
                                        public void onResponse(Bitmap bitmap) {
                                            Drawable db = new BitmapDrawable(bitmap);
                                            ImageButton store_picture = new ImageButton(Store.this);
                                            store_picture.setImageDrawable(db);
                                            store_picture.setMinimumHeight(180);
                                            store_picture.setMinimumWidth(900);
                                            store_picture.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                            store_picture.setBackgroundColor(Color.alpha(100));
                                            store_picture.setLayoutParams(view_layout);
                                            store_picture.setTag(TAG_IB);
                                            store_picture.setOnClickListener(tablerowOnClickListener);
                                            tr.addView(store_picture);


                                            store_table.addView(tr);
                                        }
                                    }, 0, 0, null,
                                    new Response.ErrorListener() {
                                        public void onErrorResponse(VolleyError error) {
                                        }
                                    });
                            requestQueue.add(imagerequest);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener()

            {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(Store.this, error.toString(), Toast.LENGTH_LONG).show();
                }
            });

            requestQueue.add(stringRequest);
            return "Success";
            // Building Parameters
        }
    }

    private View.OnClickListener tablerowOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {

            int tag = Integer.parseInt(v.getTag().toString());

            bundle.putString("storename", storename.get(tag));
            intent.setClass(Store.this, Event.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    };


    private View.OnClickListener ibtnOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.icsearch:
                    intent.setClass(Store.this, Search.class);
                    startActivity(intent);
                    break;
                case R.id.icimage:
                    new AlertDialog.Builder(Store.this)
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
                    intent.setClass(Store.this, MainActivity.class);
                    startActivity(intent);
                    break;
                case R.id.iclike:
                    intent.setClass(Store.this, Like.class);
                    startActivity(intent);
                    break;
                case R.id.icsetting:
                    intent.setClass(Store.this, Setting.class);
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

                passintent.setClass(Store.this, SearchResult.class);
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

class StoreResultIdioms {

    private String storename;
    private String storepictureurl;

    public StoreResultIdioms() {
    }

    public String getStoreName() {
        return storename;
    }

    public String getStorephoto() {
        return storepictureurl;
    }
}

