package learning.customnativeads.fragments;

import android.util.Log;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;

import learning.customnativeads.R;

public class SimpleUnifiedFragment extends BaseAdsFragment {

    public SimpleUnifiedFragment(){
        super();
    }

    @Override
    protected void loadAds() {
        String adUnitId = getString(R.string.native_demo_ad_unit_id);
        Log.d("Ad-Unit", "Using Ad Unit " + adUnitId);
        AdLoader adLoader = new AdLoader.Builder(activity, adUnitId)
                .forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                    @Override
                    public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {

                        displaySimpleUnifiedAd(adContainer, unifiedNativeAd);
                    }
                })
                .withAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(int errorCode) {
                        Toast.makeText(activity, "Failed to load native ad: "
                                + errorCode, Toast.LENGTH_SHORT).show();
                    }
                })
                .withNativeAdOptions(new NativeAdOptions.Builder()
                        // Methods in the NativeAdOptions.Builder class can be
                        // used here to specify individual options settings.
                        .build())
                .build();

        PublisherAdRequest.Builder requestBuilder = new PublisherAdRequest.Builder();
        if (activity.shouldShowTestAds()) {
            requestBuilder.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
                    .addTestDevice("FD753978A2E3416E87E7AAEFD43C226E");
        }

        adLoader.loadAd(requestBuilder.build());
    }

    private void displaySimpleUnifiedAd(ViewGroup parent, final UnifiedNativeAd unifiedNativeAd) {
        // Assumes that your ad layout is in a file call ad_unified.xml
        // in the res/layout folder
        UnifiedNativeAdView adView = (UnifiedNativeAdView) getLayoutInflater()
                .inflate(R.layout.simple_unified_ad, null);

        TextView headline = adView.findViewById(R.id.headline);
        headline.setText(unifiedNativeAd.getHeadline());
        TextView body = adView.findViewById(R.id.body);
        body.setText(unifiedNativeAd.getBody());
        MediaView mediaView = adView.findViewById(R.id.mediaView);

        adView.setHeadlineView(headline);
        adView.setBodyView(body);
        adView.setMediaView(mediaView);

        adView.setNativeAd(unifiedNativeAd);

        parent.removeAllViews();
        parent.addView(adView);
    }



}
