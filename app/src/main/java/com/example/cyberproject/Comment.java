package com.example.cyberproject;

public class Comment{

    private String textCmt;
    private String createBy;

    public Comment(){}
    public Comment(String createBy,String textCmt){
        this.textCmt = textCmt;
        this.createBy = createBy;
    }

    public String getTextCmt() { return textCmt; }
    public String getCreateBy() { return createBy; }
    public void setTextCmt(String textCmt) { this.textCmt = textCmt; }
    public void setCreateBy(String createBy) { this.createBy = createBy; }

    public String toString(){ return createBy+": "+textCmt; }

}