package learning.customnativeads;

import com.google.android.gms.ads.AdSize;

public class AdType {
    private int width;
    private int height;
    private String adUnitId;
    private AdSize adSize;

    public AdType(int width, int height, String adUnitId, AdSize adSize) {
        this.width = width;
        this.height = height;
        this.adUnitId = adUnitId;
        this.adSize = adSize;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getAdUnitId() {
        return adUnitId;
    }

    public AdSize getAdSize() {
        return adSize;
    }
}
