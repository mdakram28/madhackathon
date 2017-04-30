package com.example.kislay.govvote.models;

/**
 * Created by kislay on 30-04-2017.
 */

public class AadharInfo {
    private String uid;
    private String name;
    private String gender;
    private String yob;
    private String co;
    private String loc;
    private String vtc;
    private String po;
    private String dist;
    private String subdist;
    private String state;
    private String pc;
    private String dob;

    public void setUid(String uid){
        this.uid = uid;
    }
    public String getUid(){
        return uid;
    }
    public void setName(String name){
        this.name = name;
    }
    public String getName(){
        return name;
    }
    public void setGender(String gender){
        this.gender = uid;
    }
    public String getGender(){
        return gender;
    }
    public void setYob(String yob){
        this.yob = yob;
    }
    public String getYob(){
        return yob;
    }
    public String getCo() {
        return co;
    }

    public void setCo(String co) {
        this.co = co;
    }

    public String getLoc() {
        return loc;
    }

    public void setLoc(String loc) {
        this.loc = loc;
    }

    public String getVtc() {
        return vtc;
    }

    public void setVtc(String vtc) {
        this.vtc = vtc;
    }

    public String getPo() {
        return po;
    }

    public void setPo(String po) {
        this.po = po;
    }

    public String getDist() {
        return dist;
    }

    public void setDist(String dist) {
        this.dist = dist;
    }

    public String getSubdist() {
        return subdist;
    }

    public void setSubdist(String subdist) {
        this.subdist = subdist;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPc() {
        return pc;
    }

    public void setPc(String pc) {
        this.pc = pc;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }
}
