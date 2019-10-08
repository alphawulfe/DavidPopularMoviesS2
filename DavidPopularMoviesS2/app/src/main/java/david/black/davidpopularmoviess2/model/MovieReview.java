package david.black.davidpopularmoviess2.model;

import android.util.Log;
import david.black.davidpopularmoviess2.utils.JsonUtils;
import org.json.JSONException;

public class MovieReview {

    private String
            author = "",
            review = "";

    public MovieReview(String reviewJson) {
        if (reviewJson != null) {
            try {
                this.author = JsonUtils.getAuthor(reviewJson);
                this.review = JsonUtils.getReview(reviewJson);
            } catch (JSONException e) {
                Log.e("JSON Error MovieReview", e.toString());
            }

        }
    }
    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }



}
