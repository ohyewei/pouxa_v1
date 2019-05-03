package pouxateam.pouxa;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.List;

public class ResultViewAdapter extends BaseAdapter {

    // Declare Variables
    Context mContext;
    LayoutInflater inflater;
    private List<SearchResultIdioms> productlist = null;
    private ArrayList<SearchResultIdioms> arraylist;
    private RequestQueue requestQueue;

    public ResultViewAdapter(Context context, List<SearchResultIdioms> productlist) {
        mContext = context;
        this.productlist = productlist;
        inflater = LayoutInflater.from(mContext);
        this.arraylist = new ArrayList<>();
        this.arraylist.addAll(productlist);
    }

    public class ViewHolder {
        ImageView productpicture;
        TextView productname;
        TextView storename;
        TextView storescore;
        TextView productprice;
        TextView element1;
        TextView element2;
        TextView element3;

    }

    @Override
    public int getCount() {
        return productlist.size();
    }

    @Override
    public SearchResultIdioms getItem(int position) {
        return productlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        final Trend trend = new Trend(mContext);

        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.result_listview_item, null);
            // Locate the TextViews in result_listview_item.xmltem.xml
            holder.productpicture = (ImageView) view.findViewById(R.id.ppicture);
            holder.productname = (TextView) view.findViewById(R.id.pname);
            holder.storename = (TextView) view.findViewById(R.id.sname);
            holder.storescore = (TextView) view.findViewById(R.id.sscore);
            holder.productprice = (TextView) view.findViewById(R.id.pprice);
            holder.element1 = (TextView) view.findViewById(R.id.element1);
            holder.element2 = (TextView) view.findViewById(R.id.element2);
            holder.element3 = (TextView) view.findViewById(R.id.element3);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        // Set the results into TextViews
        requestQueue = Volley.newRequestQueue(mContext);
        //建立一個AsyncTask執行緒進行圖片讀取動作，並帶入圖片連結網址路徑
        new AsyncTask<String, String, String>() {
            @Override
            protected String doInBackground(String... params) {
                ImageRequest imagerequest = new ImageRequest(productlist.get(position).getProductpictureurl(),
                        new Response.Listener<Bitmap>() {
                            @Override
                            public void onResponse(Bitmap bitmap) {
                                Drawable db = new BitmapDrawable(bitmap);
                                holder.productpicture.setImageDrawable(db);
                                holder.productpicture.setBackgroundColor(Color.alpha(100));
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
        holder.productname.setText(productlist.get(position).getProducttitle());
        holder.storename.setText(productlist.get(position).getStorename());
        int grade = Integer.parseInt(productlist.get(position).getStorescore());
        String stars = "";
        for(int i = 1 ; i <= grade ; i++){
            stars = stars + "★";
        }
        holder.storescore.setText(stars);
        holder.productprice.setText(productlist.get(position).getProductprice());
        trend.getTrendData(new Trend.VolleyCallback() {
            TextView[] elements = {holder.element1, holder.element2, holder.element3};

            @Override
            public void onSuccess(String[] trendArray) {
                int j = 0;      //已經放入幾個元素
                for (int i = 0; i < 10; i++) {
                    if (j > 2) {
                        break;
                    } else if (productlist.get(position).getProducttitle().contains(trendArray[i])) {
                        elements[j].setText(trendArray[i]);
                        elements[j].setVisibility(View.VISIBLE);
                        j++;
                    } else {
                        elements[j].setText("");
                        elements[j].setVisibility(View.GONE);
                    }

                }
            }
        });
        // Listen for ListView Item Click
        view.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                //按下列表後要轉到Product寫在這!
                // Send single item click data to Product Class
                final Intent intent = new Intent(mContext, Product.class);
                //TextView[] elements = { holder.element1, holder.element2, holder.element3 };
                // Pass all data
                intent.putExtra("ppicture", productlist.get(position).getProductpictureurl());
                intent.putExtra("pname", productlist.get(position).getProducttitle());
                intent.putExtra("sname", productlist.get(position).getStorename());
                intent.putExtra("pprice", productlist.get(position).getProductprice());
                String pchoose = productlist.get(position).getProductchoose();
                if(productlist.get(position).getProductchoose().length() > 0){
                    pchoose = pchoose.substring(1).replace("\n","/");
                }else{
                    pchoose = "單一規格";
                }
                intent.putExtra("pchoose",pchoose);
                /*for( int j = 0 ; j < 3 ; j++ ){
                    intent.putExtra("element"+j, elements[j].getText());
                }*/
                intent.putExtra("purl", productlist.get(position).getProducturl());

                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                // Start Product Class
                mContext.startActivity(intent);
            }
        });

        holder.element1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                final Intent intent = new Intent(mContext, SearchResult.class);
                intent.putExtra("searchtype","productname");
                intent.putExtra("keyword",holder.element1.getText());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);

            }
        });

        holder.element2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                final Intent intent = new Intent(mContext, SearchResult.class);
                intent.putExtra("searchtype","productname");
                intent.putExtra("keyword",holder.element2.getText());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);

            }
        });

        holder.element3.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                final Intent intent = new Intent(mContext, SearchResult.class);
                intent.putExtra("searchtype","productname");
                intent.putExtra("keyword",holder.element3.getText());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);

            }
        });

        return view;
    }


    //價格過濾
    public void priceFilter(String low, String high) {
        productlist.clear();
        if (low.length() == 0 && high.length() == 0) {
            productlist.addAll(arraylist);
        } else {
            for (SearchResultIdioms sri : arraylist) {
                int price = Integer.parseInt(sri.getProductprice().substring(0, sri.getProductprice().length() - 1));
                if (price >= Integer.parseInt(low) && price <= Integer.parseInt(high)) {
                    productlist.add(sri);
                }
            }
        }


        if (productlist.size() == 0) {
            Toast.makeText(mContext, "沒有符合的結果", Toast.LENGTH_SHORT).show();
        }
        notifyDataSetChanged();

    }

    // 類型過濾
    public void typeFilter(String keytype1, String keytype2, String conjunction) {
        productlist.clear();
        if (keytype1.length() == 0 && keytype2.length() == 0) {
            productlist.addAll(arraylist);
        } else if (conjunction.equals("OR")) {
            for (SearchResultIdioms sri : arraylist) {
                if (sri.getProducttitle().contains(keytype1) || sri.getProducttitle().contains(keytype2)) {
                    productlist.add(sri);
                }
            }
        } else if (conjunction.equals("AND")) {
            for (SearchResultIdioms sri : arraylist) {
                if (sri.getProducttitle().contains(keytype1) && sri.getProducttitle().contains(keytype2)) {
                    productlist.add(sri);
                }
            }
        }

        if (productlist.size() == 0) {
            Toast.makeText(mContext, "沒有符合的結果", Toast.LENGTH_SHORT).show();
        }
        notifyDataSetChanged();
    }

}