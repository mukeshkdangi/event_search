package com.example.mukesh.myapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

public class EventDetailsAdaptor extends RecyclerView.Adapter<EventDetailsAdaptor.EventDetailsViewHolder>

{
    private List<EventDetails> eventDetailList;
    private Context context;
    private List<EventDetails> localStorage;
    SharedPreferenceConfig sharedPreferenceConfig;

    public EventDetailsAdaptor(List<EventDetails> eventDetailList, Context context) {
        this.eventDetailList = eventDetailList;
        this.context = context;
        sharedPreferenceConfig = new SharedPreferenceConfig(EventDetailsAdaptor.this.context);
        localStorage = sharedPreferenceConfig.loadSharedPreferencesLogList();
    }

    @NonNull
    @Override
    public EventDetailsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_layout, viewGroup, false);
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

        boolean isAlreadyFav = isThisFavEvent(eventDetail.getEventId());
        viewHolder.favIcon.setImageResource(isAlreadyFav ? R.drawable.heart_fill_red : R.drawable.heart_outline_black);
        eventDetail.setFav(isAlreadyFav);
        viewHolder.favIcon.setTag(new Gson().toJson(eventDetail));
        viewHolder.eventId.setText(eventDetail.getEventId());
    }

    private boolean isThisFavEvent(String eventId) {
        for (int idx = 0; idx < localStorage.size(); idx++) {
            if (localStorage.get(idx).getEventId().equalsIgnoreCase(eventId)) {
                Log.i("Fav found  from local storage ", "Yes ");
                return true;
            }
        }
        return false;
    }

    public void setUpdateChange(List<EventDetails> eventDetails) {
        eventDetailList = eventDetails;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return eventDetailList.size();
    }


    public static class EventDetailsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView eventName;
        public TextView eventVenue;
        public TextView eventDate;
        public ImageView eventIcon;
        public ImageView favIcon;
        public TextView eventId;
        public Context context;
        public List<EventDetails> eventDetails;
        public SharedPreferenceConfig sharedPreferenceConfig;
        public EventDetailsAdaptor eventDetailsAdaptor;


        public EventDetailsViewHolder(@NonNull View itemView, Context context, List<EventDetails> eventDetails, EventDetailsAdaptor eventDetailsAdaptor) {
            super(itemView);
            this.context = context;
            this.eventDetails = eventDetails;
            this.eventDetailsAdaptor = eventDetailsAdaptor;

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

        private void addEventToFav(ImageView img, View view) {
            try {
                EventDetails eventDetailTmp = new Gson().fromJson(img.getTag().toString(), EventDetails.class);
                sharedPreferenceConfig = new SharedPreferenceConfig(EventDetailsViewHolder.this.context);

                if (eventDetailTmp.isFav()) {
                    eventDetailTmp.setFav(false);
                    updateEventDetailsListWithFav(true, eventDetailTmp.getEventId());

                    img.setImageResource(R.drawable.heart_outline_black);
                    sharedPreferenceConfig.removeFromSharedPref(new Gson().toJson(eventDetailTmp));

                    Toast toast = Toast.makeText(this.context, eventDetailTmp.getEventName() + "was removed from favorites",
                            Toast.LENGTH_LONG);

                    TextView text = toast.getView().findViewById(android.R.id.message);
                    text.setBackgroundResource(R.drawable.dialog_bg);
                    toast.show();
                    img.setTag(new Gson().toJson(eventDetailTmp));

                    return;
                }


                img.setImageResource(R.drawable.heart_fill_red);
                eventDetailTmp.setFav(true);
                updateEventDetailsListWithFav(true, eventDetailTmp.getEventId());

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
            }
        }

        public void updateEventDetailsListWithFav(boolean isNewFav, String favEventId) {
            for (int idx = 0; idx < eventDetails.size(); idx++) {
                if (eventDetails.get(idx).getEventId().equals(favEventId)) {
                    if (isNewFav) {
                        eventDetails.get(idx).setFav(true);
                    } else {
                        eventDetails.get(idx).setFav(false);
                    }
                    break;
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
