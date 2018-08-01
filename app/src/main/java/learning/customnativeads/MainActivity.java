package learning.customnativeads;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.NativeCustomTemplateAd;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.newAdButton)
    Button newAdButton;

    @BindView(R.id.adView)
    FrameLayout adContainer;


    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.code_lab:
                                loadCodeLabAd();
                                bottomNavigationView.getMenu().findItem(R.id.code_lab).setChecked(true);
                                break;
                            case R.id.unified_simple:
                                loadSimpleUnifiedNativeAd();
                                bottomNavigationView.getMenu().findItem(R.id.unified_simple).setChecked(true);
                                break;
                            case R.id.unified_complex:
                                bottomNavigationView.getMenu().findItem(R.id.unified_complex).setChecked(true);
                                  loadComplexUnifiedAd();
                                break;
                        }
                        return false;
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshCurrentAd();
    }

    @OnClick(R.id.newAdButton)
    protected void refreshCurrentAd() {
        bottomNavigationView.setSelectedItemId(bottomNavigationView.getSelectedItemId());
    }

    // ======= CODE LAB NATIVE AD ========

    private void loadCodeLabAd() {
        AdLoader adLoader = new AdLoader.Builder(getApplicationContext(),
                getString(R.string.codelab_test_ad_unit_id))
                .forCustomTemplateAd(getString(R.string.native_template_id),
                        new NativeCustomTemplateAd.OnCustomTemplateAdLoadedListener() {
                            @Override
                            public void onCustomTemplateAdLoaded(NativeCustomTemplateAd ad) {
                                displayCodeLabCustomTemplateAd(adContainer, ad);
                            }
                        },
                        null)
                .build();

        adLoader.loadAd(new PublisherAdRequest.Builder()
                .build());
    }

    private void displayCodeLabCustomTemplateAd(FrameLayout parent, final NativeCustomTemplateAd ad) {

        View adView = getLayoutInflater()
                .inflate(R.layout.custom_template, null);

        // Show the custom template
        TextView headline = adView.findViewById(R.id.headline);
        TextView caption = adView.findViewById(R.id.caption);
        ImageView mainImage = adView.findViewById(R.id.mainImage);
        headline.setText(ad.getText("Headline"));
        caption.setText(ad.getText("Caption"));
        mainImage.setImageDrawable(ad.getImage("MainImage").getDrawable());

        // Record an impression
        ad.recordImpression();

        // Handle clicks on image
        mainImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ad.performClick("MainImage");
            }
        });

        parent.removeAllViews();
        parent.addView(adView);
    }


    // ======= SIMPLE UNIFIED NATIVE AD ========

    protected void loadSimpleUnifiedNativeAd() {
        AdLoader adLoader = new AdLoader.Builder(this,  getString(R.string.native_demo_ad_unit_id))
                .forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                    @Override
                    public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                        displaySimpleUnifiedAd(adContainer, unifiedNativeAd);
                    }
                })
                .withAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(int errorCode) {
                        // Handle the failure by logging, altering the UI, and so on.
                    }
                })
                .withNativeAdOptions(new NativeAdOptions.Builder()
                        // Methods in the NativeAdOptions.Builder class can be
                        // used here to specify individual options settings.
                        .build())
                .build();

        adLoader.loadAd(new PublisherAdRequest.Builder()
                .build());
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

    // ======= COMPLEX UNIFIED NATIVE AD ========

    protected void loadComplexUnifiedAd() {
        VideoOptions videoOptions = new VideoOptions.Builder()
              //  .setStartMuted(startVideoAdsMuted.isChecked())
                .build();

        AdLoader adLoader = new AdLoader.Builder(this, getString(R.string.native_demo_ad_unit_id))
                .forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                    @Override
                    public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                        displayComplexUnifiedAd(adContainer, unifiedNativeAd);
                    }
                })
                .withAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(int errorCode) {
                        newAdButton.setEnabled(true);
                        Toast.makeText(MainActivity.this, "Failed to load native ad: "
                                + errorCode, Toast.LENGTH_SHORT).show();
                    }
                })
                .withNativeAdOptions(new NativeAdOptions.Builder()
                        // Methods in the NativeAdOptions.Builder class can be
                        // used here to specify individual options settings.
                        .setVideoOptions(videoOptions)
                        .build())
                .build();

        adLoader.loadAd(new PublisherAdRequest.Builder().build());

       // videoStatus.setText("");
    }

    private void displayComplexUnifiedAd(ViewGroup parent, final UnifiedNativeAd nativeAd) {

        UnifiedNativeAdView adView = (UnifiedNativeAdView) getLayoutInflater()
                .inflate(R.layout.complex_unified_ad, null);

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
              //  videoStatus.setText("Video status: Video playback has ended.");
                super.onVideoEnd();
            }
        });

        MediaView mediaView = adView.findViewById(R.id.ad_media);
        ImageView mainImageView = adView.findViewById(R.id.ad_image);

        // Apps can check the VideoController's hasVideoContent property to determine if the
        // NativeAppInstallAd has a video asset.
        if (vc.hasVideoContent()) {
            adView.setMediaView(mediaView);
            mainImageView.setVisibility(View.GONE);
//            videoStatus.setText(String.format(Locale.getDefault(),
//                    "Video status: Ad contains a %.2f:1 video asset.",
//                    vc.getAspectRatio()));
        } else {
            adView.setImageView(mainImageView);
            mediaView.setVisibility(View.GONE);

            // At least one image is guaranteed.
            List<NativeAd.Image> images = nativeAd.getImages();
            mainImageView.setImageDrawable(images.get(0).getDrawable());

            newAdButton.setEnabled(true);
         //   videoStatus.setText("Video status: Ad does not contain a video asset.");
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
        ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(
                    nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

        adView.setNativeAd(nativeAd);

        parent.removeAllViews();
        parent.addView(adView);
    }
}
