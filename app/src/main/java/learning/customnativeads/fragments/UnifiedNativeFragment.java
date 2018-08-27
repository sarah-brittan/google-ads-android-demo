package learning.customnativeads.fragments;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;

import java.util.List;

import learning.customnativeads.R;

public class UnifiedNativeFragment extends BaseAdsFragment {

    protected void loadAds() {
        adContainer.removeAllViews();

        View chooseAdLayout = getLayoutInflater()
                .inflate(R.layout.fragment_select_type, adContainer);

        final FrameLayout adContainer = chooseAdLayout.findViewById(R.id.ad_view_container);

        Spinner spinner = chooseAdLayout.findViewById(R.id.ad_type_elector_spinner);
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(activity,
                R.array.display_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String displayType = adapter.getItem(position).toString();
                loadComplexUnifiedAd(displayType, adContainer);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(activity, "Nothing selected", Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected void loadComplexUnifiedAd(final String displayType, final FrameLayout adContainer) {
        String adUnitId = getString(R.string.pan_staggered_ad_unit_id);
        Log.d("Ad-Unit", "Using Ad Unit " + adUnitId);

        VideoOptions videoOptions = new VideoOptions.Builder()
                //  .setStartMuted(startVideoAdsMuted.isChecked())
                .build();

        AdLoader adLoader = new AdLoader.Builder(activity, adUnitId)
                .forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                    @Override
                    public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                        displayComplexUnifiedAd(displayType, adContainer, unifiedNativeAd);
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
                .withNativeAdOptions(new NativeAdOptions.Builder()
                        // Methods in the NativeAdOptions.Builder class can be
                        // used here to specify individual options settings.
                        .setVideoOptions(videoOptions)
                        .build())
                .build();

        PublisherAdRequest.Builder requestBuilder = new PublisherAdRequest.Builder();
        if (activity.shouldShowTestAds()) {
            requestBuilder.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
                    .addTestDevice("FD753978A2E3416E87E7AAEFD43C226E");
        }

        adLoader.loadAd(requestBuilder.build());
    }

    private void displayComplexUnifiedAd(String displayType, FrameLayout adContainer, final UnifiedNativeAd nativeAd) {
        if(!isAdded()){
            return;
        }
//        UnifiedNativeAdView adView = (UnifiedNativeAdView) getLayoutInflater()
//                .inflate(R.layout.native_staggered_unified_ad, null);

        int layout = R.layout.native_staggered_unified_ad;
        if (displayType.equals("Gallery")) layout = R.layout.native_gallery_unified_ad;
        else if (displayType.equals("List")) layout = R.layout.native_list_unified_ad;

        UnifiedNativeAdView adView = (UnifiedNativeAdView) getLayoutInflater()
                .inflate(layout, null);

        adContainer.removeAllViews();
        adContainer.addView(adView);

        // Get the video controller for the ad. One will always be provided, even if the ad doesn't
        // have a video asset.
        VideoController vc = nativeAd.getVideoController();

        // Create a new VideoLifecycleCallbacks object and pass it to the VideoController. The
        // VideoController will call methods on this object when events occur in the video
        // lifecycle.
        vc.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
            public void onVideoEnd() {
                // Publishers should allow native ads to complete video playback before refreshing
                // or replacing them with another ad in the same UI location.
                newAdButton.setEnabled(true);
                super.onVideoEnd();
            }
        });

        MediaView mediaView = adView.findViewById(R.id.ad_media);
        ImageView mainImageView = adView.findViewById(R.id.ad_image);

        // Apps can check the VideoController's hasVideoContent property to determine if the
        // NativeAppInstallAd has a video asset.
        if (vc.hasVideoContent()) {
            adView.setMediaView(mediaView);
            adView.setImageView(null);
            mainImageView.setVisibility(View.GONE);
        } else {
            adView.setImageView(mainImageView);
            adView.setMediaView(null);
            mediaView.setVisibility(View.GONE);

            // At least one image is guaranteed.
            List<NativeAd.Image> images = nativeAd.getImages();
            mainImageView.setImageDrawable(images.get(0).getDrawable());

            newAdButton.setEnabled(true);
        }

        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

        // Some assets are guaranteed to be in every UnifiedNativeAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
        ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        if (adView.getCallToActionView() != null)
            ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.getIcon() == null) {
            if (adView.getIconView() != null) adView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(
                    nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            if (adView.getPriceView() != null) adView.getPriceView().setVisibility(View.GONE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            if (adView.getStoreView() != null) adView.getStoreView().setVisibility(View.GONE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
            if (adView.getStarRatingView() != null)
                adView.getStarRatingView().setVisibility(View.GONE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            if (adView.getAdvertiserView() != null)
                adView.getAdvertiserView().setVisibility(View.GONE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

        adView.setNativeAd(nativeAd);

//        adContainer.removeAllViews();
//        adContainer.addView(adView);

        nativeAd.destroy();
    }

}
