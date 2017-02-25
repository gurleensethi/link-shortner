package app.com.thetechnocafe.linkshortner.Networking;

import app.com.thetechnocafe.linkshortner.Models.ShortenedLinkModel;
import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.POST;

/**
 * Created by gurleensethi on 25/02/17.
 */

public interface GoogleLinkShortenerAPI {
    @POST("url?key=AIzaSyCc1i6J2DwNqinDnJlr2AXnKlNJrLrgqkY")
    Observable<ShortenedLinkModel> getShortenedLink(@Field("longUrl") String longUrl);
}
