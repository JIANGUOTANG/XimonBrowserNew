package jian.com.ximon.ad.ad;

import jian.com.ximon.util.Constant;

/**
 * Created by ying on 17-11-13.
 */

public class AdPresenter {
    private AdView adView;
    private AdModel adModel;
    private int machine_id;
    public AdPresenter(AdActivity context, AdView adView, int machine_id) {
        this.adView = adView;
        this.adModel = new AdModel(context);
        this.machine_id = machine_id;
    }
    public void loadMachine(){
        adView.startLoading();
        adModel.loadAD("/index/file/"+machine_id);
    }

    /**
     *
     * @param adId 广告ID
     */
    public void deleteAd(int adId){
        adModel.deleteAD(Constant.DELETE_AD+adId);
    }

    /**
     * 推送广告
     * @param machine_id
     */
    public void sendAd(int machine_id){
        adModel.SendAD(Constant.PUSH_AD,machine_id);
    }

}
