package com.youwu.shopowner;



import com.cretin.www.cretinautoupdatelibrary.model.DownloadInfo;
import com.cretin.www.cretinautoupdatelibrary.model.TypeConfig;
import com.cretin.www.cretinautoupdatelibrary.utils.AppUpdateUtils;
import com.youwu.shopowner.bean.UpDateBean;


/**
 * 检查更新
 */
public class UpData {
    public static void UpData(UpDateBean upDateBean){
        DownloadInfo info = new DownloadInfo().setApkUrl(upDateBean.getDownload_url())
                .setProdVersionName(upDateBean.getVersion())//版本号
                .setForceUpdateFlag(upDateBean.getUpdate_status())    //是否强制更新 0 不强制更新 1 hasAffectCodes拥有字段强制更新 2 所有版本强制更新
                .setUpdateLog(upDateBean.getContent());//更新内容
        AppUpdateUtils.getInstance().getUpdateConfig().setUiThemeType(TypeConfig.UI_THEME_L);//选择UI风格
        //打开文件MD5校验
        AppUpdateUtils.getInstance().getUpdateConfig().setNeedFileMD5Check(false);

        //开启或者关闭后台静默下载功能
        AppUpdateUtils.getInstance().getUpdateConfig().setAutoDownloadBackground(false);
        if (false) {
            //开启静默下载的时候推荐关闭通知栏进度提示
            AppUpdateUtils.getInstance().getUpdateConfig().setShowNotification(false);
        } else {
            AppUpdateUtils.getInstance().getUpdateConfig().setShowNotification(true);
        }
        //因为这里打开了MD5的校验 我在这里添加一个MD5检验监听监听
        AppUpdateUtils.getInstance()
                .checkUpdate(info);
    }
}
