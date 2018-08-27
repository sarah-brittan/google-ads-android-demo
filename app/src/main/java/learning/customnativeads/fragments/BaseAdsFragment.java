package learning.customnativeads.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;
import learning.customnativeads.MainActivity;
import learning.customnativeads.R;

public abstract class BaseAdsFragment extends Fragment {

    @BindView(R.id.newAdButton)
    Button newAdButton;

    @BindView(R.id.adView)
    FrameLayout adContainer;

    MainActivity activity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.default_ad_fragment, container, false);
        ButterKnife.bind(this, view);
        activity = ((MainActivity) getActivity());
        loadAds();
        return view;
    }

    @Optional
    @OnClick(R.id.newAdButton)
    protected void refreshCurrentAd() {
        activity.getBottomNavigationView().setSelectedItemId(activity.getSelectedItemId());
    }

    protected abstract void loadAds();

}
