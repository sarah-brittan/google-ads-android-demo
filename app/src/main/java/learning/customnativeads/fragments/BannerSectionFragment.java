package learning.customnativeads.fragments;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;

import learning.customnativeads.AdType;
import learning.customnativeads.AdUtil;
import learning.customnativeads.R;

public class BannerSectionFragment extends BaseAdsFragment {

    public BannerSectionFragment(){
        super();
    }

    @Override
    protected void loadAds() {
        adContainer.removeAllViews();

        View chooseAdLayout = getLayoutInflater()
                .inflate(R.layout.fragment_select_type, adContainer);

        final FrameLayout publisherAdView = chooseAdLayout.findViewById(R.id.ad_view_container);

        Spinner spinner = chooseAdLayout.findViewById(R.id.ad_type_elector_spinner);
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(activity,
                R.array.ad_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String adName = adapter.getItem(position).toString();
                displayAds(adName, publisherAdView);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(getActivity(), "Nothing selected", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayAds(String adName, final FrameLayout nativeAdContainer) {
        nativeAdContainer.removeAllViews();
        final AdType adType = AdUtil.getAdType(getActivity(), adName);

        PublisherAdView nativeAdView = new PublisherAdView(getActivity());
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
                Toast.makeText(activity, "Failed to load native ad: "
                        + adType.getAdUnitId(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                nativeAdContainer.setVisibility(View.VISIBLE);
            }
        });

        PublisherAdRequest.Builder requestBuilder = new PublisherAdRequest.Builder();
        if (activity.shouldShowTestAds()) {
            requestBuilder.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
                    .addTestDevice("FD753978A2E3416E87E7AAEFD43C226E");
        }

        nativeAdView.loadAd(requestBuilder.build());
    }


}
