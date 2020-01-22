package com.habapps;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.pay.android.googlebilling.PurchaseManagerGoogleBilling;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.habapps.service.SkelGameAppInfoServiceImpl;

import libgdx.game.Game;

public class AndroidLauncher extends AndroidApplication {

    public static final int ID_AD_BANNER = 1111;

    private SkelGameAppInfoServiceImpl appInfoService;
    private SkelGame game;
    private AdView bannerAdview;

    private InterstitialAd interstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initServices();
        setWindowAttrs();
        createLayout();
    }

    private void createLayout() {
        LinearLayout allScreenView = new LinearLayout(this);
        allScreenView.setOrientation(LinearLayout.VERTICAL);
        int libgdxAdviewHeight = getResources().getDimensionPixelOffset(R.dimen.libgdx_adview_height);
        ViewGroup.LayoutParams adParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, libgdxAdviewHeight);
        View gameView = createGameView();
        bannerAdview = new AdView(this);
        if (!Utils.isValidExtraContent()) {
            allScreenView.addView(bannerAdview, adParams);
        }
        allScreenView.addView(gameView);
        setContentView(allScreenView);
        initAds(bannerAdview);
    }

    public void removeAds() {
        if (bannerAdview != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((ViewGroup) bannerAdview.getParent()).removeView(bannerAdview);
                    bannerAdview = null;
                }
            });
        }
    }

    private void initServices() {
        handler = new Handler(getMainLooper());
        appInfoService = new SkelGameAppInfoServiceImpl(this);
    }

    private void initAds(AdView bannerAdview) {
        MobileAds.initialize(this, getResources().getString(R.string.admob_app_id));

        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getResources().getString(R.string.admob_inter_id));
        interstitialAd.loadAd(new AdRequest.Builder().build());
        bannerAdview.setId(ID_AD_BANNER);
        bannerAdview.setAdSize(AdSize.BANNER);
        bannerAdview.setBackgroundColor(Color.parseColor(Game.getInstance().getSubGameDependencyManager().getScreenBackgroundColor().toHexadecimal()));
        bannerAdview.setAdUnitId(getResources().getString(R.string.admob_banner_id));
        bannerAdview.loadAd(new AdRequest.Builder().build());
    }


    private View createGameView() {
        game = new SkelGame(appInfoService);
        game.purchaseManager = new PurchaseManagerGoogleBilling(this);
        return initializeForView(
                //////////////////////////
                //////////////////////////
                //////////////////////////
                //////////////////////////
                //////////////////////////
                game,
                //////////////////////////
                //////////////////////////
                //////////////////////////
                //////////////////////////
                //////////////////////////
                new AndroidApplicationConfiguration());
    }

    private void setWindowAttrs() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
    }


    public void showPopupAd(final Runnable afterClose) {
        if (!Utils.isValidExtraContent()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (interstitialAd.isLoaded()) {
                        interstitialAd.show();
                        interstitialAd.setAdListener(new AdListener() {
                            @Override
                            public void onAdClosed() {
                                afterClose.run();
                                interstitialAd.loadAd(new AdRequest.Builder().build());
                            }
                        });
                    } else {
                        afterClose.run();
                    }
                }
            });
        } else {
            afterClose.run();
        }
    }

}