package app.com.thetechnocafe.linkshortner.Networking;

import app.com.thetechnocafe.linkshortner.Models.LongLinkPOSTModel;
import app.com.thetechnocafe.linkshortner.Models.ShortenedLinkModel;
import app.com.thetechnocafe.linkshortner.Models.UrlListModels.ShortenedLinks;
import app.com.thetechnocafe.linkshortner.Utilities.Constants;
import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by gurleensethi on 25/02/17.
 */

public interface GoogleLinkShortenerAPI {
    @POST("url?key=AIzaSyCc1i6J2DwNqinDnJlr2AXnKlNJrLrgqkY")
    Observable<ShortenedLinkModel> getShortenedLink(@Body LongLinkPOSTModel longUrl);

    @POST("url")
    Observable<ShortenedLinkModel> getShortenedLinkWithAuth(@Header(Constants.AUTHORIZATION) String authKey, @Header(Constants.CONTENT_TYPE) String contentType, @Body LongLinkPOSTModel longUrl);

    @GET("url/history")
    Observable<ShortenedLinks> getListOfShortenedLinks(@Header(Constants.AUTHORIZATION) String authKey, @Query("projection") String projection);

    @GET("url/history")
    Observable<ShortenedLinks> getListOfShortenedLinksWithStartToken(@Header(Constants.AUTHORIZATION) String authKey, @Query("projection") String projection, @Query("start-token") String startToken);
}
