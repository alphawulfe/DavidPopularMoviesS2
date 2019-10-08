package david.black.davidpopularmoviess2.reviews;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import david.black.davidpopularmoviess2.R;
import david.black.davidpopularmoviess2.model.MovieReview;
import david.black.davidpopularmoviess2.utils.JsonUtils;

import org.json.JSONException;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewAdapterViewHolder> {
    private int itemCount;
    private MovieReview[] reviewList = new MovieReview[0];

    @NonNull
    @Override
    public ReviewAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutIdForReviewItem = R.layout.review_item;
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForReviewItem, parent, false);
        ReviewAdapterViewHolder viewHolder = new ReviewAdapterViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewAdapterViewHolder holder, int position) {
        holder.mAuthorTextView.setText(reviewList[position].getAuthor());
        holder.mReviewTextView.setText(reviewList[position].getReview());
    }

    @Override
    public int getItemCount() {
        return itemCount;
    }

    public class ReviewAdapterViewHolder extends RecyclerView.ViewHolder {
        public final TextView mAuthorTextView, mReviewTextView;
        public ReviewAdapterViewHolder(View view) {
            super(view);
            mAuthorTextView = view.findViewById(R.id.author_tv);
            mReviewTextView = view.findViewById(R.id.review_tv);
        }
    }


    public void setReviews(String reviewsJson) {
        try {
            itemCount = JsonUtils.getReviewCount(reviewsJson);
            reviewList = new MovieReview[itemCount];
            for (int i=0; i<itemCount; i++) {
                String singleReviewJson = JsonUtils.getReviewJson(reviewsJson, i);
                reviewList[i] = new MovieReview(singleReviewJson);
            }
        } catch (JSONException e) {
            Log.e("JSON error, setReviews",e.toString());
        }
        notifyDataSetChanged();
    }
}
