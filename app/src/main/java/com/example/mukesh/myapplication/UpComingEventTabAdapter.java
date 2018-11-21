package com.example.mukesh.myapplication;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mukesh.myapplication.POJO.UpcomingEventInfo;

import java.util.List;
import java.util.Objects;

public class UpComingEventTabAdapter extends RecyclerView.Adapter<UpComingEventTabAdapter.UpComingEventTabViewolder> {

    List<UpcomingEventInfo> upcomingEventInfos;

    public UpComingEventTabAdapter(List<UpcomingEventInfo> upcomingEventInfos) {
        this.upcomingEventInfos = upcomingEventInfos;
    }

    @NonNull
    @Override
    public UpComingEventTabViewolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.upcoming_event_tab_item, viewGroup, false);
        return new UpComingEventTabAdapter.UpComingEventTabViewolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UpComingEventTabViewolder upComingEventTabViewolder, int pos) {
        upComingEventTabViewolder.eventName.setText(upcomingEventInfos.get(pos).getEventName());
        if (Objects.nonNull(upcomingEventInfos.get(pos).getArtistName())) {
            upComingEventTabViewolder.artistName.setText(upcomingEventInfos.get(pos).getArtistName());
        } else {
            upComingEventTabViewolder.artistName.setVisibility(View.GONE);
        }
        if (Objects.nonNull(upcomingEventInfos.get(pos).getDate())) {
            upComingEventTabViewolder.date.setText(upcomingEventInfos.get(pos).getDate());
        } else {
            upComingEventTabViewolder.date.setVisibility(View.GONE);
        }
        upComingEventTabViewolder.type.setText(upcomingEventInfos.get(pos).getType());
    }

    @Override
    public int getItemCount() {
        return upcomingEventInfos.size();
    }

    class UpComingEventTabViewolder extends RecyclerView.ViewHolder {

        TextView eventName;
        TextView artistName;
        TextView date;
        TextView type;

        public UpComingEventTabViewolder(@NonNull View itemView) {
            super(itemView);

            eventName = itemView.findViewById(R.id.eventName);
            artistName = itemView.findViewById(R.id.artistName);
            date = itemView.findViewById(R.id.time);
            type = itemView.findViewById(R.id.type);

        }
    }
}

