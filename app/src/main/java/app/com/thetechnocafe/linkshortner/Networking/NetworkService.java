package app.com.thetechnocafe.linkshortner.Networking;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import app.com.thetechnocafe.linkshortner.Models.UrlListModels.ShortLink;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by gurleensethi on 25/02/17.
 */

public class NetworkService {
    private static final String TAG = NetworkService.class.getSimpleName();
    private static NetworkService mInstance;
    private static String baseUrl = "https://www.googleapis.com/urlshortener/v1/";
    private Retrofit mRetrofit;

    //Singleton class
    private NetworkService() {
        //Create the retrofit client
        mRetrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    //Instance method
    public static NetworkService getInstance() {
        if (mInstance == null) {
            mInstance = new NetworkService();
        }
        return mInstance;
    }

    //Return the Link Shortener API
    public GoogleLinkShortenerAPI getLinkShortenerAPI() {
        return mRetrofit.create(GoogleLinkShortenerAPI.class);
    }

    /**
     * Get all the links from the goo.gl api using the nextPageToken
     *
     * @param authToken  Authentication token required for access
     * @param projection
     */
    public Observable<List<ShortLink>> getAllLinksFromAPI(String authToken, String projection) {
        Observable<List<ShortLink>> observable = Observable.create(emitter -> {
            List<ShortLink> shortLinks = new ArrayList<>();

            getLinkShortenerAPI().getListOfShortenedLinks(authToken, projection)
                    .subscribe(shortenedLinks -> {
                        shortLinks.addAll(shortenedLinks.getShortenedLinks());
                        if (shortenedLinks.getNextPageToken() != null) {
                            getLinksWithStartToken(authToken, projection, shortenedLinks.getNextPageToken(), shortLinks);
                        }

                        emitter.onNext(shortLinks);
                    }, throwable -> {
                        Log.d(TAG, throwable.toString());
                    });
        });

        return observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Recursively get all the pages using the nextPageToken
     *
     * @param authToken     Authentication token required for access
     * @param projection
     * @param nextPageToken
     * @param shortLinks
     */
    private void getLinksWithStartToken(String authToken, String projection, String nextPageToken, List<ShortLink> shortLinks) {
        getLinkShortenerAPI().getListOfShortenedLinksWithStartToken(authToken, projection, nextPageToken)
                .blockingSubscribe(shortenedLinks -> {
                    shortLinks.addAll(shortenedLinks.getShortenedLinks());
                    if (shortenedLinks.getNextPageToken() != null) {
                        getLinksWithStartToken(authToken, projection, shortenedLinks.getNextPageToken(), shortLinks);
                    }
                }, throwable -> {
                    Log.d(TAG, throwable.toString());
                });
    }
}
