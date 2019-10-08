package david.black.davidpopularmoviess2.utils;

import android.content.Context;
import android.net.Uri;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import david.black.davidpopularmoviess2.R;
import java.io.IOException;
import java.net.URL;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.Scanner;
import java.io.InputStream;


public class NetworkUtils {
    private static final String PARAM_KEY = "api_key";
    private static final String VIDEO_KEY = "v";

    public static URL buildMovieQueryUrl(Context context, String sortOrder, String apiKey) {
        String baseUrl = context.getString(R.string.tmdb_base_url);
        Uri tmdbUri = Uri.parse(baseUrl).buildUpon()
                .appendPath(sortOrder)
                .appendQueryParameter(PARAM_KEY, apiKey)
                .build();
        URL tmdbUrl = null;
        try {
            tmdbUrl = new URL(tmdbUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return tmdbUrl;
    }


    public static URL buildTrailerQueryUrl(Context context, int movieId, String apiKey) {
        String baseUrl = context.getString(R.string.tmdb_base_url);
        Uri tmdbUri = Uri.parse(baseUrl).buildUpon()
                .appendPath(Integer.toString(movieId))
                .appendPath("videos")
                .appendQueryParameter(PARAM_KEY, apiKey)
                .build();
        URL tmdbUrl = null;
        try {
            tmdbUrl = new URL(tmdbUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return tmdbUrl;
    }


    public static URL buildReviewQueryUrl(Context context, int movieId, String apiKey) {
        String baseUrl = context.getString(R.string.tmdb_base_url);
        Uri tmdbUri = Uri.parse(baseUrl).buildUpon()
                .appendPath(Integer.toString(movieId))
                .appendPath("reviews")
                .appendQueryParameter(PARAM_KEY, apiKey)
                .build();
        URL tmdbUrl = null;
        try {
            tmdbUrl = new URL(tmdbUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return tmdbUrl;
    }

    public static Uri buildYoutubeUri(String videoKey) {
        String baseUrl = "https://www.youtube.com";
        Uri youtubeUri = Uri.parse(baseUrl).buildUpon()
                .appendPath("watch")
                .appendQueryParameter(VIDEO_KEY, videoKey)
                .build();
        URL youtubeUrl = null;
        try {
            youtubeUrl = new URL(youtubeUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return youtubeUri;
    }



    public static String getResponseFromHttpUrl(URL url) throws IOException {

        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            String response = null;
            if (hasInput) {
                response = scanner.next();
            }
            scanner.close();
            return response;
        } finally {
            urlConnection.disconnect();
        }
    }


    public static boolean deviceIsConnected(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return (activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting());
    }
}
