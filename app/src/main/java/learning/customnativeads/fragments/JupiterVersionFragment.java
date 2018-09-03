package learning.customnativeads.fragments;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.NativeAppInstallAd;
import com.google.android.gms.ads.formats.NativeAppInstallAdView;
import com.google.android.gms.ads.formats.NativeContentAd;
import com.google.android.gms.ads.formats.NativeContentAdView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.List;

import learning.customnativeads.ImageUtils;
import learning.customnativeads.R;

public class JupiterVersionFragment extends BaseAdsFragment {

    @Override
    protected void loadAds() {
        String adUnitId = getString(R.string.pan_staggered_ad_unit_id);
        Log.d("Ad-Unit", "Using Ad Unit " + adUnitId);


        AdLoader adLoader = new AdLoader.Builder(activity, adUnitId)
                .forAppInstallAd(new NativeAppInstallAd.OnAppInstallAdLoadedListener() {
                    @Override
                    public void onAppInstallAdLoaded(NativeAppInstallAd appInstallAd) {
                        displayPanameraStaggeredInstallAd(adContainer, appInstallAd);
                    }
                })
                .forContentAd(new NativeContentAd.OnContentAdLoadedListener() {
                    @Override
                    public void onContentAdLoaded(NativeContentAd contentAd) {
                        displayPanameraStaggeredContentAd(adContainer, contentAd);
                    }
                })
                .withAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(int errorCode) {
                        newAdButton.setEnabled(true);
                        Toast.makeText(activity, "Failed to load native ad: "
                                + errorCode, Toast.LENGTH_SHORT).show();
                    }
                })
                .build();

        PublisherAdRequest.Builder requestBuilder = new PublisherAdRequest.Builder();
        if (activity.shouldShowTestAds()) {
            requestBuilder.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
                    .addTestDevice("FD753978A2E3416E87E7AAEFD43C226E");
        }

        adLoader.loadAd(requestBuilder.build());
    }

    private void displayPanameraStaggeredContentAd(ViewGroup parent, NativeContentAd nativeAd) {
        NativeContentAdView adView = (NativeContentAdView) getLayoutInflater()
                .inflate(R.layout.list_item_content_ad_staggered, null);

        TextView mAdHeadline = adView.findViewById(R.id.ad_headline);
        MediaView mAdImage = adView.findViewById(R.id.ad_image);
        TextView mAdBody = adView.findViewById(R.id.ad_body);
        TextView mAdCTA = adView.findViewById(R.id.call_to_action);
        // ImageView mAdLogo = adView.findViewById(R.id.ad_logo);
        //TextView mAdRating = adView.findViewById(R.id.app_rating_text);
        TextView mAdAdvertiser = adView.findViewById(R.id.ad_advertiser);

        adView.setHeadlineView(mAdHeadline);
        adView.setMediaView(mAdImage);
        adView.setBodyView(mAdBody);
        adView.setCallToActionView(mAdCTA);
        // adView.setIconView(mAdLogo);

        mAdHeadline.setText(nativeAd.getHeadline());
        mAdBody.setText(nativeAd.getBody());
        mAdCTA.setVisibility(View.VISIBLE);
        mAdCTA.setText(nativeAd.getCallToAction());

        List<NativeAd.Image> images = nativeAd.getImages();

//        if (mAdImage != null && images != null && images.size() > 0) {
//            String url = images.get(0).getUri().toString();
//      //      ImageLoader.getInstance().displayImage(url, mAdImage, ImageUtils.getDisplayOptions());
//        }

        // assign native ad object to the native view and make visible
        adView.setNativeAd(nativeAd);

        parent.removeAllViews();
        parent.addView(adView);
    }

    private void displayPanameraStaggeredInstallAd(ViewGroup parent, final NativeAppInstallAd nativeAd) {

        NativeAppInstallAdView adView = (NativeAppInstallAdView) getLayoutInflater()
                .inflate(R.layout.list_item_app_installad_staggered, null);

        TextView mAdHeadline = adView.findViewById(R.id.ad_headline);
        MediaView mAdImage = adView.findViewById(R.id.ad_image);
        TextView mAdBody = adView.findViewById(R.id.ad_body);
        TextView mAdCTA = adView.findViewById(R.id.call_to_action);
        ImageView mAdLogo = adView.findViewById(R.id.ad_logo);
        TextView mAdRating = adView.findViewById(R.id.app_rating_text);
        TextView mAdAdvertiser = adView.findViewById(R.id.ad_advertiser);

        adView.setHeadlineView(mAdHeadline);
        adView.setMediaView(mAdImage);
        adView.setBodyView(mAdBody);
        adView.setCallToActionView(mAdCTA);
        adView.setIconView(mAdLogo);
        adView.setStarRatingView(mAdRating);

        mAdHeadline.setText(nativeAd.getHeadline());
        mAdBody.setText(nativeAd.getBody());
        mAdCTA.setVisibility(View.VISIBLE);
        mAdCTA.setText(nativeAd.getCallToAction());

        if (mAdRating != null)
            mAdRating.setText("Rating: " + nativeAd.getStarRating());

        //                                       List<NativeAd.Image> images = nativeAd.getImages();

//        if (mAdImage != null && images != null && images.size() > 0) {
//            String url = images.get(0).getUri().toString();
//       //     ImageLoader.getInstance().displayImage(url, mAdImage, ImageUtils.getDisplayOptions());
//        }

        NativeAd.Image logoImage = nativeAd.getIcon();
        if (mAdLogo != null && logoImage != null) {
            mAdLogo.setVisibility(View.VISIBLE);
            mAdLogo.setScaleType(ImageView.ScaleType.CENTER);
            String url = logoImage.getUri().toString();
            ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(activity));
            ImageLoader.getInstance().displayImage(url, mAdLogo, ImageUtils.getDisplayOptions());
        } else if(mAdLogo != null) {
            mAdLogo.setVisibility(View.GONE);

        }
        // assign native ad object to the native view and make visible
        adView.setNativeAd(nativeAd);

        parent.removeAllViews();
        parent.addView(adView);
    }
}
