package david.black.davidpopularmoviess2.main;

import android.support.annotation.NonNull;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import david.black.davidpopularmoviess2.R;
import david.black.davidpopularmoviess2.model.Movie;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import david.black.davidpopularmoviess2.utils.JsonUtils;
import com.squareup.picasso.Picasso;
import org.json.JSONException;
import java.util.List;

public class GridAdapter extends RecyclerView.Adapter<GridAdapter.PosterGridAdapterViewHolder> {
    private static int itemCount = 20;
    private OnClickHandler posterClickHandler;
    private String[] posterUrls = new String[itemCount];
    public Context adapterContext;
    public GridAdapter(Context context, OnClickHandler clickHandler) {
        adapterContext = context;
        posterClickHandler = clickHandler;
    }

    public class PosterGridAdapterViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        public final ImageView posterImgView;

        public PosterGridAdapterViewHolder(View view) {
            super(view);
            posterImgView = (ImageView) view.findViewById(R.id.movie_poster);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            posterClickHandler.onItemClicked(adapterPosition);
        }
    }

    @NonNull
    @Override
    public PosterGridAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutIdForGridItem = R.layout.poster_grid_item;
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForGridItem, parent, false);
        PosterGridAdapterViewHolder viewHolder = new PosterGridAdapterViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PosterGridAdapterViewHolder holder, int position) {
        if (position < posterUrls.length) {
            if (posterUrls[position] != null) {
                Picasso.with(adapterContext)
                        .load(posterUrls[position])
                        .into(holder.posterImgView);
                return;
            }
        }
        Picasso.with(adapterContext)
                .load(R.drawable.ic_blip_blap)
                .into(holder.posterImgView);
    }

    @Override
    public int getItemCount() {
        return itemCount;
    }

    public static void setItemCount(int itemCount) {
        GridAdapter.itemCount = itemCount;
    }

    public interface OnClickHandler {
        void onItemClicked(int position);
    }

    public void setPosterUrls(String json) {
        setItemCount(20);
        posterUrls = new String[getItemCount()];
        for (int i=0; i<itemCount; i++) {
            try {
                String movieJson = JsonUtils.getMovieJson(json, i);
                posterUrls[i] = JsonUtils.buildPosterUrl(movieJson);
            } catch (JSONException e) {
                Log.e("Json Error",e.toString());
            }
        }
        notifyDataSetChanged();
    }

    public void setPosterUrls(List<Movie> movies) {
        setItemCount(movies.size());
        for (int i=0; i<itemCount; i++) {
            posterUrls[i] = movies.get(i).getPosterUrl();
        }
        notifyDataSetChanged();
    }

}
