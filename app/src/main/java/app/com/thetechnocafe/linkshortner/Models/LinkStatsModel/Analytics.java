package app.com.thetechnocafe.linkshortner.Models.LinkStatsModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by gurleensethi on 19/03/17.
 */

public class Analytics {

    @SerializedName("allTime")
    @Expose
    private AllTime allTime;
    @SerializedName("month")
    @Expose
    private Month month;
    @SerializedName("week")
    @Expose
    private Week week;
    @SerializedName("day")
    @Expose
    private Day day;
    @SerializedName("twoHours")
    @Expose
    private TwoHours twoHours;

    public AllTime getAllTime() {
        return allTime;
    }

    public void setAllTime(AllTime allTime) {
        this.allTime = allTime;
    }

    public Month getMonth() {
        return month;
    }

    public void setMonth(Month month) {
        this.month = month;
    }

    public Week getWeek() {
        return week;
    }

    public void setWeek(Week week) {
        this.week = week;
    }

    public Day getDay() {
        return day;
    }

    public void setDay(Day day) {
        this.day = day;
    }

    public TwoHours getTwoHours() {
        return twoHours;
    }

    public void setTwoHours(TwoHours twoHours) {
        this.twoHours = twoHours;
    }

}
