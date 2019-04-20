package com.luminous.mpartner.gallery;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.exoplayer2.Player;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;
import com.luminous.mpartner.R;
import com.luminous.mpartner.databinding.VideoItemRowLayoutBinding;
import com.luminous.mpartner.network.entities.GalleryDatum;
import com.luminous.mpartner.utilities.CommonUtility;

import java.util.List;


public class GalleryRecyclerViewAdapter extends RecyclerView.Adapter<GalleryRecyclerViewAdapter.VideoItemRowHolder> {

    private Context context;
    private List<GalleryDatum> galleryDatumList;
    private Player player;

    public GalleryRecyclerViewAdapter(Context context, List<GalleryDatum> galleryDatumList) {
        this.context = context;
        this.galleryDatumList = galleryDatumList;
    }

    @NonNull
    @Override
    public VideoItemRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        VideoItemRowLayoutBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.video_item_row_layout, parent, false);
        return new VideoItemRowHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final VideoItemRowHolder holder, int position) {

        final YouTubeThumbnailLoader.OnThumbnailLoadedListener onThumbnailLoadedListener = new YouTubeThumbnailLoader.OnThumbnailLoadedListener() {
            @Override
            public void onThumbnailError(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader.ErrorReason errorReason) {

            }

            @Override
            public void onThumbnailLoaded(YouTubeThumbnailView youTubeThumbnailView, String s) {
                youTubeThumbnailView.setVisibility(View.VISIBLE);
                holder.binding.relativeLayoutOverYoutubeThumbnail.setVisibility(View.VISIBLE);
            }
        };

        holder.binding.youtubeThumbnail.initialize("AIzaSyC2R87w6MbpTPDXQdjoOnT6kDmSOMZVD8w", new YouTubeThumbnailView.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader youTubeThumbnailLoader) {

                youTubeThumbnailLoader.setVideo(getVideoKey(galleryDatumList.get(position).getGalleryVideoUrl()));
                youTubeThumbnailLoader.setOnThumbnailLoadedListener(onThumbnailLoadedListener);
            }

            @Override
            public void onInitializationFailure(YouTubeThumbnailView youTubeThumbnailView, YouTubeInitializationResult youTubeInitializationResult) {
                //write something for failure
            }
        });

        holder.binding.txtVideoName.setText(galleryDatumList.get(position).getGalleryVideoName());

        holder.binding.ivShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(galleryDatumList.get(position).getGalleryVideoUrl())) {
                    CommonUtility.shareDownloadLink(galleryDatumList.get(position).getGalleryVideoUrl(), context);
                }
            }
        });

    }

    private String getVideoKey(String url) {
        String videoUrl = url;
        if (url.contains("v=")) {
            String[] splitString = url.split("v=");
            if (splitString[1].contains("&")) {
                String[] splitStringNew = splitString[1].split("&");
                videoUrl = splitStringNew[0];
            } else
                videoUrl = splitString[1];
        }
        return videoUrl;
    }

    @Override
    public int getItemCount() {
        return galleryDatumList == null ? 0 : galleryDatumList.size();
    }

    class VideoItemRowHolder extends RecyclerView.ViewHolder {
        VideoItemRowLayoutBinding binding;

        VideoItemRowHolder(VideoItemRowLayoutBinding itemBinding) {
            super(itemBinding.getRoot());
            binding = itemBinding;

            binding.btnYoutubePlayer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = YouTubeStandalonePlayer.createVideoIntent((Activity) context, "AIzaSyC2R87w6MbpTPDXQdjoOnT6kDmSOMZVD8w", getVideoKey(galleryDatumList.get(getLayoutPosition()).getGalleryVideoUrl()));
                    context.startActivity(intent);
                }
            });

        }

    }
}

