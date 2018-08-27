package learning.customnativeads;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import butterknife.BindView;
import butterknife.ButterKnife;
import learning.customnativeads.fragments.BannerSectionFragment;
import learning.customnativeads.fragments.CodeLabExampleFragment;
import learning.customnativeads.fragments.JupiterVersionFragment;
import learning.customnativeads.fragments.RecyclerViewFragment;
import learning.customnativeads.fragments.UnifiedNativeFragment;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigationView;

    boolean showTestAds = true;
    Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {

                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        if (currentFragment != null) transaction.remove(currentFragment);
                        switch (item.getItemId()) {
                            case R.id.bottom_bar_1:
                                currentFragment = new RecyclerViewFragment();
                                bottomNavigationView.getMenu().findItem(R.id.bottom_bar_1).setChecked(true);
                                break;
                            case R.id.bottom_bar_2:
                                currentFragment = new UnifiedNativeFragment();
                                bottomNavigationView.getMenu().findItem(R.id.bottom_bar_2).setChecked(true);
                                break;
                            case R.id.bottom_bar_3:
                                currentFragment = new BannerSectionFragment();
                                bottomNavigationView.getMenu().findItem(R.id.bottom_bar_3).setChecked(true);
                                break;
                            case R.id.bottom_bar_4:
                                currentFragment = new JupiterVersionFragment();
                                bottomNavigationView.getMenu().findItem(R.id.bottom_bar_4).setChecked(true);
                                break;
                            case R.id.bottom_bar_5:
                                currentFragment = new CodeLabExampleFragment();
                                bottomNavigationView.getMenu().findItem(R.id.bottom_bar_5).setChecked(true);
                                break;
                        }
                        if (currentFragment != null)
                            transaction.add(R.id.fragment_container, currentFragment);
                        transaction.commit();
                        return false;
                    }
                });

        bottomNavigationView.setSelectedItemId(R.id.bottom_bar_1);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //     refreshCurrentAd();
    }

    public BottomNavigationView getBottomNavigationView() {
        return bottomNavigationView;
    }

    public int getSelectedItemId() {
        return bottomNavigationView.getSelectedItemId();
    }

    public boolean shouldShowTestAds() {
        return showTestAds;
    }
}
