
package app.com.thetechnocafe.linkshortner.Models.UrlListModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Week {

    @SerializedName("shortUrlClicks")
    @Expose
    private String shortUrlClicks;

    public String getShortUrlClicks() {
        return shortUrlClicks;
    }

    public void setShortUrlClicks(String shortUrlClicks) {
        this.shortUrlClicks = shortUrlClicks;
    }

}
