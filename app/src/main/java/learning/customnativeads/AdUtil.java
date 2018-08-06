package learning.customnativeads;

import com.google.android.gms.ads.AdSize;

import java.util.HashMap;
import java.util.Map;

public class AdUtil{
   private static Map<String, AdType> adTypes = new HashMap<>();

    public AdType getAdType(String adName){
        initAdTypes();
        return adTypes.get(adName);
    }

    private void initAdTypes(){
        if(adTypes.isEmpty()){
            String adName = "BANNER - 320x50";
            int width = 320;
            int height = 50;
            String adUnitId = "/6499/example/banner";
            AdSize adSize = AdSize.BANNER;
            adTypes.put(adName, new AdType(width, height, adUnitId, adSize));

            adName = "FULL_BANNER - 468x60";
            width = -1;
            height = 60;
            adUnitId = "/full_banner";
            adSize = AdSize.FULL_BANNER;
            adTypes.put(adName, new AdType(width, height, adUnitId, adSize));

            adName = "LARGE_BANNER - 320x100";
            width = 320;
            height = 100;
            adUnitId = "/large_banner";
            adSize = AdSize.LARGE_BANNER;
            adTypes.put(adName, new AdType(width, height, adUnitId, adSize));

            adName = "LEADERBOARD - 728x90";
            width = -1;
            height = 90;
            adUnitId = "/leaderboard";
            adSize = AdSize.LEADERBOARD;
            adTypes.put(adName, new AdType(width, height, adUnitId, adSize));

            adName = "MEDIUM_RECTANGLE - 300x250";
            width = 300;
            height = 250;
            adUnitId = "/medium_rectangle";
            adSize = AdSize.MEDIUM_RECTANGLE;
            adTypes.put(adName, new AdType(width, height, adUnitId, adSize));

            adName = "WIDE_SKYSCRAPER - 160x600";
            width = 160;
            height = 600;
            adUnitId = "/wide_skyscraper";
            adSize = AdSize.WIDE_SKYSCRAPER;
            adTypes.put(adName, new AdType(width, height, adUnitId, adSize));

            adName = "SMART_BANNER";
            width = -1;
            height = -1;
            adUnitId = "ca-app-pub-3940256099942544/6300978111";
            adSize = AdSize.SMART_BANNER;
            adTypes.put(adName, new AdType(width, height, adUnitId, adSize));

            adName = "FLUID";
            width = -1;
            height = -1;
            adUnitId = "/6499/example/apidemo/fluid";
            adSize = AdSize.FLUID;
            adTypes.put(adName, new AdType(width, height, adUnitId, adSize));
        }
    }


}