package com.youwu.shopowner.ui.login;

public class LogBean {

    /**
     * accessToken : eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwczpcL1wvc2Fhc190ZXN0X2FwaS55b3V3dXUuY29tXC9hcHBcL2F1dGhcL2xvZ2luIiwiaWF0IjoxNjUzNjQwMDQ5LCJleHAiOjE2NTYyMzIwNDksIm5iZiI6MTY1MzY0MDA0OSwianRpIjoiQzM3eVFBWjBrU2ZDVDNDViIsInN1YiI6MSwicHJ2IjoiMzcxOTRmNTQ0M2EyYmVmZjIzNGIwMzljZDg2OTc4ZWI4YWJkMjljMyJ9.pL50_vjjJI6NC_eJgHnb3DGQ9cuSSxpYztqV_m2W0HM
     * tokenType : bearer
     * expiresIn : 2592000
     * userInfo : {"userId":1,"userName":"收银员1"}
     */

    private String accessToken;
    private String tokenType;
    private int expiresIn;
    private UserInfoBean userInfo;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public int getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(int expiresIn) {
        this.expiresIn = expiresIn;
    }

    public UserInfoBean getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfoBean userInfo) {
        this.userInfo = userInfo;
    }

    public static class UserInfoBean {
        /**
         * userId : 1
         * userName : 收银员1
         */

        private int userId;
        private String userName;

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }
    }
}
