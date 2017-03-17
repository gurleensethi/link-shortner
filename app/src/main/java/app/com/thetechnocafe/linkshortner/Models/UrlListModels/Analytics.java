
package app.com.thetechnocafe.linkshortner.Models.UrlListModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

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

    public Analytics() {
        allTime = new AllTime();
        month = new Month();
        week = new Week();
        day = new Day();
        twoHours = new TwoHours();
    }

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
