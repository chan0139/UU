package com.example.uu;

public class ChatMessage {
    private String id;
    private String text;
    private String name;
    //user face --> photoUrl
    private String photoUrl;
    private String sentTime;
    private String imageUrl;

    public ChatMessage(){

    }
    public ChatMessage(String text,String name,String photoUrl,String sentTime,String imageUrl){
        this.text=text;
        this.name=name;
        this.photoUrl=photoUrl;
        this.sentTime =sentTime;
        this.imageUrl=imageUrl;
    }
    public String getId(){
        return id;
    }
    public void setId(String id){
        this.id=id;
    }
    public String getText(){
        return text;
    }
    public void setText(String text){
        this.text=text;
    }
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name=name;
    }
    public String getPhotoUrl(){
        return photoUrl;
    }
    public void setPhotoUrl(String photoUrl){
        this.photoUrl=photoUrl;
    }
    public String getSentTime(){ return sentTime; };
    public void setSentTime(String sentTime){ this.sentTime = sentTime;};
    public String getImageUrl(){
        return imageUrl;
    }
    public void setImageUrl(){
        this.imageUrl=imageUrl;
    }
}
