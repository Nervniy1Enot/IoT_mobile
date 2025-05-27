package com.example.diplom.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.diplom.R
import com.example.diplom.models.Room
import com.example.diplom.models.TemperatureSensor
import com.example.diplom.models.HumiditySensor
import com.example.diplom.models.Socket
import com.example.diplom.adapters.RoomsAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager

class HomeFragment : Fragment() {

    private lateinit var homeStatusText: TextView
    private lateinit var roomsRecyclerView: RecyclerView
    private lateinit var roomsAdapter: RoomsAdapter
    private val rooms = mutableListOf<Room>()
    private val temperatureSensors = mutableListOf<TemperatureSensor>()
    private val humiditySensors = mutableListOf<HumiditySensor>()
    private val sockets = mutableListOf<Socket>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        homeStatusText = view.findViewById(R.id.home_status_text)
        roomsRecyclerView = view.findViewById(R.id.rooms_recycler_view)

        setupRecyclerView()
        setupMockData()
        updateHomeStatus(true) // true = дома, false = не дома
    }

    private fun setupRecyclerView() {
        roomsAdapter = RoomsAdapter(
            rooms = rooms,
            temperatureSensors = temperatureSensors,
            humiditySensors = humiditySensors,
            sockets = sockets,
            onActionClick = { room: Room, action: String, socketId: Int? ->
                handleRoomAction(room, action, socketId)
            },
            onSocketNameChanged = { socket: Socket, newName: String ->
                handleSocketNameChanged(socket, newName)
            }
        )
        roomsRecyclerView.layoutManager = LinearLayoutManager(context)
        roomsRecyclerView.adapter = roomsAdapter
    }

    private fun setupMockData() {
        // Инициализируем датчики температуры
        temperatureSensors.clear()
        temperatureSensors.addAll(listOf(
            TemperatureSensor(1, "Датчик температуры #1", 22.5f),
            TemperatureSensor(2, "Датчик температуры #2", 20.0f),
            TemperatureSensor(3, "Датчик температуры #3", 24.0f)
        ))

        // Инициализируем датчики влажности
        humiditySensors.clear()
        humiditySensors.addAll(listOf(
            HumiditySensor(1, "Датчик влажности #1", 45f),
            HumiditySensor(2, "Датчик влажности #2", 42f),
            HumiditySensor(3, "Датчик влажности #3", 50f)
        ))

        // Инициализируем розетки с названиями устройств
        sockets.clear()
        sockets.addAll(listOf(
            Socket(1, "Розетка #1", "Телевизор", true),
            Socket(2, "Розетка #2", "Торшер", false),
            Socket(3, "Розетка #3", "Зарядка телефона", true),
            Socket(4, "Розетка #4", "Чайник", false),
            Socket(5, "Розетка #5", "Микроволновка", true),
            Socket(6, "Розетка #6", "Холодильник", true)
        ))

        // Инициализируем комнаты с привязанными датчиками и розетками
        rooms.clear()
        rooms.addAll(listOf(
            Room(1, "Гостиная", 1, 1, true, listOf(1, 2)),     // Телевизор, Торшер
            Room(2, "Спальня", 2, 2, false, listOf(3)),        // Зарядка телефона
            Room(3, "Кухня", 3, 3, true, listOf(4, 5, 6)),     // Чайник, Микроволновка, Холодильник
            Room(4, "Ванная", null, null, false, emptyList())  // Без датчиков и розеток
        ))
        roomsAdapter.notifyDataSetChanged()
    }

    private fun updateHomeStatus(isHome: Boolean) {
        if (isHome) {
            homeStatusText.text = "Статус: Дома"
            homeStatusText.setTextColor(resources.getColor(android.R.color.holo_green_dark))
        } else {
            homeStatusText.text = "Статус: Вне дома"
            homeStatusText.setTextColor(resources.getColor(android.R.color.holo_orange_dark))
        }
    }

    private fun handleRoomAction(room: Room, action: String, socketId: Int? = null) {
        when (action) {
            "light" -> {
                room.lightOn = !room.lightOn
                roomsAdapter.notifyDataSetChanged()
            }
            "socket" -> {
                socketId?.let { id ->
                    val socket = sockets.find { it.id == id }
                    socket?.let {
                        it.isOn = !it.isOn
                        roomsAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    private fun handleSocketNameChanged(socket: Socket, newName: String) {
        // Здесь можно сохранить изменение в базу данных или SharedPreferences
        println("Название устройства изменено: ${socket.name} -> $newName")

        // Показываем Toast для подтверждения
        Toast.makeText(context, "Название изменено на: $newName", Toast.LENGTH_SHORT).show()
    }
}