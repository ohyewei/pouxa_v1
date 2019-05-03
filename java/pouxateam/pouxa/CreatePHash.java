package pouxateam.pouxa;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import java.io.InputStream;

/**
 * Created by Vivian on 2016/8/28.
 */
public class CreatePHash {

    private int size = 32;
    private int smallerSize = 8;

    public void CreatPHash() {
        initCoefficients();
    }

    public void CreatPHash(int size, int smallerSize) {
        this.size = size;
        this.smallerSize = smallerSize;

        initCoefficients();
    }

    public String getHash(InputStream is) throws Exception {

        //讀取圖片
        is.reset();
        Bitmap img = BitmapFactory.decodeStream(is);

        //縮小圖片尺寸到32X32
        img = resize(img, size, size);

        //減少圖片色彩成灰白
        img = grayscale(img);

        double[][] vals = new double[size][size];

        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                vals[x][y] = getBlue(img, x, y);
            }
        }

        //計算DCT
        double[][] dctVals = applyDCT(vals);

        //計算灰度平均值
        double total = 0;

        for (int x = 0; x < smallerSize; x++) {
            for (int y = 0; y < smallerSize; y++) {
                total += dctVals[x][y];
            }
        }
        total -= dctVals[0][0];

        double avg = total / (double) ((smallerSize * smallerSize) - 1);

        //比較灰度平均值，大於平均值為1，小於平均值為0
        //產生hash code
        String hash = "";

        for (int x = 0; x < smallerSize; x++) {
            for (int y = 0; y < smallerSize; y++) {
                if (x != 0 && y != 0) {
                    hash += (dctVals[x][y] > avg ? "1" : "0");
                }
            }
        }
        return hash;
    }

    private Bitmap resize(Bitmap image, int width, int height) {

        return Bitmap.createScaledBitmap(image, width, height, false);
    }


    private Bitmap grayscale(Bitmap img) {

        int width = img.getWidth();            //獲取位圖的寬
        int height = img.getHeight();        //獲取位圖的高

        int[] pixels = new int[width * height];    //通過位圖的大小創建像素點數組

        img.getPixels(pixels, 0, width, 0, 0, width, height);
        int alpha = 0xFF << 24;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int grey = pixels[width * i + j];

                int red = ((grey & 0x00FF0000) >> 16);
                int green = ((grey & 0x0000FF00) >> 8);
                int blue = (grey & 0x000000FF);

                grey = (int) ((float) red * 0.3 + (float) green * 0.59 + (float) blue * 0.11);
                grey = alpha | (grey << 16) | (grey << 8) | grey;
                pixels[width * i + j] = grey;
            }
        }
        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        result.setPixels(pixels, 0, width, 0, 0, width, height);
        return result;

    }

    private static int getBlue(Bitmap img, int x, int y) {
        int pixel = img.getPixel(x, y);
        return Color.blue(pixel);
    }

    private double[] c;

    public void initCoefficients() {
        c = new double[size];

        for (int i = 1; i < size; i++) {
            c[i] = 1;
        }
        c[0] = 1 / Math.sqrt(2.0);

    }

    private double[][] applyDCT(double[][] f) {
        int N = size;

        double[][] F = new double[N][N];
        for (int u = 0; u < N - 1; u++) {
            for (int v = 0; v < N; v++) {
                double sum = 0.0;
                for (int i = 0; i < N; i++) {
                    for (int j = 0; j < N; j++) {
                        sum += Math.cos(((2 * i + 1) / (2.0 * N)) * u * Math.PI)
                                * Math.cos(((2 * j + 1) / (2.0 * N)) * v * Math.PI) * (f[i][j]);
                    }
                }
                sum *= ((c[u] * c[v]) / 4.0);
                F[u][v] = sum;
            }
        }
        return F;
    }
}
