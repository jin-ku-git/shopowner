package com.youwu.shopowner.bean;

import androidx.databinding.BaseObservable;

import java.io.Serializable;

/**
 * 2021/12/23
 * 版本更新
 * 金库
 */

public class UpDateBean extends BaseObservable implements Serializable {
    /**
     * "id": 1,
     * "name": "版本一",
     * "version": "1.2",
     * "download_url": "http://files.youwuu.com/upload_file/2021-11-29-161654/取餐柜补货_v1.2-20211129161218-Debug-3.apk",
     * "channel": "xxc-bh",
     * "status": 1,
     * "create_at": "2021-11-29 14:42:58",
     * "content": "更新内容",
     * "update_status": 2
     */





    private String id;//用户id
    private String name;//版本名称
    private String version;//版本号
    private String download_url;//下载地址
    private String channel;//
    private String status;//
    private String create_at;//
    private String content;//更新内容
    private int update_status;//1 强制更新  2不强制

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDownload_url() {
        return download_url;
    }

    public void setDownload_url(String download_url) {
        this.download_url = download_url;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreate_at() {
        return create_at;
    }

    public void setCreate_at(String create_at) {
        this.create_at = create_at;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getUpdate_status() {
        return update_status;
    }

    public void setUpdate_status(int update_status) {
        this.update_status = update_status;
    }
}
