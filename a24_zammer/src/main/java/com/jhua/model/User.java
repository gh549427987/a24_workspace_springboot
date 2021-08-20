package com.jhua.model;

import java.io.Serializable;
import java.util.Date;

public class User implements Serializable {
	
	private static final long serialVersionUID = -8338645486168560715L;

    private Long id;

    private String userId;

    private String userIconUrl;

    private String nickName;

    private String mobileNo;

    private String email;

    private Byte status;

    private String type;

    private String chainMerchantId;

    private Integer onlineLimit;

    private String userLevel;

    private String discountCardMatrixOrderid;

    private String username;

    private String password;

    private String salt;

    private Byte loginType;

    private Long sdkAid;

    private String sdkUserinfo;

    private Date lastLoginTime;

    private String remark;

    private String createBy;

    private String updateBy;

    private Date createTime;

    private Date updateTime;
	
    // 该字段不在数据库中
    private String sdkLoginJson;

    // 该字段不在数据库中
    private String deviceid;

    // 该字段不在数据库中
    private String udid;

    // 该字段不在数据库中
    private String ip;

    // 该字段不在数据库中
    private String sessionId;

    // 该字段不在数据库中
    //Launcher端登录标记，用来区别在商家管理后台、运营管理后台的session
    private Boolean isLauncher = false;

    // 该字段不在数据库中
    private String terminalSource;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId == null ? null : userId.trim();
    }

    public String getUserIconUrl() {
        return userIconUrl;
    }

    public void setUserIconUrl(String userIconUrl) {
        this.userIconUrl = userIconUrl == null ? null : userIconUrl.trim();
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName == null ? null : nickName.trim();
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo == null ? null : mobileNo.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email == null ? null : email.trim();
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    public String getChainMerchantId() {
        return chainMerchantId;
    }

    public void setChainMerchantId(String chainMerchantId) {
        this.chainMerchantId = chainMerchantId == null ? null : chainMerchantId.trim();
    }

    public Integer getOnlineLimit() {
        return onlineLimit;
    }

    public void setOnlineLimit(Integer onlineLimit) {
        this.onlineLimit = onlineLimit;
    }

    public String getUserLevel() {
        return userLevel;
    }

    public void setUserLevel(String userLevel) {
        this.userLevel = userLevel == null ? null : userLevel.trim();
    }

    public String getDiscountCardMatrixOrderid() {
        return discountCardMatrixOrderid;
    }

    public void setDiscountCardMatrixOrderid(String discountCardMatrixOrderid) {
        this.discountCardMatrixOrderid = discountCardMatrixOrderid == null ? null : discountCardMatrixOrderid.trim();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username == null ? null : username.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt == null ? null : salt.trim();
    }

    public Byte getLoginType() {
        return loginType;
    }

    public void setLoginType(Byte loginType) {
        this.loginType = loginType;
    }

    public Long getSdkAid() {
        return sdkAid;
    }

    public void setSdkAid(Long sdkAid) {
        this.sdkAid = sdkAid;
    }

    public String getSdkUserinfo() {
        return sdkUserinfo;
    }

    public void setSdkUserinfo(String sdkUserinfo) {
        this.sdkUserinfo = sdkUserinfo == null ? null : sdkUserinfo.trim();
    }

    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy == null ? null : createBy.trim();
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy == null ? null : updateBy.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getSdkLoginJson() {
        return sdkLoginJson;
    }

    public void setSdkLoginJson(String sdkLoginJson) {
        this.sdkLoginJson = sdkLoginJson;
    }

    public String getDeviceid() {
        return deviceid;
    }

    public void setDeviceid(String deviceid) {
        this.deviceid = deviceid;
    }

    public String getUdid() {
        return udid;
    }

    public void setUdid(String udid) {
        this.udid = udid;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Boolean getIsLauncher() {
        return isLauncher;
    }

    public void setIsLauncher(Boolean isLauncher) {
        this.isLauncher = isLauncher;
    }

    public String getTerminalSource() {
        return terminalSource;
    }

    public void setTerminalSource(String terminalSource) {
        this.terminalSource = terminalSource;
    }

}