package app.com.thetechnocafe.linkshortner.Models.LinkStatsModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


/**
 * Created by gurleensethi on 19/03/17.
 */

public class Month {

    @SerializedName("shortUrlClicks")
    @Expose
    private String shortUrlClicks;
    @SerializedName("longUrlClicks")
    @Expose
    private String longUrlClicks;
    @SerializedName("referrers")
    @Expose
    private List<Referrer> referrers = null;
    @SerializedName("countries")
    @Expose
    private List<Country> countries = null;
    @SerializedName("browsers")
    @Expose
    private List<Browser> browsers = null;
    @SerializedName("platforms")
    @Expose
    private List<Platform> platforms = null;

    public String getShortUrlClicks() {
        return shortUrlClicks;
    }

    public void setShortUrlClicks(String shortUrlClicks) {
        this.shortUrlClicks = shortUrlClicks;
    }

    public String getLongUrlClicks() {
        return longUrlClicks;
    }

    public void setLongUrlClicks(String longUrlClicks) {
        this.longUrlClicks = longUrlClicks;
    }

    public List<Referrer> getReferrers() {
        return referrers;
    }

    public void setReferrers(List<Referrer> referrers) {
        this.referrers = referrers;
    }

    public List<Country> getCountries() {
        return countries;
    }

    public void setCountries(List<Country> countries) {
        this.countries = countries;
    }

    public List<Browser> getBrowsers() {
        return browsers;
    }

    public void setBrowsers(List<Browser> browsers) {
        this.browsers = browsers;
    }

    public List<Platform> getPlatforms() {
        return platforms;
    }

    public void setPlatforms(List<Platform> platforms) {
        this.platforms = platforms;
    }

}
