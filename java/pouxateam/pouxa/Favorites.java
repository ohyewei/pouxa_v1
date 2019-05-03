package pouxateam.pouxa;

/**
 * Created by Vivian on 2016/8/25.
 */



import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Favorites {

    public static final String PREFS_NAME = "PRODUCT_APP";
    public static final String FAVORITES = "Product_Favorite";

    public Favorites() {
        super();
    }

    // This four methods are used for maintaining favorites.
    public void saveFavorites(Context context, List<FavoriteItem> favorites) {
        SharedPreferences myFavorites;
        SharedPreferences.Editor editor;

        myFavorites = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        editor = myFavorites.edit();

        //把List轉成String後用SharedPreferences儲存
        Gson gson = new Gson();
        String jsonFavorites = gson.toJson(favorites);

        editor.putString(FAVORITES, jsonFavorites);

        editor.commit();
    }

    public void addFavorite(Context context, FavoriteItem product) {
        List<FavoriteItem> favorites = getFavorites(context);
        if (favorites == null)
            favorites = new ArrayList<>();
        
        favorites.add(product);
        saveFavorites(context, favorites);
    }

    public void removeFavorite(Context context, FavoriteItem product) {
        ArrayList<FavoriteItem> favorites = getFavorites(context);
        int index = -1;
        for( FavoriteItem fi :favorites ){
            String producttitle = fi.getName();             //我的收藏中的商品名
            if( producttitle.equals(product.getName())){
                index++;
                break;
            }else
                index++;
        }
        try{
            favorites.remove(index);
            saveFavorites(context, favorites);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public boolean findFavorite(Context context, String producttitle) {
        ArrayList<FavoriteItem> favorites = getFavorites(context);
        if (favorites != null) {
            for( FavoriteItem fi : favorites ){
                if(fi.getName().contains(producttitle)){
                    return true;
                }
            }
        }
        return false;       //項目不存在
    }
    public void clearFavorite(Context context) {
        ArrayList<FavoriteItem> favorites = getFavorites(context);
        if (favorites != null) 
            favorites.clear();
            saveFavorites(context, favorites);
        
    }

    public ArrayList<FavoriteItem> getFavorites(Context context) {
        SharedPreferences myFavorites;
        List<FavoriteItem> favorites;

        myFavorites = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);

        if (myFavorites.contains(FAVORITES)) {
            String jsonFavorites = myFavorites.getString(FAVORITES, null);
            //把String轉回List
            Gson gson = new Gson();
            FavoriteItem[] favoriteItems = gson.fromJson(jsonFavorites,
                    FavoriteItem[].class);

            favorites = Arrays.asList(favoriteItems);
            favorites = new ArrayList<>(favorites);
        } else
            return null;
        return (ArrayList<FavoriteItem>) favorites;
    }
}