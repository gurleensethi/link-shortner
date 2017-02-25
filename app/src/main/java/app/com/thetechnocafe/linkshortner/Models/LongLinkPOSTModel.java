package app.com.thetechnocafe.linkshortner.Models;

/**
 * Created by gurleensethi on 25/02/17.
 */

public class LongLinkPOSTModel {
    private String longUrl;

    public LongLinkPOSTModel(String longUrl) {
        this.longUrl = longUrl;
    }

    public String getLongUrl() {
        return longUrl;
    }

    public void setLongUrl(String longUrl) {
        this.longUrl = longUrl;
    }
}
