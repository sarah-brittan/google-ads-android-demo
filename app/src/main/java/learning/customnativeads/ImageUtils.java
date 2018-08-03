package learning.customnativeads;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

/**
 * Created by OLX - Ankur on 24-03-2017.
 */

public class ImageUtils {
    public static DisplayImageOptions getDisplayOptions() {
        return new DisplayImageOptions.Builder()
                .displayer(new FadeInBitmapDisplayer(800))
                .cacheOnDisk(true)
                .resetViewBeforeLoading(true)
                .showImageOnLoading(R.drawable.default_product)
                .showImageOnFail(R.drawable.default_product)
                .build();
    }
}
