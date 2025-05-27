package com.example.diplom.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.diplom.R
import com.example.diplom.models.Room
import com.example.diplom.models.Socket

class RoomsManagementAdapter(
    private val rooms: List<Room>,
    private val sockets: List<Socket>,
    private val onEditRoom: (Room) -> Unit,
    private val onDeleteRoom: (Room) -> Unit,
    private val onEditSocket: (Socket) -> Unit
) : RecyclerView.Adapter<RoomsManagementAdapter.RoomManagementViewHolder>() {

    class RoomManagementViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val roomName: TextView = itemView.findViewById(R.id.room_name)
        val sensorsStatus: TextView = itemView.findViewById(R.id.sensors_status)
        val socketsContainer: LinearLayout = itemView.findViewById(R.id.sockets_container)
        val editRoomButton: Button = itemView.findViewById(R.id.edit_room_button)
        val deleteRoomButton: Button = itemView.findViewById(R.id.delete_room_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomManagementViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_room_management, parent, false)
        return RoomManagementViewHolder(view)
    }

    override fun onBindViewHolder(holder: RoomManagementViewHolder, position: Int) {
        val room = rooms[position]

        holder.roomName.text = room.name

        // Статус датчиков
        holder.sensorsStatus.text = if (room.temperatureSensorId != null) {
            "Датчики: температура и влажность"
        } else {
            "Датчики: не настроены"
        }

        // Очищаем контейнер розеток
        holder.socketsContainer.removeAllViews()

        // Добавляем розетки
        if (room.socketIds.isEmpty()) {
            val noSocketsText = TextView(holder.itemView.context)
            noSocketsText.text = "Розетки не настроены"
            noSocketsText.textSize = 14f
            noSocketsText.setTextColor(holder.itemView.context.getColor(R.color.text_secondary))
            noSocketsText.setPadding(0, 8, 0, 8)
            holder.socketsContainer.addView(noSocketsText)
        } else {
            room.socketIds.forEach { socketId ->
                val socket = sockets.find { it.id == socketId }
                socket?.let {
                    val socketView = createSocketView(holder, it)
                    holder.socketsContainer.addView(socketView)
                }
            }
        }

        // Кнопки управления
        holder.editRoomButton.setOnClickListener {
            onEditRoom(room)
        }

        holder.deleteRoomButton.setOnClickListener {
            onDeleteRoom(room)
        }
    }

    private fun createSocketView(holder: RoomManagementViewHolder, socket: Socket): View {
        val socketView = LinearLayout(holder.itemView.context)
        socketView.orientation = LinearLayout.HORIZONTAL
        socketView.setPadding(0, 4, 0, 4)

        val socketInfo = TextView(holder.itemView.context)
        socketInfo.text = "${socket.name}: ${socket.deviceName}"
        socketInfo.textSize = 14f
        socketInfo.setTextColor(holder.itemView.context.getColor(R.color.text_primary))
        socketInfo.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)

        val editButton = Button(holder.itemView.context)
        editButton.text = "✏️"
        editButton.textSize = 12f
        editButton.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        editButton.setOnClickListener {
            onEditSocket(socket)
        }

        socketView.addView(socketInfo)
        socketView.addView(editButton)

        return socketView
    }

    override fun getItemCount(): Int = rooms.size
}