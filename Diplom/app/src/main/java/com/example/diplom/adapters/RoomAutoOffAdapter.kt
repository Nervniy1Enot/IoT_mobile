package com.example.diplom.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Switch
import android.widget.TextView
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.diplom.R
import com.example.diplom.models.Room
import com.example.diplom.models.Socket
import com.example.diplom.models.RoomAutoOffSettings

class RoomAutoOffAdapter(
    private val rooms: List<Room>,
    private val sockets: List<Socket>,
    private val roomSettings: MutableList<RoomAutoOffSettings>,
    private val onSettingsChanged: (RoomAutoOffSettings) -> Unit
) : RecyclerView.Adapter<RoomAutoOffAdapter.RoomAutoOffViewHolder>() {

    private var globalEnabled = false

    class RoomAutoOffViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val roomName: TextView = itemView.findViewById(R.id.room_name)
        val roomEnabledSwitch: Switch = itemView.findViewById(R.id.room_enabled_switch)
        val lightCheckBox: CheckBox = itemView.findViewById(R.id.light_checkbox)
        val socketsContainer: LinearLayout = itemView.findViewById(R.id.sockets_container)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomAutoOffViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_room_auto_off, parent, false)
        return RoomAutoOffViewHolder(view)
    }

    override fun onBindViewHolder(holder: RoomAutoOffViewHolder, position: Int) {
        val room = rooms[position]
        val settings = roomSettings.find { it.roomId == room.id } ?: return

        holder.roomName.text = room.name

        // Настройка переключателя комнаты
        holder.roomEnabledSwitch.setOnCheckedChangeListener(null)
        holder.roomEnabledSwitch.isChecked = settings.enabled
        holder.roomEnabledSwitch.isEnabled = globalEnabled
        holder.roomEnabledSwitch.setOnCheckedChangeListener { _, isChecked ->
            settings.enabled = isChecked
            updateRoomUI(holder, settings, room)
            onSettingsChanged(settings)
        }

        // Настройка чекбокса света
        holder.lightCheckBox.setOnCheckedChangeListener(null)
        holder.lightCheckBox.isChecked = settings.turnOffLights
        holder.lightCheckBox.setOnCheckedChangeListener { _, isChecked ->
            settings.turnOffLights = isChecked
            onSettingsChanged(settings)
        }

        // Настройка розеток
        setupSocketsForRoom(holder, room, settings)
        updateRoomUI(holder, settings, room)
    }

    private fun setupSocketsForRoom(holder: RoomAutoOffViewHolder, room: Room, settings: RoomAutoOffSettings) {
        holder.socketsContainer.removeAllViews()

        if (room.socketIds.isEmpty()) {
            val noSocketsText = TextView(holder.itemView.context)
            noSocketsText.text = "Нет розеток"
            noSocketsText.textSize = 14f
            noSocketsText.setTextColor(holder.itemView.context.resources.getColor(android.R.color.darker_gray))
            noSocketsText.setPadding(16, 8, 8, 8)
            holder.socketsContainer.addView(noSocketsText)
            return
        }

        room.socketIds.forEach { socketId ->
            val socket = sockets.find { it.id == socketId } ?: return@forEach

            val checkBox = CheckBox(holder.itemView.context)
            checkBox.text = socket.deviceName
            checkBox.textSize = 14f
            checkBox.setTextColor(holder.itemView.context.resources.getColor(android.R.color.black))
            checkBox.setPadding(16, 4, 8, 4)

            checkBox.setOnCheckedChangeListener(null)
            checkBox.isChecked = settings.turnOffSockets.contains(socketId)

            checkBox.setOnCheckedChangeListener { _, isChecked ->
                val updatedSockets = settings.turnOffSockets.toMutableList()
                if (isChecked) {
                    if (!updatedSockets.contains(socketId)) {
                        updatedSockets.add(socketId)
                    }
                } else {
                    updatedSockets.remove(socketId)
                }

                // Обновляем настройки
                val updatedSettings = settings.copy(turnOffSockets = updatedSockets)
                val index = roomSettings.indexOfFirst { it.roomId == room.id }
                if (index != -1) {
                    roomSettings[index] = updatedSettings
                }
                onSettingsChanged(updatedSettings)
            }

            holder.socketsContainer.addView(checkBox)
        }
    }

    private fun updateRoomUI(holder: RoomAutoOffViewHolder, settings: RoomAutoOffSettings, room: Room) {
        val enabled = globalEnabled && settings.enabled

        holder.lightCheckBox.isEnabled = enabled
        holder.lightCheckBox.alpha = if (enabled) 1.0f else 0.5f

        // Обновляем состояние всех чекбоксов розеток
        for (i in 0 until holder.socketsContainer.childCount) {
            val view = holder.socketsContainer.getChildAt(i)
            if (view is CheckBox) {
                view.isEnabled = enabled
                view.alpha = if (enabled) 1.0f else 0.5f
            }
        }
    }

    fun setGlobalEnabled(enabled: Boolean) {
        globalEnabled = enabled
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = rooms.size
}