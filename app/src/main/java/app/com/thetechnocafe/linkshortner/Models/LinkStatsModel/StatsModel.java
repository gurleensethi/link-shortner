package app.com.thetechnocafe.linkshortner.Models.LinkStatsModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import app.com.thetechnocafe.linkshortner.Models.UrlListModels.Analytics;

/**
 * Created by gurleensethi on 19/03/17.
 */

public class StatsModel {

    @SerializedName("kind")
    @Expose
    private String kind;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("longUrl")
    @Expose
    private String longUrl;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("created")
    @Expose
    private String created;
    @SerializedName("analytics")
    @Expose
    private Analytics analytics;

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLongUrl() {
        return longUrl;
    }

    public void setLongUrl(String longUrl) {
        this.longUrl = longUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public Analytics getAnalytics() {
        return analytics;
    }

    public void setAnalytics(Analytics analytics) {
        this.analytics = analytics;
    }

}