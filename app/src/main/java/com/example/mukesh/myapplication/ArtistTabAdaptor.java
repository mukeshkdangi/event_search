package com.example.mukesh.myapplication;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.mukesh.myapplication.POJO.ArtistTabAdaptorPojo;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

public class ArtistTabAdaptor extends RecyclerView.Adapter<ArtistTabAdaptor.ArtistTabDetailsViewHolder>

{
    private List<ArtistTabAdaptorPojo> artistTabAdaptorPojosList;
    private Context context;

    public ArtistTabAdaptor(List<ArtistTabAdaptorPojo> eventDetailList, Context context) {
        this.artistTabAdaptorPojosList = eventDetailList;
        this.context = context;
    }

    @NonNull
    @Override
    public ArtistTabAdaptor.ArtistTabDetailsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.artist_tab_items, viewGroup, false);
        ArtistTabAdaptor.ArtistTabDetailsViewHolder eventDetailsViewHolder = new ArtistTabAdaptor.ArtistTabDetailsViewHolder(view, context, artistTabAdaptorPojosList, this);
        return eventDetailsViewHolder;
    }

    @Override
    public void onBindViewHolder(ArtistTabAdaptor.ArtistTabDetailsViewHolder viewHolder, int position) {

        ArtistTabAdaptorPojo artistTabInfo = artistTabAdaptorPojosList.get(position);

        if (Objects.nonNull(artistTabInfo.getHeading())) {
            viewHolder.heading.setText(artistTabInfo.getHeading());
            viewHolder.nameRowKey.setVisibility(View.GONE);
        } else {
            viewHolder.heading.setVisibility(View.GONE);
            viewHolder.nameRowValue.setVisibility(View.GONE);
        }
        if (Objects.nonNull(artistTabInfo.getFollowers())) {
            viewHolder.follower.setText(artistTabInfo.getFollowers());
        } else {
            viewHolder.follower.setVisibility(View.GONE);
            viewHolder.followerKey.setVisibility(View.GONE);
        }

        if (Objects.nonNull(artistTabInfo.getPopularity())) {
            viewHolder.popularity.setText(artistTabInfo.getPopularity());
        } else {
            viewHolder.popularity.setVisibility(View.GONE);
            viewHolder.popularityKey.setVisibility(View.GONE);
        }

        if (Objects.nonNull(artistTabInfo.getCheckAtUrl())) {
            viewHolder.nameRowValue.setText(artistTabInfo.getHeading());
            viewHolder.nameRowKey.setVisibility(View.VISIBLE);

            viewHolder.checkAt.setMovementMethod(LinkMovementMethod.getInstance());
            String text = "<a style='cursor:pointer;' href='" + artistTabInfo.getCheckAtUrl() + "'> Spotify </a>";
            viewHolder.checkAt.setText(Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT));
            viewHolder.checkAt.setClickable(true);
            viewHolder.checkAt.setAutoLinkMask(Linkify.ALL);
            viewHolder.checkAt.setLinksClickable(true);
        } else {
            viewHolder.checkAt.setVisibility(View.GONE);
            viewHolder.checkAtKey.setVisibility(View.GONE);
        }


        if (artistTabInfo.getImages().size() > 0)
             Glide.with(viewHolder.context).load(artistTabInfo.getImages().get(0)). into(viewHolder.image11);

        else
            viewHolder.image11.setVisibility(View.GONE);
        if (artistTabInfo.getImages().size() > 1)
            Glide.with(viewHolder.context).load(artistTabInfo.getImages().get(1)).into(viewHolder.image12);
        else
            viewHolder.image12.setVisibility(View.GONE);
        if (artistTabInfo.getImages().size() > 2)
            Glide.with(viewHolder.context).load(artistTabInfo.getImages().get(2)).into(viewHolder.image13);
        else
            viewHolder.image13.setVisibility(View.GONE);
        if (artistTabInfo.getImages().size() > 3)
            Glide.with(viewHolder.context).load(artistTabInfo.getImages().get(3)).into(viewHolder.image14);
        else
            viewHolder.image14.setVisibility(View.GONE);
        if (artistTabInfo.getImages().size() > 4)
            Glide.with(viewHolder.context).load(artistTabInfo.getImages().get(4)).into(viewHolder.image15);
        else
            viewHolder.image15.setVisibility(View.GONE);

        if (artistTabInfo.getImages().size() > 5)
            Glide.with(viewHolder.context).load(artistTabInfo.getImages().get(5)).into(viewHolder.image16);
        else
            viewHolder.image16.setVisibility(View.GONE);
        if (artistTabInfo.getImages().size() > 6)
            Glide.with(viewHolder.context).load(artistTabInfo.getImages().get(6)).into(viewHolder.image17);
        else
            viewHolder.image17.setVisibility(View.GONE);
        if (artistTabInfo.getImages().size() > 7)
            Glide.with(viewHolder.context).load(artistTabInfo.getImages().get(7)).into(viewHolder.image18);
        else
            viewHolder.image18.setVisibility(View.GONE);

        viewHolder.image19.setVisibility(View.GONE);

    }


    @Override
    public int getItemCount() {
        return artistTabAdaptorPojosList.size();
    }

    public static class ArtistTabDetailsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        Context context;

        TextView heading;
        TextView nameRowValue;
        TextView follower;
        TextView popularity;
        TextView checkAt;

        LinearLayout nameRowKey;
        LinearLayout followerKey;
        LinearLayout popularityKey;
        LinearLayout checkAtKey;

        ImageView image11;
        ImageView image12;
        ImageView image13;
        ImageView image14;

        ImageView image15;
        ImageView image16;
        ImageView image17;
        ImageView image18;
        ImageView image19;


        public ArtistTabDetailsViewHolder(@NonNull View itemView, Context context, List<ArtistTabAdaptorPojo> artistTabAdaptorPojosList, ArtistTabAdaptor artistTabAdaptor) {
            super(itemView);
            this.context = context;
            heading = itemView.findViewById(R.id.artist_heading_1);
            nameRowValue = itemView.findViewById(R.id.name_row_value_1);
            follower = itemView.findViewById(R.id.follower_row_value_1);
            popularity = itemView.findViewById(R.id.popularity_row_value_1);
            checkAt = itemView.findViewById(R.id.checkat_row_key_value_1);

            image11 = itemView.findViewById(R.id.image_1_1);
            image12 = itemView.findViewById(R.id.image_1_2);
            image13 = itemView.findViewById(R.id.image_1_3);
            image14 = itemView.findViewById(R.id.image_1_4);
            image15 = itemView.findViewById(R.id.image_1_5);
            image16 = itemView.findViewById(R.id.image_1_6);
            image17 = itemView.findViewById(R.id.image_1_7);
            image18 = itemView.findViewById(R.id.image_1_8);
            image19 = itemView.findViewById(R.id.image_1_9);

            nameRowKey = itemView.findViewById(R.id.nameKey);
            followerKey = itemView.findViewById(R.id.followerKey);
            popularityKey = itemView.findViewById(R.id.popularityKey);
            checkAtKey = itemView.findViewById(R.id.checkAtKey);

        }

        @Override
        public void onClick(View v) {

        }
    }
}

