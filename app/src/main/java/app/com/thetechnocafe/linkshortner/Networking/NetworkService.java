package app.com.thetechnocafe.linkshortner.Networking;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by gurleensethi on 25/02/17.
 */

public class NetworkService {
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
}
