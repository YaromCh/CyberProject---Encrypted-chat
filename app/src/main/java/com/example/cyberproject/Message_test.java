package com.example.cyberproject;

import android.graphics.Point;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Message_test {

    private MessageDetails messageDetails;

    public Message_test(){
        messageDetails = new MessageDetails();
    }
    public Message_test(MessageDetails m){
        messageDetails = new MessageDetails(m);

    }
    public Message_test(Date createD, String createBy, String myToken, boolean isEncrypted, Point placeCreate, String msgText){
        this.messageDetails = new MessageDetails(createD, createBy,myToken, isEncrypted, placeCreate, msgText);
    }

    public MessageDetails getMessageDetails() { return messageDetails; }
    public void setMessageDetails(MessageDetails messageDetails) { this.messageDetails = messageDetails; }
    public HashMap<String,Object> getHashMap(){
        String commentsS = "";
        for(int i=0; i<messageDetails.getComments().size(); i++)
            commentsS+=messageDetails.getComments().get(i).toString()+'\n';
        HashMap<String,Object> h = new HashMap<>();
        h.put("createD", messageDetails.getCreateD());
        h.put("createBy", messageDetails.getCreateBy());
        h.put("msgText", messageDetails.getMsgText());
        h.put("comments", messageDetails.getComments());
        return h;
    }

    public class MessageDetails {
        private String msgId;
        private Date createD;
        private String createBy;
        private String myToken;
        private boolean isEncrypted;
        private Point placeCreate;
        private String msgText;
        private ArrayList<Comment> comments;

        public MessageDetails(){
            this.comments = new ArrayList<Comment>();
        }
        public MessageDetails(MessageDetails m){
            this.msgId = m.msgId;
            this.createD = m.createD;
            this.createBy = m.createBy;
            this.myToken = m.myToken;
            this.isEncrypted = m.isEncrypted;
            this.placeCreate = m.placeCreate;
            this.msgText = m.msgText;
            this.comments = m.comments;
        }
        public MessageDetails(Date createD, String createBy,String myToken, boolean isEncrypted, Point placeCreate, String msgText){
            this.msgId = createBy+createD;
            this.comments = new ArrayList<Comment>();
            this.createD = createD;
            this.createBy = createBy;
            this.myToken = myToken;
            this.isEncrypted = isEncrypted;
            this.placeCreate = placeCreate;
            this.msgText = msgText;
        }

        public MessageDetails(Date createD, String createBy,String myToken, boolean isEncrypted, Point placeCreate, String msgText, ArrayList<Comment> comments){
            this.msgId = createBy+createD;
            this.createD = createD;
            this.createBy = createBy;
            this.myToken = myToken;
            this.isEncrypted = isEncrypted;
            this.placeCreate = placeCreate;
            this.msgText = msgText;
            this.comments = comments;
        }

        public Date getCreateD() { return createD; }
        public String getCreateBy() { return createBy; }
        public String getMyToken() { return myToken; }
        public boolean isEncrypted() { return isEncrypted; }
        public Point getPlaceCreate() { return placeCreate; }
        public String getMsgText() { return msgText; }
        public List<Comment> getComments() { return comments; }
        public String getMsgId() { return msgId; }

        public void setCreateD(Date createD) { this.createD = createD; }
        public void setCreateBy(String createBy) { this.createBy = createBy; }
        public void setMyToken(String myToken) { this.myToken = myToken; }
        public void setEncrypted(boolean encrypted) { isEncrypted = encrypted; }
        public void setPlaceCreate(Point placeCreate) { this.placeCreate = placeCreate; }
        public void setMsgText(String msgText) { this.msgText = msgText; }
        public void setComments(ArrayList<Comment> comments) { this.comments = comments; }
        public void setMsgId(String msgId) { this.msgId = msgId; }

        public void addComment(Date createD, String createBy, String myToken, String cmntText){
            addCommentC(new Comment(createD, createBy, myToken, cmntText));
        }

        public void addCommentC(Comment c){
            if(this.comments == null) {
                this.comments = new ArrayList<Comment>();
            }
            this.comments.add(c);
        }

        private class Comment {
            private Date createD;
            private String createBy;
            private String myToken;
            private String cmntText;

            public Comment(){}
            public Comment(Comment c){
                this.createD = c.createD;
                this.createBy = c.createBy;
                this.myToken = c.myToken;
                this.cmntText = cmntText;
            }

            public Comment(Date createD, String createBy, String myToken, String cmntText){
                this.createD = createD;
                this.createBy = createBy;
                this.myToken = myToken;
                this.cmntText = cmntText;
            }

            public Date getCreateD() { return createD; }
            public String getCreateBy() { return createBy; }
            public String getMyToken() { return myToken; }
            public String getCmntText() { return cmntText; }

            public void setCreateD(Date createD) { this.createD = createD; }
            public void setCreateBy(String createBy) { this.createBy = createBy; }
            public void setMyToken(String createBy) { this.myToken = myToken; }
            public void setCmntText(String cmntText) { this.cmntText = cmntText; }

//            public HashMap<String,Object> getHashMap(){
//                HashMap<String,Object> h = new HashMap<>();
//                h.put("createD", createD);
//                h.put("createBy", createBy);
//                h.put("cmntText", cmntText);
//                return h;
//            }

            @NonNull
            @Override
            public String toString() {
                return createBy+"/"+createD+">>"+cmntText;
            }
        }
    }
}
