package com.tibep.proiectlicenta.ui.upload;

public class Upload {

    private String description;
    private String imageURL;
    private String name;
    private String timestamp;

    public Upload(){
    }

    Upload(String description, String imageURL,String name,String timestamp){
        if(description.trim().equals("")){
            description="Nu exista nici o descriere";
        }
        this.description =description;
        this.imageURL=imageURL;
        this.name=name;
        this.timestamp=timestamp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}
