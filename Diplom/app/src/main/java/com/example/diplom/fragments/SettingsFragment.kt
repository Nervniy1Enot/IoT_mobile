package com.example.diplom.fragments

import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.diplom.R
import com.example.diplom.models.*
import com.example.diplom.adapters.RoomAutoOffAdapter
import java.text.SimpleDateFormat
import java.util.*

class SettingsFragment : Fragment() {

    private lateinit var alarmEnabledSwitch: Switch
    private lateinit var alarmTimeButton: Button
    private lateinit var alarmTimeText: TextView
    private lateinit var alarmDaysContainer: LinearLayout

    private lateinit var autoOffEnabledSwitch: Switch
    private lateinit var autoOffDelaySpinner: Spinner
    private lateinit var roomsAutoOffRecyclerView: RecyclerView
    private lateinit var roomsAutoOffAdapter: RoomAutoOffAdapter

    private var alarmSettings = AlarmSettings()
    private var autoOffSettings = AutoOffSettings()
    private val rooms = mutableListOf<Room>()
    private val sockets = mutableListOf<Socket>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        setupMockData()
        setupAlarmSettings()
        setupAutoOffSettings()
        loadSettings()
    }

    private fun initViews(view: View) {
        // Настройки будильника
        alarmEnabledSwitch = view.findViewById(R.id.alarm_enabled_switch)
        alarmTimeButton = view.findViewById(R.id.alarm_time_button)
        alarmTimeText = view.findViewById(R.id.alarm_time_text)
        alarmDaysContainer = view.findViewById(R.id.alarm_days_container)

        // Настройки автовыключения
        autoOffEnabledSwitch = view.findViewById(R.id.auto_off_enabled_switch)
        autoOffDelaySpinner = view.findViewById(R.id.auto_off_delay_spinner)
        roomsAutoOffRecyclerView = view.findViewById(R.id.rooms_auto_off_recycler_view)
    }

    private fun setupMockData() {
        // Загружаем те же данные что и в HomeFragment
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
            Room(1, "Гостиная", 1, 1, true, listOf(1, 2)),
            Room(2, "Спальня", 2, 2, false, listOf(3)),
            Room(3, "Кухня", 3, 3, true, listOf(4, 5, 6)),
            Room(4, "Ванная", null, null, false, emptyList())
        ))

        // Инициализируем настройки автовыключения для каждой комнаты
        autoOffSettings.roomSettings.clear()
        rooms.forEach { room ->
            autoOffSettings.roomSettings.add(
                RoomAutoOffSettings(
                    roomId = room.id,
                    enabled = false,
                    turnOffLights = true,
                    turnOffSockets = emptyList()
                )
            )
        }
    }

    private fun setupAlarmSettings() {
        alarmEnabledSwitch.setOnCheckedChangeListener { _, isChecked ->
            alarmSettings.enabled = isChecked
            updateAlarmUI()
            saveSettings()
        }

        alarmTimeButton.setOnClickListener {
            showTimePicker()
        }

        setupDayCheckboxes()
    }

    private fun setupAutoOffSettings() {
        autoOffEnabledSwitch.setOnCheckedChangeListener { _, isChecked ->
            autoOffSettings.enabled = isChecked
            updateAutoOffUI()
            saveSettings()
        }

        setupDelaySpinner()
        setupRoomsRecyclerView()
    }

    private fun setupRoomsRecyclerView() {
        roomsAutoOffAdapter = RoomAutoOffAdapter(
            rooms,
            sockets,
            autoOffSettings.roomSettings
        ) { roomSettings ->
            saveSettings()
        }
        roomsAutoOffRecyclerView.layoutManager = LinearLayoutManager(context)
        roomsAutoOffRecyclerView.adapter = roomsAutoOffAdapter
    }

    private fun setupDayCheckboxes() {
        val days = arrayOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс")

        for (i in days.indices) {
            val checkBox = CheckBox(context)
            checkBox.text = days[i]
            checkBox.textSize = 16f
            checkBox.setTextColor(resources.getColor(android.R.color.black))
            checkBox.setPadding(0, 8, 16, 8)

            checkBox.setOnCheckedChangeListener { _, isChecked ->
                alarmSettings.activeDays[i] = isChecked
                saveSettings()
            }

            alarmDaysContainer.addView(checkBox)
        }
    }

    private fun setupDelaySpinner() {
        val delays = arrayOf("Сразу", "5 минут", "10 минут", "15 минут", "30 минут")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, delays)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        autoOffDelaySpinner.adapter = adapter

        autoOffDelaySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                autoOffSettings.delayMinutes = when (position) {
                    0 -> 0
                    1 -> 5
                    2 -> 10
                    3 -> 15
                    4 -> 30
                    else -> 0
                }
                saveSettings()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun showTimePicker() {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, alarmSettings.hour)
        calendar.set(Calendar.MINUTE, alarmSettings.minute)

        TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                alarmSettings.hour = hourOfDay
                alarmSettings.minute = minute
                updateAlarmTimeText()
                saveSettings()
            },
            alarmSettings.hour,
            alarmSettings.minute,
            true
        ).show()
    }

    private fun updateAlarmUI() {
        val enabled = alarmSettings.enabled
        alarmTimeButton.isEnabled = enabled
        alarmTimeText.alpha = if (enabled) 1.0f else 0.5f

        for (i in 0 until alarmDaysContainer.childCount) {
            val checkBox = alarmDaysContainer.getChildAt(i) as CheckBox
            checkBox.isEnabled = enabled
            checkBox.alpha = if (enabled) 1.0f else 0.5f
        }
    }

    private fun updateAutoOffUI() {
        val enabled = autoOffSettings.enabled
        autoOffDelaySpinner.isEnabled = enabled
        autoOffDelaySpinner.alpha = if (enabled) 1.0f else 0.5f
        roomsAutoOffRecyclerView.alpha = if (enabled) 1.0f else 0.5f

        // Обновляем адаптер для отображения состояния
        roomsAutoOffAdapter.setGlobalEnabled(enabled)
    }

    private fun updateAlarmTimeText() {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, alarmSettings.hour)
        calendar.set(Calendar.MINUTE, alarmSettings.minute)

        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        alarmTimeText.text = timeFormat.format(calendar.time)
    }

    private fun loadSettings() {
        // Здесь можно загрузить настройки из SharedPreferences или базы данных
        updateAlarmTimeText()
        updateAlarmUI()
        updateAutoOffUI()

        // Устанавливаем состояние чекбоксов дней
        for (i in 0 until alarmDaysContainer.childCount) {
            val checkBox = alarmDaysContainer.getChildAt(i) as CheckBox
            checkBox.isChecked = alarmSettings.activeDays[i]
        }

        // Устанавливаем позицию спиннера задержки
        val delayPosition = when (autoOffSettings.delayMinutes) {
            0 -> 0
            5 -> 1
            10 -> 2
            15 -> 3
            30 -> 4
            else -> 0
        }
        autoOffDelaySpinner.setSelection(delayPosition)
    }

    private fun saveSettings() {
        // Здесь можно сохранить настройки в SharedPreferences или базу данных
        println("Настройки сохранены:")
        println("Будильник: ${alarmSettings.enabled}, время: ${alarmSettings.hour}:${alarmSettings.minute}")
        println("Автовыключение: ${autoOffSettings.enabled}, задержка: ${autoOffSettings.delayMinutes} мин")
        autoOffSettings.roomSettings.forEach { roomSetting ->
            val room = rooms.find { it.id == roomSetting.roomId }
            println("Комната ${room?.name}: включено=${roomSetting.enabled}, свет=${roomSetting.turnOffLights}, розетки=${roomSetting.turnOffSockets}")
        }
    }
}