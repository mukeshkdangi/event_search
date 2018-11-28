package com.example.mukesh.myapplication;


import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mukesh.myapplication.Activity.EventMoreDetails;
import com.example.mukesh.myapplication.POJO.EventDetails;
import com.example.mukesh.myapplication.Storage.SharedPreferenceConfig;
import com.google.gson.Gson;

import java.util.List;
import java.util.Objects;

public class FavEventDetailsAdaptor extends RecyclerView.Adapter<FavEventDetailsAdaptor.EventDetailsViewHolder>

{
    private List<EventDetails> eventDetailList;
    private Context context;
    public SharedPreferenceConfig sharedPreferenceConfig;
    public View view;

    public FavEventDetailsAdaptor(List<EventDetails> eventDetailList, Context context) {
        this.eventDetailList = eventDetailList;
        this.context = context;
        sharedPreferenceConfig = new SharedPreferenceConfig(FavEventDetailsAdaptor.this.context);
    }

    @NonNull
    @Override
    public EventDetailsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fav_item_layout, viewGroup, false);
        view.findViewById(R.id.no_fav).setVisibility(View.GONE);
        EventDetailsViewHolder eventDetailsViewHolder = new EventDetailsViewHolder(view, context, eventDetailList, this);
        return eventDetailsViewHolder;
    }

    @Override
    public void onBindViewHolder(EventDetailsViewHolder viewHolder, int position) {

        EventDetails eventDetail = eventDetailList.get(position);
        viewHolder.eventName.setText(eventDetail.getEventName());
        viewHolder.eventVenue.setText(eventDetail.getEventVenue());

        if (eventDetail.getEventType().contains("Music")) {
            viewHolder.eventIcon.setImageResource(R.drawable.music_icon);
        } else if (eventDetail.getEventType().contains("Sport")) {
            viewHolder.eventIcon.setImageResource(R.drawable.sport_icon);
        } else if (eventDetail.getEventType().contains("Art")) {
            viewHolder.eventIcon.setImageResource(R.drawable.art_icon);
        } else if (eventDetail.getEventType().contains("Film")) {
            viewHolder.eventIcon.setImageResource(R.drawable.film_icon);
        } else if (eventDetail.getEventType().contains("Miscellaneous")) {
            viewHolder.eventIcon.setImageResource(R.drawable.miscellaneous_icon);
        }

        viewHolder.eventDate.setText(eventDetail.getEventDate());

        viewHolder.favIcon.setImageResource(R.drawable.heart_fill_red);
        eventDetail.setFav(true);
        viewHolder.favIcon.setTag(new Gson().toJson(eventDetail));
        viewHolder.eventId.setText(eventDetail.getEventId());
    }

    public void setUpdateChange(List<EventDetails> eventDetails) {
        eventDetailList = eventDetails;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }


    public void removeItem(int position) {
        eventDetailList.remove(position);

        notifyItemRemoved(position);
    }

    @Override
    public int getItemCount() {
        return eventDetailList.size();
    }

    public class EventDetailsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView eventName;
        TextView eventVenue;
        TextView eventDate;
        ImageView eventIcon;
        ImageView favIcon;
        TextView eventId;
        Context context;
        List<EventDetails> eventDetails;
        SharedPreferenceConfig sharedPreferenceConfig;
        private FavEventDetailsAdaptor favEventDetailsAdaptor;


        public EventDetailsViewHolder(@NonNull View itemView, Context context, List<EventDetails> eventDetails, FavEventDetailsAdaptor favEventDetailsAdaptor) {
            super(itemView);
            this.context = context;
            this.eventDetails = eventDetails;
            this.favEventDetailsAdaptor = favEventDetailsAdaptor;

            eventName = itemView.findViewById(R.id.event_name);
            eventVenue = itemView.findViewById(R.id.event_venue);
            eventDate = itemView.findViewById(R.id.event_date);

            eventIcon = itemView.findViewById(R.id.icon);
            favIcon = itemView.findViewById(R.id.favicon);
            eventId = itemView.findViewById(R.id.event_id);
            itemView.setOnClickListener(this);


            final ImageView img = itemView.findViewById(R.id.favicon);
            img.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    addEventToFav(img, view);

                }
            });

        }

        private void addEventToFav(ImageView img, View view2) {
            try {
                EventDetails eventDetailTmp = new Gson().fromJson(img.getTag().toString(), EventDetails.class);
                sharedPreferenceConfig = new SharedPreferenceConfig(EventDetailsViewHolder.this.context);

                if (eventDetailTmp.isFav()) {
                    favEventDetailsAdaptor.removeItem(getAdapterPosition());
                    eventDetailTmp.setFav(false);
                    img.setImageResource(R.drawable.heart_outline_black);
                    sharedPreferenceConfig.removeFromSharedPref(new Gson().toJson(eventDetailTmp));
                    return;
                }

                img.setImageResource(R.drawable.heart_fill_red);
                eventDetailTmp.setFav(true);
                sharedPreferenceConfig.saveSharedPreferencesLogList(new Gson().toJson(eventDetailTmp));

                Toast toast = Toast.makeText(this.context, eventDetailTmp.getEventName() + "was added to favorites",
                        Toast.LENGTH_LONG);
                TextView text = toast.getView().findViewById(android.R.id.message);
                text.setBackgroundColor(PorterDuff.Mode.SRC.ordinal());
                toast.show();
                img.setTag(new Gson().toJson(eventDetailTmp));
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (Objects.nonNull(view) && (Objects.isNull(eventDetailList) || eventDetailList.size() == 0)) {
                    view.findViewById(R.id.no_fav).setVisibility(View.VISIBLE);
                } else {
                    view.findViewById(R.id.no_fav).setVisibility(View.GONE);
                }
            }
        }


        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context, EventMoreDetails.class);
            intent.putExtra("eventDetails", new Gson().toJson(eventDetails.get(getAdapterPosition())));
            context.startActivity(intent);

        }
    }
}

