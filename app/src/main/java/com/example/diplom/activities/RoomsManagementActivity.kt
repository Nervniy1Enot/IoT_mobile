package com.example.diplom.activities

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.diplom.R
import com.example.diplom.adapters.RoomsManagementAdapter
import com.example.diplom.models.Room
import com.example.diplom.models.Socket
import com.google.android.material.floatingactionbutton.FloatingActionButton

class RoomsManagementActivity : AppCompatActivity() {

    private lateinit var roomsRecyclerView: RecyclerView
    private lateinit var addRoomFab: FloatingActionButton
    private lateinit var roomsAdapter: RoomsManagementAdapter

    private val rooms = mutableListOf<Room>()
    private val sockets = mutableListOf<Socket>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rooms_management)

        initViews()
        setupMockData()
        setupRecyclerView()
        setupToolbar()
    }

    private fun initViews() {
        roomsRecyclerView = findViewById(R.id.rooms_recycler_view)
        addRoomFab = findViewById(R.id.add_room_fab)

        addRoomFab.setOnClickListener {
            showAddRoomDialog()
        }
    }

    private fun setupToolbar() {
        supportActionBar?.apply {
            title = "Управление комнатами"
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun setupMockData() {
        // Загружаем существующие данные
        sockets.clear()
        sockets.addAll(listOf(
            Socket(1, "Розетка #1", "Телевизор", true),
            Socket(2, "Розетка #2", "Торшер", false),
            Socket(3, "Розетка #3", "Зарядка телефона", true),
            Socket(4, "Розетка #4", "Чайник", false),
            Socket(5, "Розетка #5", "Микроволновка", true),
            Socket(6, "Розетка #6", "Холодильник", true)
        ))

        rooms.clear()
        rooms.addAll(listOf(
            Room(
                id = 1,
                name = "Гостиная",
                temperatureSensorId = 1,
                humiditySensorId = 1,
                lightOn = true,
                socketIds = listOf(1, 2)
            ),
            Room(
                id = 2,
                name = "Спальня",
                temperatureSensorId = 2,
                humiditySensorId = 2,
                lightOn = false,
                socketIds = listOf(3)
            ),
            Room(
                id = 3,
                name = "Кухня",
                temperatureSensorId = 3,
                humiditySensorId = 3,
                lightOn = true,
                socketIds = listOf(4, 5, 6)
            ),
            Room(
                id = 4,
                name = "Ванная",
                temperatureSensorId = null,
                humiditySensorId = null,
                lightOn = false,
                socketIds = emptyList()
            )
        ))
    }

    private fun setupRecyclerView() {
        roomsAdapter = RoomsManagementAdapter(
            rooms = rooms,
            sockets = sockets,
            onEditRoom = { room -> showEditRoomDialog(room) },
            onDeleteRoom = { room -> showDeleteRoomDialog(room) },
            onEditSocket = { socket -> showEditSocketDialog(socket) }
        )

        roomsRecyclerView.layoutManager = LinearLayoutManager(this)
        roomsRecyclerView.adapter = roomsAdapter
    }

    private fun showAddRoomDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_edit_room, null)
        val roomNameEdit = dialogView.findViewById<EditText>(R.id.room_name_edit)
        val hasSensorsCheckbox = dialogView.findViewById<CheckBox>(R.id.has_sensors_checkbox)
        val socketsContainer = dialogView.findViewById<LinearLayout>(R.id.sockets_container)

        // Добавляем чекбоксы для всех свободных розеток
        val availableSockets = sockets.filter { socket ->
            !rooms.any { room -> room.socketIds.contains(socket.id) }
        }

        val selectedSocketIds = mutableSetOf<Int>()
        availableSockets.forEach { socket ->
            val checkBox = CheckBox(this)
            checkBox.text = "${socket.name} (${socket.deviceName})"
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    selectedSocketIds.add(socket.id)
                } else {
                    selectedSocketIds.remove(socket.id)
                }
            }
            socketsContainer.addView(checkBox)
        }

        AlertDialog.Builder(this)
            .setTitle("Добавить комнату")
            .setView(dialogView)
            .setPositiveButton("Добавить") { _, _ ->
                val roomName = roomNameEdit.text.toString().trim()
                if (roomName.isNotEmpty()) {
                    val newRoom = Room(
                        id = rooms.maxOfOrNull { it.id }?.plus(1) ?: 1,
                        name = roomName,
                        temperatureSensorId = if (hasSensorsCheckbox.isChecked) rooms.size + 1 else null,
                        humiditySensorId = if (hasSensorsCheckbox.isChecked) rooms.size + 1 else null,
                        lightOn = false,
                        socketIds = selectedSocketIds.toList()
                    )
                    rooms.add(newRoom)
                    roomsAdapter.notifyItemInserted(rooms.size - 1)
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun showEditRoomDialog(room: Room) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_edit_room, null)
        val roomNameEdit = dialogView.findViewById<EditText>(R.id.room_name_edit)
        val hasSensorsCheckbox = dialogView.findViewById<CheckBox>(R.id.has_sensors_checkbox)
        val socketsContainer = dialogView.findViewById<LinearLayout>(R.id.sockets_container)

        // Заполняем текущие данные
        roomNameEdit.setText(room.name)
        hasSensorsCheckbox.isChecked = room.temperatureSensorId != null

        // Добавляем чекбоксы для розеток
        val availableSockets = sockets.filter { socket ->
            room.socketIds.contains(socket.id) || !rooms.any { r -> r.socketIds.contains(socket.id) }
        }

        val selectedSocketIds = room.socketIds.toMutableSet()
        availableSockets.forEach { socket ->
            val checkBox = CheckBox(this)
            checkBox.text = "${socket.name} (${socket.deviceName})"
            checkBox.isChecked = room.socketIds.contains(socket.id)
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    selectedSocketIds.add(socket.id)
                } else {
                    selectedSocketIds.remove(socket.id)
                }
            }
            socketsContainer.addView(checkBox)
        }

        AlertDialog.Builder(this)
            .setTitle("Редактировать комнату")
            .setView(dialogView)
            .setPositiveButton("Сохранить") { _, _ ->
                val roomName = roomNameEdit.text.toString().trim()
                if (roomName.isNotEmpty()) {
                    val roomIndex = rooms.indexOfFirst { it.id == room.id }
                    if (roomIndex != -1) {
                        rooms[roomIndex] = room.copy(
                            name = roomName,
                            temperatureSensorId = if (hasSensorsCheckbox.isChecked) room.temperatureSensorId ?: room.id else null,
                            humiditySensorId = if (hasSensorsCheckbox.isChecked) room.humiditySensorId ?: room.id else null,
                            socketIds = selectedSocketIds.toList()
                        )
                        roomsAdapter.notifyItemChanged(roomIndex)
                    }
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun showDeleteRoomDialog(room: Room) {
        AlertDialog.Builder(this)
            .setTitle("Удалить комнату")
            .setMessage("Вы уверены, что хотите удалить комнату \"${room.name}\"?")
            .setPositiveButton("Удалить") { _, _ ->
                val roomIndex = rooms.indexOfFirst { it.id == room.id }
                if (roomIndex != -1) {
                    rooms.removeAt(roomIndex)
                    roomsAdapter.notifyItemRemoved(roomIndex)
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun showEditSocketDialog(socket: Socket) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_socket, null)
        val deviceNameEdit = dialogView.findViewById<EditText>(R.id.device_name_edit)

        deviceNameEdit.setText(socket.deviceName)

        AlertDialog.Builder(this)
            .setTitle("Изменить название устройства")
            .setView(dialogView)
            .setPositiveButton("Сохранить") { _, _ ->
                val newName = deviceNameEdit.text.toString().trim()
                if (newName.isNotEmpty()) {
                    socket.deviceName = newName
                    roomsAdapter.notifyDataSetChanged()
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}