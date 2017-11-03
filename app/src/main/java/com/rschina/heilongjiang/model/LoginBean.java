package com.rschina.heilongjiang.model;

/**
 * Created by Administrator on 2017/9/25.
 */

public class LoginBean {


    /**
     * userName : OH679710670684
     * tgt : TGT-376-bHLVAxeTbJCsl2JExRz5xmpkJcotRbCfjX4rtZ0PUSknkZ1N7i-cas01.example.org
     * code : 1001
     * msg : 登录成功！
     */

    private String userName;
    private String tgt;
    private String code;
    private String msg;
    private String contact;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTgt() {
        return tgt;
    }

    public void setTgt(String tgt) {
        this.tgt = tgt;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getContact() {
        return contact;
    }
}
