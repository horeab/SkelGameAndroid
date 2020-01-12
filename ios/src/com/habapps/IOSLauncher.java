package com.habapps;

import com.badlogic.gdx.backends.iosrobovm.IOSApplication;
import com.badlogic.gdx.backends.iosrobovm.IOSApplicationConfiguration;
import com.habapps.skelgame.SkelGameAppInfoServiceImpl;

import org.apache.xml.serializer.utils.Utils;
import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.foundation.Foundation;
import org.robovm.apple.foundation.NSAutoreleasePool;
import org.robovm.apple.foundation.NSErrorException;
import org.robovm.apple.uikit.UIApplication;
import org.robovm.apple.uikit.UIApplicationLaunchOptions;
import org.robovm.apple.uikit.UIView;
import org.robovm.pods.google.GGLContext;
import org.robovm.pods.google.mobileads.GADAdSize;
import org.robovm.pods.google.mobileads.GADBannerView;
import org.robovm.pods.google.mobileads.GADBannerViewDelegateAdapter;
import org.robovm.pods.google.mobileads.GADInterstitial;
import org.robovm.pods.google.mobileads.GADInterstitialDelegateAdapter;
import org.robovm.pods.google.mobileads.GADRequest;
import org.robovm.pods.google.mobileads.GADRequestError;

public class IOSLauncher extends IOSApplication.Delegate {


    private boolean adsInitialized = false;

    private GameProperties gameProperties = GameProperties.skelgame;
    private SkelGame game;

    private GADBannerView bannerAdview;
    private GADInterstitial interstitialAd;

    private IOSApplication iosApplication;

    private SkelGameAppInfoServiceImpl appInfoService;

    @Override
    protected IOSApplication createApplication() {
        final IOSApplicationConfiguration config = new IOSApplicationConfiguration();
        appInfoService = new SkelGameAppInfoServiceImpl(this);
        config.orientationLandscape = !appInfoService.isPortraitMode();
        config.orientationPortrait = appInfoService.isPortraitMode();
        game = new SkelGame(appInfoService);
        game.purchaseManager = new PurchaseManageriOSApple();
        iosApplication = new IOSApplication(
                game,
                config);
        return iosApplication;
    }

    public float getBannerAdHeight() {
        return (float) bannerAdview.getBounds().getSize().getHeight() * (float) UIApplication.getSharedApplication().getKeyWindow().getRootViewController().getView().getContentScaleFactor();
    }

    public GameProperties getGameProperties() {
        return gameProperties;
    }

    public float getSafeAreaInsets() {
        double topMargin = 0;
        if (Foundation.getMajorSystemVersion() >= 11) {
            UIView view = UIApplication.getSharedApplication().getKeyWindow().getRootViewController().getView();
            topMargin = view.getSafeAreaInsets().getBottom();
        }
        return Math.abs((float) topMargin);
    }

    @Override
    public boolean didFinishLaunching(UIApplication application, UIApplicationLaunchOptions launchOptions) {
        boolean finishLaunching = super.didFinishLaunching(application, launchOptions);

        if (!appInfoService.isScreenShotMode() && !Utils.isValidExtraContent()) {
            initializeAds(iosApplication);
        }
        return finishLaunching;
    }

    public static void main(String[] argv) {
        NSAutoreleasePool pool = new NSAutoreleasePool();
        UIApplication.main(argv, null, IOSLauncher.class);
        pool.close();
    }

    public void removeAds() {
        bannerAdview.setFrame(new CGRect(0, 0, 0, 0));
        bannerAdview.setDelegate(null);
    }

    private void initializeAds(IOSApplication iosApplication) {
        if (!adsInitialized) {
            adsInitialized = true;
            try {
                GGLContext.getSharedInstance().configure();
            } catch (NSErrorException e) {
                System.err.println("Error configuring the Google context: " + e.getError());
            }
            bannerAdview = new GADBannerView(GADAdSize.SmartBannerPortrait());

            bannerAdview.setAdUnitID(gameProperties.getBannerAdId());
            bannerAdview.setRootViewController(iosApplication.getUIViewController());
            iosApplication.getUIViewController().getView().addSubview(bannerAdview);
            bannerAdview.setFrame(new CGRect(
                    bannerAdview.getBounds().getX(),
                    bannerAdview.getBounds().getY() + getSafeAreaInsets(),
                    bannerAdview.getBounds().getSize().getWidth(),
                    bannerAdview.getBounds().getSize().getHeight()));

            bannerAdview.setDelegate(new GADBannerViewDelegateAdapter() {
                @Override
                public void didReceiveAd(GADBannerView view) {
                    super.didReceiveAd(view);
                }

                @Override
                public void didFailToReceiveAd(GADBannerView view,
                                               GADRequestError error) {
                    super.didFailToReceiveAd(view, error);
                }
            });

            bannerAdview.loadRequest(createRequest());

            interstitialAd = createAndLoadInterstitial();

        }
    }

    private GADInterstitial createAndLoadInterstitial() {
        interstitialAd = new GADInterstitial(gameProperties.getInterstitialAdId());
        final IOSLauncher launcher = this;
        interstitialAd.setDelegate(new GADInterstitialDelegateAdapter() {
            @Override
            public void didDismissScreen(GADInterstitial ad) {
                launcher.interstitialAd = createAndLoadInterstitial();
            }
        });
        interstitialAd.loadRequest(createRequest());
        return interstitialAd;
    }

    public void showPopupAd(Runnable afterClose) {
        if (!appInfoService.isScreenShotMode() && !Utils.isValidExtraContent()) {
            if (interstitialAd.isReady()) {
                interstitialAd.present(UIApplication.getSharedApplication().getKeyWindow().getRootViewController());
            } else {
                interstitialAd.loadRequest(createRequest());
            }
        }
        afterClose.run();
    }

    private GADRequest createRequest() {
        GADRequest request = new GADRequest();
        return request;
    }
}