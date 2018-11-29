package com.example.mukesh.myapplication;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mukesh.myapplication.POJO.EventTabDataList;

import java.util.List;
import java.util.Objects;


public class EventTabAdapter extends RecyclerView.Adapter<EventTabAdapter.EventTabViewolder> {

    List<EventTabDataList> eventTabDataLists;

    public EventTabAdapter(List<EventTabDataList> eventTabDataLists) {
        this.eventTabDataLists = eventTabDataLists;
    }

    @NonNull
    @Override
    public EventTabAdapter.EventTabViewolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int pos) {

        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.event_tab_item, viewGroup, false);
        return new EventTabViewolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventTabAdapter.EventTabViewolder viewHolder, int pos) {
        String key = eventTabDataLists.get(pos).getKey();
        String val = eventTabDataLists.get(pos).getValue();
        if (Objects.nonNull(val)) {
            viewHolder.key.setText(key);
            viewHolder.value.setText(val);
            if (val.contains("http")) {
                viewHolder.value.setMovementMethod(LinkMovementMethod.getInstance());
                String text = "";
                if (!eventTabDataLists.get(pos).isSeatMapURL()) {
                    text = "<a href='" + val + "'> Ticketmaster </a>";
                } else {
                    text = "<a href='" + val + "'> View Here </a>";
                }
                viewHolder.value.setText(Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT));

                viewHolder.value.setClickable(true);
                viewHolder.value.setAutoLinkMask(Linkify.ALL);
                viewHolder.value.setLinksClickable(true);

            }
        } else {
            viewHolder.key.setVisibility(View.GONE);
            viewHolder.value.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return eventTabDataLists.size();
    }

    public class EventTabViewolder extends RecyclerView.ViewHolder {

        TextView key;
        TextView value;

        public EventTabViewolder(@NonNull View itemView) {
            super(itemView);
            key = itemView.findViewById(R.id.key);
            value = itemView.findViewById(R.id.value);
        }
    }
}

