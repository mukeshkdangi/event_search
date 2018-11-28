package com.example.mukesh.myapplication;

import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mukesh.myapplication.POJO.UpcomingEventInfo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
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

        String text = "<a href='" + upcomingEventInfos.get(pos).getUrl() + "'> " + upcomingEventInfos.get(pos).getEventName() + " </a>";

        upComingEventTabViewolder.eventName.setMovementMethod(LinkMovementMethod.getInstance());
        upComingEventTabViewolder.eventName.setText(Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT));
        upComingEventTabViewolder.eventName.setAutoLinkMask(Linkify.ALL);
        upComingEventTabViewolder.eventName.setPaintFlags(upComingEventTabViewolder.eventName.getPaintFlags() & (~Paint.UNDERLINE_TEXT_FLAG));
        upComingEventTabViewolder.eventName.setPaintFlags(0);

        if (Objects.nonNull(upcomingEventInfos.get(pos).getArtistName())) {
            upComingEventTabViewolder.artistName.setText(upcomingEventInfos.get(pos).getArtistName());
        } else {
            upComingEventTabViewolder.artistName.setVisibility(View.GONE);
        }
        if (Objects.nonNull(upcomingEventInfos.get(pos).getDate()) &&
                !upcomingEventInfos.get(pos).getDate().equalsIgnoreCase("null")) {
            SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

            try {
                Date time = ft.parse(upcomingEventInfos.get(pos).getDate());
                ft.applyPattern("MMM dd, yyyy hh:mm:ss");
                upComingEventTabViewolder.date.setText(ft.format(time));
            } catch (ParseException e) {
                try {
                    ft = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                    Date time = ft.parse(upcomingEventInfos.get(pos).getDate());
                    ft.applyPattern("MMM dd, yyyy");
                    upComingEventTabViewolder.date.setText(ft.format(time));
                } catch (Exception e2) {
                    upComingEventTabViewolder.date.setVisibility(View.GONE);
                }
            }
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

