package goads.app.com.goadsapp.entity;

/**
 * Created by Raj on 11/9/16.
 */
public class DisplayDemandTag {
    private int displayDemandTagID;
    private int width;
    private int height;
    private String imageFile;
    private String flashFile;
    private String jsTag;
    private String clickThroughURL;
    private String title;
    private String description;
    private String faviconURL;
    private String advertiserDomain;

    public int getDisplayDemandTagID() {
        return displayDemandTagID;
    }

    public void setDisplayDemandTagID(int displayDemandTagID) {
        this.displayDemandTagID = displayDemandTagID;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getImageFile() {
        return imageFile;
    }

    public void setImageFile(String imageFile) {
        this.imageFile = imageFile;
    }

    public String getFlashFile() {
        return flashFile;
    }

    public void setFlashFile(String flashFile) {
        this.flashFile = flashFile;
    }

    public String getJsTag() {
        return jsTag;
    }

    public void setJsTag(String jsTag) {
        this.jsTag = jsTag;
    }

    public String getClickThroughURL() {
        return clickThroughURL;
    }

    public void setClickThroughURL(String clickThroughURL) {
        this.clickThroughURL = clickThroughURL;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFaviconURL() {
        return faviconURL;
    }

    public void setFaviconURL(String faviconURL) {
        this.faviconURL = faviconURL;
    }

    public String getAdvertiserDomain() {
        return advertiserDomain;
    }

    public void setAdvertiserDomain(String advertiserDomain) {
        this.advertiserDomain = advertiserDomain;
    }
}
