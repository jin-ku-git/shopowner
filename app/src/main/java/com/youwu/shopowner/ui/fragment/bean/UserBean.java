package com.youwu.shopowner.ui.fragment.bean;

import java.io.Serializable;

public class UserBean implements Serializable {

    /**
     * id : 1
     * name : 收银员1
     * tel : 15806906223
     * store_id : 1
     * company_id : 1
     * created_at : 2022-05-25 03:43:36
     * updated_at : 2022-05-25 03:43:36
     * store_name : 门店1
     */

    private int id;
    private String name;//名称
    private String tel;//手机号
    private int store_id;//门店ID
    private int company_id;//公司id
    private String created_at;
    private String updated_at;
    private String store_name;//门店名称
    private String topic;//MQTT订阅主题
    private int is_order;//1 自动接单 2 手动接单
    private String start;//营业开始时间
    private String end;//营业结束时间

    public int getIs_order() {
        return is_order;
    }

    public void setIs_order(int is_order) {
        this.is_order = is_order;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public int getStore_id() {
        return store_id;
    }

    public void setStore_id(int store_id) {
        this.store_id = store_id;
    }

    public int getCompany_id() {
        return company_id;
    }

    public void setCompany_id(int company_id) {
        this.company_id = company_id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getStore_name() {
        return store_name;
    }

    public void setStore_name(String store_name) {
        this.store_name = store_name;
    }
}
