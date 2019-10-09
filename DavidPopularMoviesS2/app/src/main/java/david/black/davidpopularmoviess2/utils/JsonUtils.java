package david.black.davidpopularmoviess2.utils;

import david.black.davidpopularmoviess2.model.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonUtils {


    public static Movie parseMovieJson(String movieJson) throws JSONException {
        if (movieJson != null) {
            Movie movie = new Movie();
            JSONObject movieJsonObj = new JSONObject(movieJson);
            movie.setId(movieJsonObj.getInt("id"));
            movie.setTitle(movieJsonObj.getString("title"));
            String ratingString = movieJsonObj.getString("vote_average");
            float ratingFloat = Float.parseFloat(ratingString);
            movie.setRating(ratingFloat);
            movie.setReleaseDate(movieJsonObj.getString("release_date"));
            movie.setSynopsis(movieJsonObj.getString("overview"));
            movie.setPosterUrl("http://image.tmdb.org/t/p/w185" + movieJsonObj.getString("poster_path"));
            return movie;
        } else {
            throw new JSONException("JSON error. parseMovieJson issue. ");
        }
    }

    public static String getMovieJson(String tmdbQueryJson, int index) throws JSONException {
        if (tmdbQueryJson != null) {
            JSONObject queryJsonObj = new JSONObject(tmdbQueryJson);
            JSONArray movies = queryJsonObj.getJSONArray("results");
            return movies.getString(index);
        } else {
            throw new JSONException("JSON error. getMovieJson issue.");
        }
    }

    public static String buildPosterUrl(String movieJson) throws JSONException {
        JSONObject movieJsonObj = new JSONObject(movieJson);
        String posterPath = movieJsonObj.getString("poster_path");
        return "http://image.tmdb.org/t/p/w185" + posterPath;
    }

    public static String getTrailerYoutubeKey(String trailerQueryJson) throws JSONException {
        JSONObject trailersJsonObj = new JSONObject(trailerQueryJson);
        JSONArray videosArray = trailersJsonObj.getJSONArray("results");
        JSONObject videoJsonObj = videosArray.getJSONObject(0);
        return videoJsonObj.getString("key");
    }

    public static String getReviewJson(String reviewsJson, int index) throws JSONException {
        JSONObject reviewsJsonObj = new JSONObject(reviewsJson);
        JSONArray reviewsArray = reviewsJsonObj.getJSONArray("results");
        JSONObject singleReviewJsonObject = reviewsArray.getJSONObject(index);
        return singleReviewJsonObject.toString();
    }

    public static String getAuthor(String reviewJson) throws JSONException {
        JSONObject reviewJsonObj = new JSONObject(reviewJson);
        return reviewJsonObj.getString("author");
    }

    public static String getReview(String reviewJson) throws JSONException {
        JSONObject reviewJsonObj = new JSONObject(reviewJson);
        return reviewJsonObj.getString("content");
    }

    public static int getReviewCount(String reviewsJson) throws JSONException {
        JSONObject reviewsJsonObj = new JSONObject(reviewsJson);
        return reviewsJsonObj.getInt("total_results");
    }
}

