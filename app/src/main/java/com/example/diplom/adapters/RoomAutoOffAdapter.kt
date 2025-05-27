package com.example.diplom.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Switch
import android.widget.TextView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.diplom.R
import com.example.diplom.models.Room
import com.example.diplom.models.Socket
import com.example.diplom.models.RoomAutoOffSettings

class RoomAutoOffAdapter(
    private val rooms: List<Room>,
    private val sockets: List<Socket>,
    private val onRoomSettingChanged: (RoomAutoOffSettings) -> Unit
) : RecyclerView.Adapter<RoomAutoOffAdapter.RoomAutoOffViewHolder>() {

    private val roomSettings = mutableMapOf<Int, RoomAutoOffSettings>()
    private var globalEnabled = true

    init {
        // Инициализируем настройки для каждой комнаты
        println("RoomAutoOffAdapter: Инициализация для ${rooms.size} комнат")
        rooms.forEach { room ->
            println("Инициализация настроек для комнаты: ${room.name} (ID: ${room.id})")
            roomSettings[room.id] = RoomAutoOffSettings(
                roomId = room.id,
                enabled = true,
                turnOffLights = true,
                turnOffSockets = mutableSetOf()
            )
        }
    }

    class RoomAutoOffViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val roomName: TextView = itemView.findViewById(R.id.room_name)
        val roomEnabledSwitch: Switch = itemView.findViewById(R.id.room_enabled_switch)
        val lightCheckBox: CheckBox = itemView.findViewById(R.id.light_checkbox)
        val socketsContainer: LinearLayout = itemView.findViewById(R.id.sockets_container)
        val socketsLabel: TextView = itemView.findViewById(R.id.sockets_label)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomAutoOffViewHolder {
        println("RoomAutoOffAdapter: onCreateViewHolder вызван")
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_room_auto_off_detailed, parent, false)
        return RoomAutoOffViewHolder(view)
    }

    override fun onBindViewHolder(holder: RoomAutoOffViewHolder, position: Int) {
        println("RoomAutoOffAdapter: onBindViewHolder для позиции $position")
        val room = rooms[position]
        val settings = roomSettings[room.id] ?: return

        println("Привязка комнаты: ${room.name} на позиции $position")

        holder.roomName.text = room.name

        // Настройка главного переключателя комнаты
        holder.roomEnabledSwitch.setOnCheckedChangeListener(null)
        holder.roomEnabledSwitch.isChecked = settings.enabled
        holder.roomEnabledSwitch.setOnCheckedChangeListener { _, isChecked ->
            settings.enabled = isChecked
            updateChildElementsEnabled(holder, settings)
            onRoomSettingChanged(settings)
        }

        // Настройка чекбокса для света
        holder.lightCheckBox.setOnCheckedChangeListener(null)
        holder.lightCheckBox.isChecked = settings.turnOffLights
        holder.lightCheckBox.setOnCheckedChangeListener { _, isChecked ->
            settings.turnOffLights = isChecked
            onRoomSettingChanged(settings)
        }

        // Очищаем контейнер розеток
        holder.socketsContainer.removeAllViews()

        // Добавляем розетки для этой комнаты
        val roomSockets = sockets.filter { socket -> room.socketIds.contains(socket.id) }

        if (roomSockets.isEmpty()) {
            // Если нет розеток, показываем сообщение
            val noSocketsText = TextView(holder.itemView.context)
            noSocketsText.text = "Нет розеток в комнате"
            noSocketsText.textSize = 14f
            noSocketsText.setTextColor(
                ContextCompat.getColor(holder.itemView.context, R.color.text_secondary)
            )
            noSocketsText.setPadding(16, 8, 16, 8)
            holder.socketsContainer.addView(noSocketsText)
            holder.socketsLabel.visibility = View.GONE
        } else {
            holder.socketsLabel.visibility = View.VISIBLE

            roomSockets.forEach { socket ->
                val checkBox = CheckBox(holder.itemView.context)
                checkBox.text = socket.deviceName
                checkBox.textSize = 14f
                checkBox.setTextColor(
                    ContextCompat.getColor(holder.itemView.context, R.color.text_primary)
                )
                checkBox.setPadding(16, 8, 16, 8)

                checkBox.setOnCheckedChangeListener(null)
                checkBox.isChecked = settings.turnOffSockets.contains(socket.id)

                checkBox.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        settings.turnOffSockets.add(socket.id)
                    } else {
                        settings.turnOffSockets.remove(socket.id)
                    }
                    onRoomSettingChanged(settings)
                }

                holder.socketsContainer.addView(checkBox)
            }
        }

        // Обновляем состояние дочерних элементов
        updateChildElementsEnabled(holder, settings)
    }

    private fun updateChildElementsEnabled(holder: RoomAutoOffViewHolder, settings: RoomAutoOffSettings) {
        val enabled = settings.enabled && globalEnabled
        val alpha = if (enabled) 1.0f else 0.5f

        holder.lightCheckBox.isEnabled = enabled
        holder.lightCheckBox.alpha = alpha

        // Обновляем состояние всех чекбоксов розеток
        for (i in 0 until holder.socketsContainer.childCount) {
            val view = holder.socketsContainer.getChildAt(i)
            if (view is CheckBox) {
                view.isEnabled = enabled
                view.alpha = alpha
            } else if (view is TextView) {
                view.alpha = alpha
            }
        }
    }

    fun setGlobalEnabled(enabled: Boolean) {
        globalEnabled = enabled
        notifyDataSetChanged()
    }

    fun getSettings(): Map<Int, RoomAutoOffSettings> = roomSettings.toMap()

    fun updateSettings(newSettings: Map<Int, RoomAutoOffSettings>) {
        roomSettings.clear()
        roomSettings.putAll(newSettings)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        println("RoomAutoOffAdapter: getItemCount() = ${rooms.size}")
        return rooms.size
    }
}