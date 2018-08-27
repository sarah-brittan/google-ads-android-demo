package learning.customnativeads.fragments;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.formats.NativeCustomTemplateAd;

import learning.customnativeads.R;

public class CodeLabExampleFragment extends BaseAdsFragment {

    @Override
    protected void loadAds() {
        AdLoader adLoader;
        adLoader = new AdLoader.Builder(activity,
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
}
