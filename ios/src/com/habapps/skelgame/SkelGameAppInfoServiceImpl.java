package com.habapps.skelgame;


import com.habapps.IOSLauncher;

import libgdx.game.external.AppInfoService;

public class SkelGameAppInfoServiceImpl implements AppInfoService {

    private IOSLauncher iosLauncher;

    public SkelGameAppInfoServiceImpl(IOSLauncher iosLauncher) {
        this.iosLauncher = iosLauncher;
    }
    @Override
    public boolean isPortraitMode() {
        return true;
    }


    @Override
    public String getProVersionStoreAppId() {
        return null;
    }

    @Override
    public String getGameIdPrefix() {
        return iosLauncher.getGameProperties().getGameIdEnum().name();
    }

    @Override
    public boolean isProVersion() {
        return false;
    }

    @Override
    public String getAppName() {
        return iosLauncher.getGameProperties().getAppName();
    }

    @Override
    public String getStoreAppId() {
        return iosLauncher.getGameProperties().getStoreAppId();
    }

    @Override
    public void showPopupAd(Runnable afterClose) {
        iosLauncher.showPopupAd(afterClose);
    }

    @Override
    public void showRewardedVideoAd() {
//        activity.showRewardedVideoAd();
    }

    @Override
    public String getLanguage() {
        return iosLauncher.getGameProperties().getLanguage();
    }

    @Override
    public boolean isScreenShotMode() {
        return false;
    }

    @Override
    public boolean googleFacebookLoginEnabled() {
        return false;
    }

    @Override
    public String getImplementationGameResourcesFolder() {
        return "";
    }

    @Override
    public String getResourcesFolder() {
        return "";
    }

    @Override
    public String getMainResourcesFolder() {
        return "";
    }

    @Override
    public float gameScreenTopMargin() {
        if (isScreenShotMode() || isProVersion()) {
            return 0;
        }
        return iosLauncher.getSafeAreaInsets() + iosLauncher.getBannerAdHeight();
    }
}
