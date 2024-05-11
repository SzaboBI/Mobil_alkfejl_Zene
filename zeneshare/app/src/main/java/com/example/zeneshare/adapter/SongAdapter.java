package com.example.zeneshare.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zeneshare.R;
import com.example.zeneshare.model.song;

import java.util.ArrayList;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder> implements Filterable {
    private ArrayList<song> songsData;
    private ArrayList<song> allSongsData;
    private Context mContext;
    private SongClickListener listener;
    private int lastPosition = -1;

    public SongAdapter(Context context, ArrayList<song> itemData, SongClickListener listener){
        this.songsData=itemData;
        this.allSongsData=itemData;
        this.mContext = context;
        this.listener = listener;
    }
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.song_list, parent, false),listener);
    }

    @Override
    public void onBindViewHolder(SongAdapter.ViewHolder holder, int position) {
        song currentItem = songsData.get(position);
        holder.bindTo(currentItem);
    }

    @Override
    public int getItemCount() {
        return songsData.size();
    }

    @Override
    public Filter getFilter() {
        return songFilter;
    }
    private Filter songFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<song> filteredList = new ArrayList<>();
            FilterResults filterResults = new FilterResults();
            if (constraint == null || constraint.length()==0) {
                filterResults.count= allSongsData.size();
                filterResults.values=allSongsData;
            }
            else {
                String pattern = constraint.toString().toLowerCase().trim();
                for (int i =0; i<allSongsData.size();i++){
                    if (allSongsData.get(i).getTitle().contains(pattern)){
                        filteredList.add(allSongsData.get(i));
                    } else if (allSongsData.get(i).getAuthor().contains(pattern)) {
                        filteredList.add(allSongsData.get(i));
                    }
                }
                filterResults.count=filterResults.count;
                filterResults.values=filteredList;
            }
            return filterResults;
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            songsData= (ArrayList<song>) results.values;
            notifyDataSetChanged();
        }
    };

    static class ViewHolder extends RecyclerView.ViewHolder{

        private final TextView TVTitle;
        private final TextView TVAuthor;
        private final ImageButton IBdownload;
        public ViewHolder(View itemView, SongClickListener listener) {
            super(itemView);
            TVTitle = itemView.findViewById(R.id.songTitle);
            TVAuthor = itemView.findViewById(R.id.author);
            IBdownload = itemView.findViewById(R.id.playButton);
            IBdownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String id = (String) TVAuthor.getTag();
                    listener.onSongClicked(id);

                }
            });
        }

        public void bindTo(song currentItem) {
            TVTitle.setText(currentItem.getTitle());
            TVAuthor.setTag(currentItem.idGet());
            TVAuthor.setText(currentItem.getAuthor());
            if (currentItem.getDownloaded()){
                IBdownload.setClickable(false);
                IBdownload.setVisibility(View.INVISIBLE);
            }
            else {
                IBdownload.setClickable(true);
                IBdownload.setVisibility(View.VISIBLE);
            }

        }
    };
}
