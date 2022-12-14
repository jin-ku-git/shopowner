package com.cretin.www.cretinautoupdatelibrary.utils;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.text.TextUtils;

import com.cretin.www.cretinautoupdatelibrary.activity.UpdateBackgroundActivity;
import com.cretin.www.cretinautoupdatelibrary.activity.UpdateType10Activity;
import com.cretin.www.cretinautoupdatelibrary.activity.UpdateType11Activity;
import com.cretin.www.cretinautoupdatelibrary.activity.UpdateType12Activity;
import com.cretin.www.cretinautoupdatelibrary.activity.UpdateType1Activity;
import com.cretin.www.cretinautoupdatelibrary.activity.UpdateType2Activity;
import com.cretin.www.cretinautoupdatelibrary.activity.UpdateType3Activity;
import com.cretin.www.cretinautoupdatelibrary.activity.UpdateType4Activity;
import com.cretin.www.cretinautoupdatelibrary.activity.UpdateType5Activity;
import com.cretin.www.cretinautoupdatelibrary.activity.UpdateType6Activity;
import com.cretin.www.cretinautoupdatelibrary.activity.UpdateType7Activity;
import com.cretin.www.cretinautoupdatelibrary.activity.UpdateType8Activity;
import com.cretin.www.cretinautoupdatelibrary.activity.UpdateType9Activity;
import com.cretin.www.cretinautoupdatelibrary.interfaces.AppDownloadListener;
import com.cretin.www.cretinautoupdatelibrary.interfaces.AppUpdateInfoListener;
import com.cretin.www.cretinautoupdatelibrary.interfaces.MD5CheckListener;
import com.cretin.www.cretinautoupdatelibrary.model.DownloadInfo;
import com.cretin.www.cretinautoupdatelibrary.model.LibraryUpdateEntity;
import com.cretin.www.cretinautoupdatelibrary.model.TypeConfig;
import com.cretin.www.cretinautoupdatelibrary.model.UpdateConfig;
import com.cretin.www.cretinautoupdatelibrary.net.HttpCallbackModelListener;
import com.cretin.www.cretinautoupdatelibrary.net.HttpUtils;
import com.cretin.www.cretinautoupdatelibrary.service.UpdateReceiver;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadLargeFileListener;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.connection.FileDownloadUrlConnection;
import com.liulishuo.filedownloader.util.FileDownloadHelper;
import com.liulishuo.filedownloader.util.FileDownloadUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.cretin.www.cretinautoupdatelibrary.utils.AppUtils.getAppLocalPath;

/**
 * @date: on 2019-10-09
 * @author: a112233
 * @email: mxnzp_life@163.com
 * @desc: ????????????
 */
public class AppUpdateUtils {

    private static Application mContext;
    private static AppUpdateUtils updateUtils;
    private static UpdateConfig updateConfig;
    //???????????????
    private static boolean isInit;

    //????????????
    private BaseDownloadTask downloadTask;

    //??????????????????
    private static boolean isDownloading = false;

    //????????????????????????
    private DownloadInfo downloadInfo;

    //apk???????????????
    private static String downloadUpdateApkFilePath = "";

    //AppDownloadListener?????????
    private static List<AppDownloadListener> appDownloadListenerList;

    //MD5????????????
    private static List<MD5CheckListener> md5CheckListenerList;

    //??????????????????
    private static List<AppUpdateInfoListener> appUpdateInfoListenerList;

    //?????????????????????
    private AppUpdateUtils() {
        appDownloadListenerList = new ArrayList<>();
        md5CheckListenerList = new ArrayList<>();
        appUpdateInfoListenerList = new ArrayList<>();
    }

    /**
     * ???????????????
     *
     * @param context
     * @param config
     */
    public static void init(Application context, UpdateConfig config) {
        isInit = true;
        mContext = context;
        updateConfig = config;
        ResUtils.init(context);

        FileDownloadHelper.ConnectionCreator fileDownloadConnection = null;
        //????????????????????????
        if (updateConfig != null && updateConfig.getCustomDownloadConnectionCreator() != null) {
            fileDownloadConnection = updateConfig.getCustomDownloadConnectionCreator();
        } else {
            fileDownloadConnection = new FileDownloadUrlConnection
                    .Creator(new FileDownloadUrlConnection.Configuration()
                    .connectTimeout(30_000) // set connection timeout.
                    .readTimeout(30_000) // set read timeout.
            );
        }

        FileDownloader.setupOnApplicationOnCreate(mContext)
                .connectionCreator(fileDownloadConnection)
                .commit();
    }

    public static AppUpdateUtils getInstance() {
        if (updateUtils == null) {
            updateUtils = new AppUpdateUtils();
        }
        return updateUtils;
    }

    /**
     * ???????????? sdk??????????????????
     */
    public void checkUpdate() {
        checkInit();

        UpdateConfig updateConfig = getUpdateConfig();

        if (updateConfig.getDataSourceType() != TypeConfig.DATA_SOURCE_TYPE_URL) {
            LogUtils.log("?????? DATA_SOURCE_TYPE_URL ???????????????????????????????????????UpdateConfig??????dataSourceType???????????? DATA_SOURCE_TYPE_URL ");
            return;
        }

        if (TextUtils.isEmpty(updateConfig.getBaseUrl())) {
            LogUtils.log("?????? DATA_SOURCE_TYPE_URL ???????????????????????????????????????UpdateConfig??????baseUrl???????????????????????????");
            return;
        }

        getData();
    }

    /**
     * ???????????? sdk????????????json???modelClass ??????????????????
     *
     * @param jsonData
     */
    public void checkUpdate(String jsonData) {
        if (TextUtils.isEmpty(jsonData)) {
            return;
        }
        UpdateConfig updateConfig = getUpdateConfig();

        if (updateConfig.getDataSourceType() != TypeConfig.DATA_SOURCE_TYPE_JSON) {
            LogUtils.log("?????? DATA_SOURCE_TYPE_JSON ???????????????????????????????????????UpdateConfig??????dataSourceType????????? DATA_SOURCE_TYPE_JSON ");
            return;
        }

        if (updateConfig.getModelClass() == null || !(updateConfig.getModelClass() instanceof LibraryUpdateEntity)) {
            LogUtils.log("?????? DATA_SOURCE_TYPE_JSON ???????????????????????????????????????UpdateConfig??????modelClass???????????????modelClass????????????LibraryUpdateEntity??????");
            return;
        }

        try {
            Object data = JSONHelper.parseObject(jsonData, updateConfig.getModelClass().getClass());//????????????
            requestSuccess(data);
        } catch (Exception e) {
            LogUtils.log("JSON???????????????????????????json??????????????????????????????modelClass");
        }
    }

    /**
     * ???????????? ????????????????????? ???????????????????????????????????? ???????????????????????? ?????????????????????
     */
    public void checkUpdate(DownloadInfo info) {
        checkInit();

        if (info == null) {
            return;
        }

        //???????????????????????????????????? ??????app?????????????????????????????????????????????????????? ?????????????????????
        int versionCode = AppUtils.getVersionCode(mContext);
        String version = AppUtils.getVersion(mContext);

        if (Double.parseDouble(version) >= Double.parseDouble(info.getProdVersionName())) {
            listenToUpdateInfo(true);
            clearAllListener();
            return;
        }

        //???????????????????????????????????? ??????app?????????????????????????????????????????????????????? ?????????????????????
//        int versionCode = AppUtils.getVersionCode(mContext);
//        if (versionCode >= info.getProdVersionCode()) {
//            listenToUpdateInfo(true);
//            clearAllListener();
//            return;
//        }

        //????????????????????????????????????
        listenToUpdateInfo(false);

        UpdateConfig updateConfig = getUpdateConfig();
        //????????????????????????????????? ???????????????????????????????????????????????????
        if (!updateConfig.isAutoDownloadBackground()) {
            //????????????????????????
            if (info.getForceUpdateFlag() != 0) {
                //??????????????????
                if (info.getForceUpdateFlag() == 1) {
                    //hasAffectCodes????????????????????????
                    String hasAffectCodes = info.getHasAffectCodes();
                    if (!TextUtils.isEmpty(hasAffectCodes)) {
                        List<String> codes = Arrays.asList(hasAffectCodes.split("\\|"));
                        if (codes.contains(versionCode + "")) {
                            //?????????????????? ????????????????????????
                        } else {
                            //????????????????????? ?????????????????????????????????
                            info.setForceUpdateFlag(0);
                        }
                    }
                } else {
                    //??????????????????????????????
                }
            }
        }


        //??????sdk????????? ?????????????????????
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            LogUtils.log("sdk????????????");
            return;
        }

        int type = updateConfig.getUiThemeType();
        if (type == TypeConfig.UI_THEME_AUTO) {
            //????????????
            String versionName = AppUtils.getVersionName(mContext);
            type = 301 + versionName.hashCode() % 12;
        } else if (type == TypeConfig.UI_THEME_CUSTOM) {
            Class customActivityClass = updateConfig.getCustomActivityClass();
            if (customActivityClass == null) {
                LogUtils.log("?????? UI_THEME_CUSTOM ??????UI?????????????????????????????????UpdateConfig??????customActivityClass????????????????????????Activity");
                return;
            }
            //?????????????????????
            Intent intent = new Intent(mContext, updateConfig.getCustomActivityClass());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("info", info);
            mContext.startActivity(intent);
            return;
        }

        //????????????????????????????????? ?????????????????????????????????
        if (!updateConfig.isAutoDownloadBackground()) {
            //?????????????????????????????????
            if (type == TypeConfig.UI_THEME_A) {
                UpdateType1Activity.launch(mContext, info);
            } else if (type == TypeConfig.UI_THEME_B) {
                UpdateType2Activity.launch(mContext, info);
            } else if (type == TypeConfig.UI_THEME_C) {
                UpdateType3Activity.launch(mContext, info);
            } else if (type == TypeConfig.UI_THEME_D) {
                UpdateType4Activity.launch(mContext, info);
            } else if (type == TypeConfig.UI_THEME_E) {
                UpdateType5Activity.launch(mContext, info);
            } else if (type == TypeConfig.UI_THEME_F) {
                UpdateType6Activity.launch(mContext, info);
            } else if (type == TypeConfig.UI_THEME_G) {
                UpdateType7Activity.launch(mContext, info);
            } else if (type == TypeConfig.UI_THEME_H) {
                UpdateType8Activity.launch(mContext, info);
            } else if (type == TypeConfig.UI_THEME_I) {
                UpdateType9Activity.launch(mContext, info);
            } else if (type == TypeConfig.UI_THEME_J) {
                UpdateType10Activity.launch(mContext, info);
            } else if (type == TypeConfig.UI_THEME_K) {
                UpdateType11Activity.launch(mContext, info);
            } else if (type == TypeConfig.UI_THEME_L) {
                UpdateType12Activity.launch(mContext, info);
            }
        } else {
            //????????????
            UpdateBackgroundActivity.launch(mContext, info);
            //?????????????????????????????? ???????????????????????????
            clearAllListener();
        }
    }

    /**
     * ????????????
     *
     * @param info
     */
    public void download(DownloadInfo info) {
        checkInit();

        downloadInfo = info;

        FileDownloader.setup(mContext);

        downloadUpdateApkFilePath = getAppLocalPath(mContext, info.getProdVersionName());

        //?????????????????????????????? ????????????????????????????????????????????????????????????????????? ??????????????????
        File tempFile = new File(downloadUpdateApkFilePath);
        if (tempFile != null && tempFile.exists()) {
            if (tempFile.length() != info.getFileSize()) {
                AppUtils.deleteFile(downloadUpdateApkFilePath);
                AppUtils.deleteFile(FileDownloadUtils.getTempPath(downloadUpdateApkFilePath));
            }
        }

        downloadTask = FileDownloader.getImpl().create(info.getApkUrl())
                .setPath(downloadUpdateApkFilePath);
        downloadTask
                .addHeader("Accept-Encoding", "identity")
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/37.0.2062.120 Safari/537.36")
                .setListener(fileDownloadListener)
                .setAutoRetryTimes(3)
                .start();
    }

    /**
     * ????????????
     */
    public void cancelTask() {
        isDownloading = false;
        if (downloadTask != null) {
            downloadTask.pause();
        }
        UpdateReceiver.cancelDownload(mContext);
    }

    private FileDownloadListener fileDownloadListener = new FileDownloadLargeFileListener() {
        @Override
        protected void pending(BaseDownloadTask task, long soFarBytes, long totalBytes) {
            downloadStart();
            if (totalBytes < 0) {
                downloadTask.pause();
            }
        }

        @Override
        protected void progress(BaseDownloadTask task, long soFarBytes, long totalBytes) {
            downloading(soFarBytes, totalBytes);
            if (totalBytes < 0) {
                downloadTask.pause();
            }
        }

        @Override
        protected void paused(BaseDownloadTask task, long soFarBytes, long totalBytes) {
            for (AppDownloadListener appDownloadListener : getAllAppDownloadListener()) {
                appDownloadListener.pause();
            }
        }

        @Override
        protected void completed(BaseDownloadTask task) {
            downloadComplete(task.getPath());
        }

        @Override
        protected void error(BaseDownloadTask task, Throwable e) {
            AppUtils.deleteFile(downloadUpdateApkFilePath);
            AppUtils.deleteFile(FileDownloadUtils.getTempPath(downloadUpdateApkFilePath));
            downloadError(e);
        }

        @Override
        protected void warn(BaseDownloadTask task) {

        }
    };

    /**
     * @param e
     */
    private void downloadError(Throwable e) {
        isDownloading = false;
        AppUtils.deleteFile(downloadUpdateApkFilePath);
        UpdateReceiver.send(mContext, -1);
        for (AppDownloadListener appDownloadListener : getAllAppDownloadListener()) {
            appDownloadListener.downloadFail(e.getMessage());
        }
        LogUtils.log("???????????????????????????????????????" + e.getMessage());
    }

    /**
     * ????????????
     *
     * @param path
     */
    private void downloadComplete(String path) {
        isDownloading = false;
        UpdateReceiver.send(mContext, 100);
        for (AppDownloadListener appDownloadListener : getAllAppDownloadListener()) {
            appDownloadListener.downloadComplete(path);
        }
        LogUtils.log("???????????????????????????????????????????????????" + downloadUpdateApkFilePath);
        //??????MD5
        File newFile = new File(path);
        if (newFile.exists()) {
            //??????????????????MD5??????
            if (updateConfig.isNeedFileMD5Check()) {
                try {
                    String md5 = Md5Utils.getFileMD5(newFile);
                    if (!TextUtils.isEmpty(md5) && md5.equals(downloadInfo.getMd5Check())) {
                        //????????????
                        for (MD5CheckListener md5CheckListener : getAllMD5CheckListener()) {
                            md5CheckListener.fileMd5CheckSuccess();
                        }
                        AppUtils.installApkFile(mContext, newFile);
                        LogUtils.log("??????MD5????????????");
                    } else {
                        //????????????
                        for (MD5CheckListener md5CheckListener : getAllMD5CheckListener()) {
                            md5CheckListener.fileMd5CheckFail(downloadInfo.getMd5Check(), md5);
                        }
                        LogUtils.log("??????MD5???????????????originMD5???" + downloadInfo.getMd5Check() + "  localMD5???" + md5);
                    }
                } catch (Exception e) {
                    LogUtils.log("??????MD5??????????????????????????????" + e.getMessage());
                    //????????????
                    AppUtils.installApkFile(mContext, newFile);
                }
            } else {
                //????????????
                AppUtils.installApkFile(mContext, newFile);
            }
        }
    }


    /**
     * ????????????
     *
     * @param soFarBytes
     * @param totalBytes
     */
    private void downloading(long soFarBytes, long totalBytes) {
        isDownloading = true;
        int progress = (int) (soFarBytes * 100.0 / totalBytes);
        if (progress < 0) progress = 0;
        UpdateReceiver.send(mContext, progress);
        for (AppDownloadListener appDownloadListener : getAllAppDownloadListener()) {
            appDownloadListener.downloading(progress);
        }
        LogUtils.log("?????????????????????????????????" + progress + "%");
    }

    /**
     * ????????????
     */
    private void downloadStart() {
        LogUtils.log("??????????????????");
        isDownloading = true;
        UpdateReceiver.send(mContext, 0);
        for (AppDownloadListener appDownloadListener : getAllAppDownloadListener()) {
            appDownloadListener.downloadStart();
        }
    }

    public static boolean isDownloading() {
        checkInit();
        return isDownloading;
    }

    public UpdateConfig getUpdateConfig() {
        if (updateConfig == null) {
            return new UpdateConfig();
        }
        return updateConfig;
    }

    /**
     * ???????????????
     *
     * @return
     */
    private static void checkInit() {
        if (!isInit) {
            throw new RuntimeException("AppUpdateUtils???????????????init?????????????????????????????????");
        }
    }

    /**
     * ??????Context
     *
     * @return
     */
    public Context getContext() {
        checkInit();
        return mContext;
    }

    /**
     * ????????????
     */
    public void reDownload() {
        for (AppDownloadListener appDownloadListener : getAllAppDownloadListener()) {
            appDownloadListener.reDownload();
        }
        download(downloadInfo);
    }

    /**
     * ???????????????????????????
     */
    public void clearAllData() {
        //??????????????????????????????
        FileDownloader.getImpl().clearAllTaskData();
        //??????????????????????????????
        AppUtils.delAllFile(new File(AppUtils.getAppRootPath(mContext)));
    }

    /**
     * ????????????
     */
    private void getData() {
        UpdateConfig updateConfig = getUpdateConfig();
        Object modelClass = updateConfig.getModelClass();
        if (modelClass != null) {
            if (modelClass instanceof LibraryUpdateEntity) {
                if (updateConfig.getMethodType() == TypeConfig.METHOD_GET) {
                    //GET??????
                    HttpUtils.doGet(AppUpdateUtils.getInstance().getContext(), updateConfig.getBaseUrl(), updateConfig.getRequestHeaders(), updateConfig.getModelClass().getClass(), new HttpCallbackModelListener() {
                        @Override
                        public void onFinish(Object response) {
                            requestSuccess(response);
                        }

                        @Override
                        public void onError(Exception e) {
                            LogUtils.log("GET?????????????????????" + e.getMessage());
                        }
                    });
                } else {
                    //POST??????
                    HttpUtils.doPost(AppUpdateUtils.getInstance().getContext(), updateConfig.getBaseUrl(), updateConfig.getRequestHeaders(), updateConfig.getRequestParams(), updateConfig.getModelClass().getClass(), new HttpCallbackModelListener() {
                        @Override
                        public void onFinish(Object response) {
                            requestSuccess(response);
                        }

                        @Override
                        public void onError(Exception e) {
                            LogUtils.log("POST?????????????????????" + e.getMessage());
                        }
                    });
                }
            } else {
                throw new RuntimeException(modelClass.getClass().getSimpleName() + "????????????LibraryUpdateEntity??????");
            }
        }
    }

    private void requestSuccess(Object response) {
        LibraryUpdateEntity libraryUpdateEntity = (LibraryUpdateEntity) response;
        if (libraryUpdateEntity != null) {
            checkUpdate(new DownloadInfo()
                    .setForceUpdateFlag(libraryUpdateEntity.forceAppUpdateFlag())
                    .setProdVersionCode(libraryUpdateEntity.getAppVersionCode())
                    .setFileSize(Long.parseLong(libraryUpdateEntity.getAppApkSize()))
                    .setProdVersionName(libraryUpdateEntity.getAppVersionName())
                    .setApkUrl(libraryUpdateEntity.getAppApkUrls())
                    .setHasAffectCodes(libraryUpdateEntity.getAppHasAffectCodes())
                    .setMd5Check(libraryUpdateEntity.getFileMd5Check())
                    .setUpdateLog(libraryUpdateEntity.getAppUpdateLog()));
        }
    }

    public AppUpdateUtils addMd5CheckListener(MD5CheckListener md5CheckListener) {
        if (md5CheckListener != null && !md5CheckListenerList.contains(md5CheckListener)) {
            md5CheckListenerList.add(md5CheckListener);
        }
        return this;
    }

    public AppUpdateUtils addAppDownloadListener(AppDownloadListener appDownloadListener) {
        if (appDownloadListener != null && !appDownloadListenerList.contains(appDownloadListener)) {
            appDownloadListenerList.add(appDownloadListener);
        }
        return this;
    }

    public AppUpdateUtils addAppUpdateInfoListener(AppUpdateInfoListener appUpdateInfoListener) {
        if (appUpdateInfoListener != null && !appUpdateInfoListenerList.contains(appUpdateInfoListener)) {
            appUpdateInfoListenerList.add(appUpdateInfoListener);
        }
        return this;
    }

    public List<AppUpdateInfoListener> getAllAppUpdateInfoListener() {
        List<AppUpdateInfoListener> listeners = new ArrayList<>();
        listeners.addAll(appUpdateInfoListenerList);
        return listeners;
    }

    private List<AppDownloadListener> getAllAppDownloadListener() {
        List<AppDownloadListener> listeners = new ArrayList<>();
        listeners.addAll(appDownloadListenerList);
        return listeners;
    }

    private List<MD5CheckListener> getAllMD5CheckListener() {
        List<MD5CheckListener> listeners = new ArrayList<>();
        listeners.addAll(md5CheckListenerList);
        return listeners;
    }

    //????????????????????????
    private void listenToUpdateInfo(boolean isLatest) {
        for (AppUpdateInfoListener appUpdateInfoListener : getAllAppUpdateInfoListener()) {
            appUpdateInfoListener.isLatestVersion(isLatest);
        }
    }

    //??????????????????
    protected static void clearAllListener() {
        md5CheckListenerList.clear();
        appUpdateInfoListenerList.clear();
        appDownloadListenerList.clear();
    }
}
