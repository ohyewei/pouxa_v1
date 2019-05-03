package pouxateam.pouxa;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

public class Product extends Activity {

    private ImageButton icsearch, icimage, ichome, iclike, icsetting;
    Intent intent = new Intent();
    private RelativeLayout rlBackgroundPanel = null;
    private BitmapDrawable bmpDrawImg = null;
    //以圖搜圖
    CreatePHash p;
    String imagecode;
    private final static int CAMERA = 66;
    private final static int PHOTO = 99;
    final ArrayList<String> choose = new ArrayList<>();

    // Declare Variables
    ImageView imgppicture;
    TextView txtpname;
    TextView txtsname;
    TextView txtpprice;
    TextView txtpchoose;
    ImageButton btnbuy;
    ImageButton btnshare;
    ImageButton btnlike;

    String ppicture;
    String pname;
    String sname;
    String pprice;
    String pchoose;
    String purl;

    View view;
    private RequestQueue requestQueue;

    Favorites favorites = new Favorites();
    @Override
    public void onResume() {
        super.onResume();
        //When BACK BUTTON is pressed, the activity on the stack is restarted
        //判斷此商品是否已加入我的收藏

        if(favorites.findFavorite(Product.this,txtpname.getText().toString()) == false){
            System.out.println("尚未加入收藏");
            btnlike.setImageDrawable(getResources().getDrawable(R.drawable.btn_heart_unclick));
            btnlike.setTag("unclick");                //還沒按下收藏
        }else{
            btnlike.setTag("click");                  //已經按下收藏
            System.out.println("已加入收藏");
            btnlike.setImageDrawable(getResources().getDrawable(R.drawable.btn_heart_click));
        }
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        FontsOverride.setDefaultFont(this, "MONOSPACE", "fonts/typeface.otf");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        // Retrieve data from MainActivity on item click event
        fnSetBackground(); // 呼叫設置背景函數
        Intent i = getIntent();
        // Get the results
        ppicture = i.getStringExtra("ppicture");
        pname = i.getStringExtra("pname");
        sname = i.getStringExtra("sname");
        pprice = i.getStringExtra("pprice");
        pchoose = i.getStringExtra("pchoose");
        purl = i.getStringExtra("purl");

        imgppicture = (ImageView) findViewById(R.id.ppicture);
        txtpname = (TextView) findViewById(R.id.pname);
        txtsname = (TextView) findViewById(R.id.sname);
        txtpprice = (TextView) findViewById(R.id.pprice);
        txtpchoose = (TextView)findViewById(R.id.pchoose);
        btnbuy = (ImageButton)findViewById(R.id.btnbuy);
        btnshare = (ImageButton)findViewById(R.id.btnshare);
        btnlike = (ImageButton)findViewById(R.id.btnlike);

        icsearch = (ImageButton) findViewById(R.id.icsearch);
        icimage = (ImageButton) findViewById(R.id.icimage);
        choose.add("拍攝照片");
        choose.add("從相簿選取");
        choose.add("取消");
        ichome = (ImageButton) findViewById(R.id.ichome);
        iclike = (ImageButton) findViewById(R.id.iclike);
        icsetting = (ImageButton) findViewById(R.id.icsetting);
        ImageButton[] ibtnres = {icsearch, icimage, ichome, iclike, icsetting, btnbuy, btnlike};
        for (ImageButton ib : ibtnres) {
            ib.setOnClickListener(ibtnOnClickListener);
        }

        //讀取網路圖片後用ImageView顯示
        requestQueue = Volley.newRequestQueue(this);
        //建立一個AsyncTask執行緒進行圖片讀取動作，並帶入圖片連結網址路徑
        new AsyncTask<String, String, String>()
        {
            @Override
            protected String doInBackground(String... params)
            {
                ImageRequest imagerequest = new ImageRequest(ppicture,
                        new Response.Listener<Bitmap>() {
                            @Override
                            public void onResponse(Bitmap bitmap) {
                                Drawable db = new BitmapDrawable(bitmap);
                                imgppicture.setImageDrawable(db);
                                imgppicture.setBackgroundColor(Color.alpha(100));
                            }
                        }, 0, 0, null,
                        new Response.ErrorListener() {
                            public void onErrorResponse(VolleyError error) {
                            }
                        });
                requestQueue.add(imagerequest);
                return "success";
            }
        }.execute();
        // Load the results into the TextViews
        txtpname.setText(pname);
        txtsname.setText(sname);
        txtpprice.setText(pprice);
        txtpchoose.setText("規格："+pchoose);
        txtpchoose.setSelected(true);

        p = new CreatePHash();
    }

    //設置背景函數
    public void fnSetBackground() {
        // 先取得RelativeLayout
        rlBackgroundPanel = (RelativeLayout) findViewById(R.id.activity_product);
        // 取得該張圖片，並放置在變數bmpDrawImg
        bmpDrawImg = new BitmapDrawable(getResources().openRawResource(+R.drawable.bg_all));
        // 最後就是設定圖片
        rlBackgroundPanel.setBackgroundDrawable(bmpDrawImg);
    }

    //icon換頁監聽
    private View.OnClickListener ibtnOnClickListener = new View.OnClickListener() {

        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.icsearch:
                    intent.setClass(Product.this, Search.class);
                    startActivity(intent);
                    break;
                case R.id.icimage:
                    new AlertDialog.Builder(Product.this)
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
                    intent.setClass(Product.this, MainActivity.class);
                    startActivity(intent);
                    break;
                case R.id.iclike:
                    intent.setClass(Product.this, Like.class);
                    startActivity(intent);
                    break;
                case R.id.icsetting:
                    intent.setClass(Product.this, Setting.class);
                    startActivity(intent);
                    break;
                //開啟瀏覽器
                case R.id.btnbuy:
                    Uri uri = Uri.parse(purl);
                    intent = new Intent(Intent.ACTION_VIEW, uri);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    break;
                //分享
                case R.id.btnshare:
                    //從layout控制
                    break;
                //加入收藏
                case R.id.btnlike:
                    Bitmap bitmap = Bitmap.createBitmap(imgppicture.getWidth(),imgppicture.getHeight(),Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(bitmap);
                    imgppicture.draw(canvas);
                    String productpicture = encodeToBase64(bitmap,Bitmap.CompressFormat.PNG, 100);
                    FavoriteItem nofavoriteItem = new FavoriteItem("尚未有收藏","");
                    FavoriteItem favoriteItem = new FavoriteItem(txtpname.getText().toString(),productpicture);
                    if(btnlike.getTag() == "unclick"){
                        btnlike.setImageDrawable(getResources().getDrawable(R.drawable.btn_heart_click));
                        btnlike.setTag("click");
                        favorites.addFavorite(Product.this,favoriteItem);
                    }else if(btnlike.getTag() == "click"){
                        btnlike.setImageDrawable(getResources().getDrawable(R.drawable.btn_heart_unclick));
                        btnlike.setTag("unclick");
                        favorites.removeFavorite(Product.this,favoriteItem);
                    }
                    break;
            }
        }
    };

    //分享
    public void open(View view){
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, purl);
        startActivity(Intent.createChooser(shareIntent, "分享至..."));

    }

    //圖片轉成字串(為了加進JSON裡)
    public String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality)
    {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
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

                passintent.setClass(Product.this, SearchResult.class);
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
