package com.example.feedssample;

public class ModelFeed {
    int id;
    String name, proPic, postPic, status;

    public ModelFeed(int id, String proPic , String postPic, String name, String status){
        this.id = id;
        this.proPic = proPic;
        this.postPic = postPic;
        this.name = name;
        this.status = status;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProPic() {
        return proPic;
    }

    public void setProPic(String proPic) {
        this.proPic = proPic;
    }

    public String getPostPic() {
        return postPic;
    }

    public void setPostPic(String postPic) {
        this.postPic = postPic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
