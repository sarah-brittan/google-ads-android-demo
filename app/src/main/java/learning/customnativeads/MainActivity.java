package learning.customnativeads;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.android.gms.ads.doubleclick.PublisherAdView;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.NativeAppInstallAd;
import com.google.android.gms.ads.formats.NativeAppInstallAdView;
import com.google.android.gms.ads.formats.NativeContentAd;
import com.google.android.gms.ads.formats.NativeContentAdView;
import com.google.android.gms.ads.formats.NativeCustomTemplateAd;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

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

    boolean showTestAds = true;

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
//                            case R.id.unified_simple:
//                                loadSimpleUnifiedNativeAd();
//                                bottomNavigationView.getMenu().findItem(R.id.unified_simple).setChecked(true);
//                                break;
                            case R.id.choose_ad:
                                bottomNavigationView.getMenu().findItem(R.id.choose_ad).setChecked(true);
                                loadChooseAd();
                                break;
                            case R.id.unified_complex:
                                bottomNavigationView.getMenu().findItem(R.id.unified_complex).setChecked(true);
                                setupPanameraChooser();
                                break;
                            case R.id.pan_listing:
                                bottomNavigationView.getMenu().findItem(R.id.pan_listing).setChecked(true);
                                loadPanameraStaggeredInstallAd();
                                break;
                        }
                        return false;
                    }
                });

        bottomNavigationView.setSelectedItemId(R.id.unified_complex);
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

    // ======= FLUID AD ========

    private void loadChooseAd() {
        adContainer.removeAllViews();

        View chooseAdLayout = getLayoutInflater()
                .inflate(R.layout.fragment_select_type, adContainer);

        final FrameLayout publisherAdView = chooseAdLayout.findViewById(R.id.ad_view_container);

        Spinner spinner = chooseAdLayout.findViewById(R.id.ad_type_elector_spinner);
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.ad_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String adName = adapter.getItem(position).toString();
                displayChosenAd(adName, publisherAdView);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(MainActivity.this, "Nothing selected", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayChosenAd(String adName, final FrameLayout nativeAdContainer) {

        nativeAdContainer.removeAllViews();
        final AdType adType = AdUtil.getAdType(this, adName);

        PublisherAdView nativeAdView = new PublisherAdView(this);
        nativeAdView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        nativeAdView.setAdUnitId(adType.getAdUnitId());
        nativeAdView.setAdSizes(adType.getAdSize());
        nativeAdContainer.addView(nativeAdView);

        nativeAdView.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
                super.onAdFailedToLoad(errorCode);
                nativeAdContainer.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, "Failed to load native ad: "
                        + adType.getAdUnitId(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                nativeAdContainer.setVisibility(View.VISIBLE);
            }
        });

        PublisherAdRequest.Builder requestBuilder = new PublisherAdRequest.Builder();
        if (showTestAds) {
            requestBuilder.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
                    .addTestDevice("FD753978A2E3416E87E7AAEFD43C226E");
        }

        nativeAdView.loadAd(requestBuilder.build());
    }


    // ======= CODE LAB NATIVE AD ========

    private void loadCodeLabAd() {
        AdLoader adLoader = new AdLoader.Builder(getApplicationContext(),
                getString(R.string.codelab_demo_custom_ad_unit_id))
                .forCustomTemplateAd(getString(R.string.codelab_template_id),
                        new NativeCustomTemplateAd.OnCustomTemplateAdLoadedListener() {
                            @Override
                            public void onCustomTemplateAdLoaded(NativeCustomTemplateAd ad) {
                                displayCodeLabCustomTemplateAd(adContainer, ad);
                            }
                        },
                        null)
                .withAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(int errorCode) {
                        Toast.makeText(MainActivity.this, "Failed to load native ad: "
                                + errorCode, Toast.LENGTH_SHORT).show();
                    }
                })
                .build();

        PublisherAdRequest.Builder requestBuilder = new PublisherAdRequest.Builder();
        if (showTestAds) {
            requestBuilder.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
                    .addTestDevice("FD753978A2E3416E87E7AAEFD43C226E");
        }

        adLoader.loadAd(requestBuilder.build());
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
        String adUnitId = getString(R.string.native_demo_ad_unit_id);
        Log.d("Ad-Unit", "Using Ad Unit " + adUnitId);
        AdLoader adLoader = new AdLoader.Builder(this, adUnitId)
                .forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                    @Override
                    public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {

                        displaySimpleUnifiedAd(adContainer, unifiedNativeAd);
                    }
                })
                .withAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(int errorCode) {
                        Toast.makeText(MainActivity.this, "Failed to load native ad: "
                                + errorCode, Toast.LENGTH_SHORT).show();
                    }
                })
                .withNativeAdOptions(new NativeAdOptions.Builder()
                        // Methods in the NativeAdOptions.Builder class can be
                        // used here to specify individual options settings.
                        .build())
                .build();

        PublisherAdRequest.Builder requestBuilder = new PublisherAdRequest.Builder();
        if (showTestAds) {
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

    // ======= COMPLEX UNIFIED NATIVE AD ========

    protected void setupPanameraChooser() {
        adContainer.removeAllViews();

        View chooseAdLayout = getLayoutInflater()
                .inflate(R.layout.fragment_select_type, adContainer);

        final FrameLayout adContainer = chooseAdLayout.findViewById(R.id.ad_view_container);

        Spinner spinner = chooseAdLayout.findViewById(R.id.ad_type_elector_spinner);
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
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
                Toast.makeText(MainActivity.this, "Nothing selected", Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected void loadComplexUnifiedAd(final String displayType, final FrameLayout adContainer) {
        String adUnitId = getString(R.string.native_backfill_demo_ad_unit_id);
        Log.d("Ad-Unit", "Using Ad Unit " + adUnitId);

        VideoOptions videoOptions = new VideoOptions.Builder()
                //  .setStartMuted(startVideoAdsMuted.isChecked())
                .build();

        AdLoader adLoader = new AdLoader.Builder(this, adUnitId)
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

        PublisherAdRequest.Builder requestBuilder = new PublisherAdRequest.Builder();
        if (showTestAds) {
            requestBuilder.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
                    .addTestDevice("FD753978A2E3416E87E7AAEFD43C226E");
        }

        adLoader.loadAd(requestBuilder.build());
    }

    private void displayComplexUnifiedAd(String displayType, FrameLayout adContainer, final UnifiedNativeAd nativeAd) {
//        UnifiedNativeAdView adView = (UnifiedNativeAdView) getLayoutInflater()
//                .inflate(R.layout.pan_staggered_unified_ad, null);

        int layout = R.layout.pan_staggered_unified_ad;
        if (displayType.equals("Gallery")) layout = R.layout.pan_gallery_unified_ad;
        else if (displayType.equals("List")) layout = R.layout.pan_list_unified_ad;

        UnifiedNativeAdView adView = (UnifiedNativeAdView) getLayoutInflater()
                .inflate(layout, null);

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

        adContainer.removeAllViews();
        adContainer.addView(adView);

        nativeAd.destroy();
    }

    // ========= PAN LISTING / STAGGERED ==========

    private void loadPanameraStaggeredInstallAd() {
        String adUnitId = getString(R.string.pan_staggered_ad_unit_id);
        Log.d("Ad-Unit", "Using Ad Unit " + adUnitId);


        AdLoader adLoader = new AdLoader.Builder(getApplicationContext(), adUnitId)
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
                        Toast.makeText(MainActivity.this, "Failed to load native ad: "
                                + errorCode, Toast.LENGTH_SHORT).show();
                    }
                })
                .build();

        PublisherAdRequest.Builder requestBuilder = new PublisherAdRequest.Builder();
        if (showTestAds) {
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
            ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(this));
            ImageLoader.getInstance().displayImage(url, mAdLogo, ImageUtils.getDisplayOptions());
        } else {
            mAdLogo.setVisibility(View.GONE);

        }
        // assign native ad object to the native view and make visible
        adView.setNativeAd(nativeAd);

        parent.removeAllViews();
        parent.addView(adView);
    }
}
