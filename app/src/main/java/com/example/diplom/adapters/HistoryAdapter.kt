package com.example.diplom.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.diplom.R
import com.example.diplom.models.HistoryEvent
import java.text.SimpleDateFormat
import java.util.*

sealed class HistoryListItem {
    data class DateHeader(val date: Date) : HistoryListItem()
    data class EventItem(val event: HistoryEvent) : HistoryListItem()
}

class AdvancedHistoryAdapter(
    private var items: List<HistoryListItem> = emptyList()
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_DATE_HEADER = 0
        private const val TYPE_EVENT = 1
    }

    private val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    private val headerDateFormat = SimpleDateFormat("dd MMMM yyyy, EEEE", Locale("ru"))

    class DateHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateText: TextView = itemView.findViewById(R.id.date_header_text)
    }

    class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icon: TextView = itemView.findViewById(R.id.event_icon)
        val title: TextView = itemView.findViewById(R.id.event_title)
        val description: TextView = itemView.findViewById(R.id.event_description)
        val time: TextView = itemView.findViewById(R.id.event_time)
        val location: TextView = itemView.findViewById(R.id.event_location)
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is HistoryListItem.DateHeader -> TYPE_DATE_HEADER
            is HistoryListItem.EventItem -> TYPE_EVENT
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_DATE_HEADER -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_date_header, parent, false)
                DateHeaderViewHolder(view)
            }
            TYPE_EVENT -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_history_event, parent, false)
                EventViewHolder(view)
            }
            else -> throw IllegalArgumentException("Unknown view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is HistoryListItem.DateHeader -> {
                val headerHolder = holder as DateHeaderViewHolder
                headerHolder.dateText.text = headerDateFormat.format(item.date)
            }
            is HistoryListItem.EventItem -> {
                val eventHolder = holder as EventViewHolder
                val event = item.event

                eventHolder.icon.text = event.type.icon
                eventHolder.title.text = event.title
                eventHolder.description.text = event.description
                eventHolder.time.text = timeFormat.format(event.timestamp)

                // Показываем локацию если есть
                val locationText = when {
                    event.roomName != null && event.deviceName != null -> "${event.roomName} • ${event.deviceName}"
                    event.roomName != null -> event.roomName
                    event.deviceName != null -> event.deviceName
                    event.userName != null -> "Пользователь: ${event.userName}"
                    else -> ""
                }

                if (locationText.isNotEmpty()) {
                    eventHolder.location.text = locationText
                    eventHolder.location.visibility = View.VISIBLE
                } else {
                    eventHolder.location.visibility = View.GONE
                }

                // Устанавливаем цвет иконки в зависимости от типа события
                val iconColor = when (event.type) {
                    com.example.diplom.models.EventType.RFID_ENTRY -> R.color.success
                    com.example.diplom.models.EventType.RFID_EXIT -> R.color.warning
                    com.example.diplom.models.EventType.LIGHT_ON -> R.color.info
                    com.example.diplom.models.EventType.LIGHT_OFF -> R.color.text_secondary
                    com.example.diplom.models.EventType.SOCKET_ON -> R.color.success
                    com.example.diplom.models.EventType.SOCKET_OFF -> R.color.text_secondary
                    com.example.diplom.models.EventType.ALARM_TRIGGERED -> R.color.warning
                    com.example.diplom.models.EventType.AUTO_OFF_TRIGGERED -> R.color.info
                    com.example.diplom.models.EventType.SYSTEM_EVENT -> R.color.text_secondary
                }

                eventHolder.icon.setTextColor(ContextCompat.getColor(eventHolder.itemView.context, iconColor))
            }
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateItems(events: List<HistoryEvent>) {
        val groupedItems = mutableListOf<HistoryListItem>()

        // Группируем события по датам
        val eventsByDate = events.groupBy { event ->
            val calendar = Calendar.getInstance()
            calendar.time = event.timestamp
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            calendar.time
        }

        // Добавляем заголовки дат и события
        eventsByDate.forEach { (date, events) ->
            groupedItems.add(HistoryListItem.DateHeader(date))
            events.forEach { event ->
                groupedItems.add(HistoryListItem.EventItem(event))
            }
        }

        items = groupedItems
        notifyDataSetChanged()
    }
}