package pouxateam.pouxa;

/**
 * Created by Vivian on 2016/8/26.
 */
public class FavoriteItem {

    private String name = "沒有收藏項目";
    private String picture = "";

    public FavoriteItem() {
        super();
    }

    public FavoriteItem(String name, String picture) {
        super();

        this.name = name;
        this.picture = picture;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

}
