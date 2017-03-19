package app.com.thetechnocafe.linkshortner.Models.LinkStatsModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by gurleensethi on 19/03/17.
 */

public class Browser {

    @SerializedName("count")
    @Expose
    private String count;
    @SerializedName("id")
    @Expose
    private String id;

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}