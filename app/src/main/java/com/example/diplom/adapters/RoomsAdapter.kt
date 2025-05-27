package com.example.diplom.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.diplom.R
import com.example.diplom.models.Room
import com.example.diplom.models.TemperatureSensor
import com.example.diplom.models.HumiditySensor
import com.example.diplom.models.Socket

class RoomsAdapter(
    private val rooms: List<Room>,
    private val temperatureSensors: List<TemperatureSensor>,
    private val humiditySensors: List<HumiditySensor>,
    private val sockets: List<Socket>,
    private val onActionClick: (Room, String, Int?) -> Unit
) : RecyclerView.Adapter<RoomsAdapter.RoomViewHolder>() {

    class RoomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val roomName: TextView = itemView.findViewById(R.id.room_name)
        val temperature: TextView = itemView.findViewById(R.id.room_temperature)
        val humidity: TextView = itemView.findViewById(R.id.room_humidity)
        val lightSwitch: Switch = itemView.findViewById(R.id.light_switch)
        val socketsContainer: LinearLayout = itemView.findViewById(R.id.sockets_container)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_room, parent, false)
        return RoomViewHolder(view)
    }

    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
        val room = rooms[position]

        holder.roomName.text = room.name

        // Получаем данные с датчиков
        val temperatureData = getTemperatureForRoom(room)
        val humidityData = getHumidityForRoom(room)

        holder.temperature.text = temperatureData
        holder.humidity.text = humidityData

        // Устанавливаем состояние переключателя света
        holder.lightSwitch.setOnCheckedChangeListener(null)
        holder.lightSwitch.isChecked = room.lightOn
        holder.lightSwitch.setOnCheckedChangeListener { _, _ ->
            onActionClick(room, "light", null)
        }

        // Очищаем контейнер розеток и добавляем розетки для этой комнаты
        holder.socketsContainer.removeAllViews()

        room.socketIds.forEach { socketId ->
            val socket = sockets.find { it.id == socketId }
            socket?.let {
                addSocketView(holder.socketsContainer, room, it)
            }
        }

        // Если нет розеток, показываем сообщение
        if (room.socketIds.isEmpty()) {
            val noSocketsView = LayoutInflater.from(holder.itemView.context)
                .inflate(R.layout.item_no_sockets, holder.socketsContainer, false)
            holder.socketsContainer.addView(noSocketsView)
        }
    }

    private fun addSocketView(container: LinearLayout, room: Room, socket: Socket) {
        val socketView = LayoutInflater.from(container.context)
            .inflate(R.layout.item_socket, container, false)

        val socketName = socketView.findViewById<TextView>(R.id.socket_name)
        val socketSwitch = socketView.findViewById<Switch>(R.id.socket_switch)

        // Показываем название устройства
        socketName.text = socket.deviceName

        socketSwitch.setOnCheckedChangeListener(null)
        socketSwitch.isChecked = socket.isOn
        socketSwitch.setOnCheckedChangeListener { _, _ ->
            onActionClick(room, "socket", socket.id)
        }

        container.addView(socketView)
    }

    private fun getTemperatureForRoom(room: Room): String {
        return if (room.temperatureSensorId != null) {
            val sensor = temperatureSensors.find { it.id == room.temperatureSensorId }
            sensor?.let { "${it.temperature}°C" } ?: "Н/Д"
        } else {
            "Н/Д"
        }
    }

    private fun getHumidityForRoom(room: Room): String {
        return if (room.humiditySensorId != null) {
            val sensor = humiditySensors.find { it.id == room.humiditySensorId }
            sensor?.let { "${it.humidity}%" } ?: "Н/Д"
        } else {
            "Н/Д"
        }
    }

    override fun getItemCount(): Int = rooms.size
}