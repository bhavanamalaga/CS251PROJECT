package com.example.socialley;


public class comment {

    String cId;

    String comment;

    String ptime;

    String udp;

    String uname;

    String uid;

    String uemail;

    public String getcId() {
        return cId;
    }

    public void setcId(String cId) {
        this.cId = cId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getPtime() {
        return ptime;
    }

    public void setPtime(String ptime) {
        this.ptime = ptime;
    }

    public String getUdp() {
        return udp;
    }

    public void setUdp(String udp) {
        this.udp = udp;
    }

    public String getUemail() {
        return uemail;
    }

    public void setUemail(String uemail) {
        this.uemail = uemail;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public comment() {
    }


    public comment(String cId, String comment, String ptime, String udp, String uemail, String uid, String uname) {
        this.cId = cId;
        this.comment = comment;
        this.ptime = ptime;
        this.udp = udp;
        this.uemail = uemail;
        this.uid = uid;
        this.uname = uname;
    }
}
