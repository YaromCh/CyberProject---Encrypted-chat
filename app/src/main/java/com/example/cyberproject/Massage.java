package com.example.cyberproject;

import android.graphics.Point;

import java.util.ArrayList;

// Massage Class: ----------------------------------------------------------------------------------
public class Massage {
    private String createBy;
    private String textMsg;
    private boolean isEncrypted;
    private String createDate;
    private Point placeCreate;
    private ArrayList<Comment> comments;

    public Massage(){
        placeCreate = new Point();
        comments = new ArrayList<Comment>();
    }

    public void setCreateBy(String createBy) { this.createBy = createBy; }
    public void setTextMsg(String textMsg) { this.textMsg = textMsg; }
    public void setIsEncrypted(boolean isEncrypted) { this.isEncrypted = isEncrypted; }
    public void setCreateDate(String createDate) { this.createDate = createDate; }
    public void setPlaceCreate(Point placeCreate) { this.placeCreate = placeCreate; }
    public void setComments(ArrayList<Comment> comments) { this.comments = comments; }

    public String getCreateBy() { return createBy; }
    public String getTextMsg() { return textMsg; }
    public boolean getIsEncrypted() { return isEncrypted; }
    public String getCreateDate() { return createDate; }
    public Point getPlaceCreate() { return placeCreate; }
    public ArrayList<Comment> getComments() { return comments; }

}
