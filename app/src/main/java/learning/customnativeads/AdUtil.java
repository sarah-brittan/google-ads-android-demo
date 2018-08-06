package learning.customnativeads;

import android.content.Context;

import com.google.android.gms.ads.AdSize;

import java.util.HashMap;
import java.util.Map;

public class AdUtil {
    private static Map<String, AdType> adTypes = new HashMap<>();


    public static AdType getAdType(Context context, String adName) {
        initAdTypes(context);
        return adTypes.get(adName);
    }

    private static void initAdTypes(Context context) {
        if (adTypes.isEmpty()) {
            String adName = "BANNER - 320x50";
            int width = 320;
            int height = 50;
            String adUnitId = context.getString(R.string.banner_demo_ad_unit_id);
            AdSize adSize = AdSize.BANNER;
            adTypes.put(adName, new AdType(width, height, adUnitId, adSize));

            adName = "FLUID";
            width = -1;
            height = -1;
            adUnitId = context.getString(R.string.fluid_demo_ad_unit_id);
            adSize = AdSize.FLUID;
            adTypes.put(adName, new AdType(width, height, adUnitId, adSize));

            adName = "FULL_BANNER - 468x60";
            width = -1;
            height = 60;
            adUnitId = context.getString(R.string.banner_ad_unit_id);
            adSize = AdSize.FULL_BANNER;
            adTypes.put(adName, new AdType(width, height, adUnitId, adSize));

            adName = "SMART_BANNER";
            width = -1;
            height = -1;
            adUnitId = context.getString(R.string.smart_banner_demo_ad_unit_id);
            adSize = AdSize.SMART_BANNER;
            adTypes.put(adName, new AdType(width, height, adUnitId, adSize));

            adName = "LARGE_BANNER - 320x100";
            width = 320;
            height = 100;
            adUnitId = context.getString(R.string.banner_demo_ad_unit_id);
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
            adUnitId = context.getString(R.string.banner_ad_unit_id);
            adSize = AdSize.MEDIUM_RECTANGLE;
            adTypes.put(adName, new AdType(width, height, adUnitId, adSize));

            adName = "WIDE_SKYSCRAPER - 160x600";
            width = 160;
            height = 600;
            adUnitId = "/wide_skyscraper";
            adSize = AdSize.WIDE_SKYSCRAPER;
            adTypes.put(adName, new AdType(width, height, adUnitId, adSize));

        }
    }


}