/*
 * Copyright (C) 2017 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package learning.customnativeads.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.formats.UnifiedNativeAd;

import java.util.ArrayList;

import learning.customnativeads.MainActivity;
import learning.customnativeads.R;
import learning.customnativeads.RecyclerViewAdapter;

import static learning.customnativeads.LoadItemsService.addMenuItemsFromJson;

public class RecyclerViewFragment extends Fragment {

    // List of MenuItems that populate the RecyclerView.
    private ArrayList<Object> mRecyclerViewItems;

    public static final int NUM_OF_ADS = 3;
    public static final int DEFAULT_OFFSET = 5;
    public static final int VISIBLE_THRESHOLD = 5;
    private int adFetchedCount = 0;
    private int adAddedCount = 0;

    private AdLoader adLoader;

    private ArrayList<UnifiedNativeAd> nativeAds = new ArrayList<>();

    private MainActivity activity;

    private RecyclerViewAdapter adapter;

    private RecyclerView mRecyclerView;

    private LinearLayoutManager layoutManager;

    private boolean hasPreloadedAd = false;

    private int index = DEFAULT_OFFSET;

    public RecyclerViewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // retain this fragment
        setRetainInstance(true);

        activity = (MainActivity) getActivity();
        mRecyclerViewItems = addMenuItemsFromJson(activity);
        loadNativeAd();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_recycler_view, container, false);
        mRecyclerView = rootView.findViewById(R.id.recycler_view);

        // Use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView.
//        mRecyclerView.setHasFixedSize(true);

        // Specify a linear layout manager.
        layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                if (lastVisibleItem % VISIBLE_THRESHOLD == 0 && hasPreloadedAd && lastVisibleItem >= (DEFAULT_OFFSET * adFetchedCount)) {
                    insertNativeAdsIntoList();
                }
            }
        });

        // Specify an adapter.
        adapter = new RecyclerViewAdapter(activity, mRecyclerViewItems);
        mRecyclerView.setAdapter(adapter);

        return rootView;
    }

    private void loadNativeAd() {
        AdLoader.Builder builder = new AdLoader.Builder(activity, getListAdUnitId(activity));
        adLoader = builder
                .forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                    @Override
                    public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                        adFetchedCount++;
                        Log.d("Ads: ", "Ads fetched = " + adFetchedCount);
                        // A native ad loaded successfully, check if the ad loader has finished loading and if so, insert the ads into the list.
                        nativeAds.add(unifiedNativeAd);
                        if (!adLoader.isLoading()) {
                            hasPreloadedAd = true;
                            if (adAddedCount == 0) {
                                insertNativeAdsIntoList();
                            }
                        }
                    }
                })
                .withAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(int i) {
                        // A native ad failed to load, check if the ad loader has finished loading and if so, insert the ads into the list.
                        Log.e("MainActivity", "The previous native ad failed to load. Attempting to load another.");
                        if (!adLoader.isLoading()) {
                            hasPreloadedAd = false;
                        }
                    }
                })
                .build();

        AdRequest.Builder requestBuilder = new AdRequest.Builder();
        if (activity.shouldShowTestAds()) {
            requestBuilder.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
                    .addTestDevice("FD753978A2E3416E87E7AAEFD43C226E");
        }

        adLoader.loadAd(requestBuilder.build());
    }

    public static String getListAdUnitId(Context context) {
        return context.getResources().getString(R.string.pan_staggered_ad_unit_id);
    }

    private void insertNativeAdsIntoList() {
        if (nativeAds.size() <= 0) {
            return; //No ads to insert
        }

        for (UnifiedNativeAd ad : nativeAds) {
            adapter.insertObject(index, ad);
            index += DEFAULT_OFFSET;
        }

        adAddedCount++;
        Log.d("Ads: ", "Ads added = " + adAddedCount);

        hasPreloadedAd = false;
        loadNativeAd();
    }

}
