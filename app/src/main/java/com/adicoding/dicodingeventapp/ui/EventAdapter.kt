package com.adicoding.dicodingeventapp.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.adicoding.dicodingeventapp.R
import com.adicoding.dicodingeventapp.data.response.ListEventsItem
import com.bumptech.glide.Glide

class EventAdapter(private val listEvent: List<ListEventsItem>) : RecyclerView.Adapter<EventAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_event, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val event = listEvent[position]
        holder.bind(event)
    }

    override fun getItemCount(): Int = listEvent.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imgEvent: ImageView = itemView.findViewById(R.id.img_event)
        private val tvEventName: TextView = itemView.findViewById(R.id.tv_event_name)

        fun bind(event: ListEventsItem) {
            Glide.with(itemView.context)
                .load(event.imageLogo ?: event.mediaCover)
                .into(imgEvent)
            tvEventName.text = event.name
        }
    }
}