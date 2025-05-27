package com.example.diplom.fragments

import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import com.example.diplom.R
import com.example.diplom.models.*
import java.text.SimpleDateFormat
import java.util.*

class SettingsFragment : Fragment() {

    private lateinit var alarmEnabledSwitch: Switch
    private lateinit var alarmTimeButton: Button
    private lateinit var alarmTimeText: TextView
    private lateinit var alarmDaysContainer: LinearLayout

    private lateinit var autoOffEnabledSwitch: Switch
    private lateinit var autoOffDelaySpinner: Spinner
    private lateinit var roomsAutoOffContainer: LinearLayout

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
        initializeRoomSettings()
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
        roomsAutoOffContainer = view.findViewById(R.id.rooms_auto_off_container)
    }

    private fun setupMockData() {
        // Загружаем точно те же данные что и в HomeFragment
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
                socketIds = listOf(1, 2)  // Телевизор, Торшер
            ),
            Room(
                id = 2,
                name = "Спальня",
                temperatureSensorId = 2,
                humiditySensorId = 2,
                lightOn = false,
                socketIds = listOf(3)      // Зарядка телефона
            ),
            Room(
                id = 3,
                name = "Кухня",
                temperatureSensorId = 3,
                humiditySensorId = 3,
                lightOn = true,
                socketIds = listOf(4, 5, 6) // Чайник, Микроволновка, Холодильник
            ),
            Room(
                id = 4,
                name = "Ванная",
                temperatureSensorId = null,
                humiditySensorId = null,
                lightOn = false,
                socketIds = emptyList()     // Без розеток
            )
        ))
    }

    private fun initializeRoomSettings() {
        // Инициализируем настройки автовыключения для каждой комнаты
        println("Инициализация настроек для ${rooms.size} комнат")
        rooms.forEach { room ->
            println("Инициализация комнаты: ${room.name} (ID: ${room.id})")
            if (!autoOffSettings.roomSettings.containsKey(room.id)) {
                autoOffSettings.roomSettings[room.id] = RoomAutoOffSettings(
                    roomId = room.id,
                    enabled = true,
                    turnOffLights = true,
                    turnOffSockets = mutableSetOf()
                )
            }
        }
        println("Всего настроек комнат: ${autoOffSettings.roomSettings.size}")
    }

    private fun setupAlarmSettings() {
        // Настройка переключателя будильника
        alarmEnabledSwitch.setOnCheckedChangeListener { _, isChecked ->
            alarmSettings.enabled = isChecked
            saveAlarmSettings()
        }

        // Настройка кнопки времени
        alarmTimeButton.setOnClickListener {
            showTimePickerDialog()
        }

        // Создание переключателей для дней недели
        setupDayCheckboxes()
    }

    private fun setupDayCheckboxes() {
        val days = arrayOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс")

        alarmDaysContainer.removeAllViews()

        for (i in days.indices) {
            val checkBox = CheckBox(requireContext())
            checkBox.text = days[i]
            checkBox.textSize = 12f
            checkBox.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_primary))

            val params = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
            checkBox.layoutParams = params

            checkBox.isChecked = alarmSettings.days[i]
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                alarmSettings.days[i] = isChecked
                saveAlarmSettings()
            }

            alarmDaysContainer.addView(checkBox)
        }
    }

    private fun setupAutoOffSettings() {
        // Настройка переключателя автовыключения
        autoOffEnabledSwitch.setOnCheckedChangeListener { _, isChecked ->
            autoOffSettings.enabled = isChecked
            updateAutoOffUI()
            saveAutoOffSettings()
        }

        // Настройка спиннера задержки
        val delayOptions = arrayOf("5 минут", "10 минут", "15 минут", "30 минут", "1 час")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, delayOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        autoOffDelaySpinner.adapter = adapter

        autoOffDelaySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val delayValues = arrayOf(5, 10, 15, 30, 60)
                autoOffSettings.delayMinutes = delayValues[position]
                saveAutoOffSettings()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Создаем элементы настроек для каждой комнаты
        createRoomSettingsViews()
    }

    private fun createRoomSettingsViews() {
        roomsAutoOffContainer.removeAllViews()

        rooms.forEach { room ->
            val roomSettings = autoOffSettings.roomSettings[room.id]
            if (roomSettings != null) {
                val roomView = createRoomSettingView(room, roomSettings)
                roomsAutoOffContainer.addView(roomView)
            }
        }
    }

    private fun createRoomSettingView(room: Room, settings: RoomAutoOffSettings): View {
        val inflater = LayoutInflater.from(requireContext())
        val roomView = inflater.inflate(R.layout.item_room_auto_off_detailed, roomsAutoOffContainer, false)

        val roomName = roomView.findViewById<TextView>(R.id.room_name)
        val roomSwitch = roomView.findViewById<Switch>(R.id.room_enabled_switch)
        val lightCheckBox = roomView.findViewById<CheckBox>(R.id.light_checkbox)
        val socketsContainer = roomView.findViewById<LinearLayout>(R.id.sockets_container)
        val socketsLabel = roomView.findViewById<TextView>(R.id.sockets_label)

        roomName.text = room.name

        // Настройка переключателя комнаты
        roomSwitch.setOnCheckedChangeListener(null)
        roomSwitch.isChecked = settings.enabled
        roomSwitch.setOnCheckedChangeListener { _, isChecked ->
            settings.enabled = isChecked
            updateRoomChildElements(lightCheckBox, socketsContainer, settings)
            saveAutoOffSettings()
        }

        // Настройка чекбокса света
        lightCheckBox.setOnCheckedChangeListener(null)
        lightCheckBox.isChecked = settings.turnOffLights
        lightCheckBox.setOnCheckedChangeListener { _, isChecked ->
            settings.turnOffLights = isChecked
            saveAutoOffSettings()
        }

        // Добавляем розетки
        val roomSockets = sockets.filter { socket -> room.socketIds.contains(socket.id) }

        if (roomSockets.isEmpty()) {
            val noSocketsText = TextView(requireContext())
            noSocketsText.text = "Нет розеток в комнате"
            noSocketsText.textSize = 14f
            noSocketsText.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary))
            noSocketsText.setPadding(16, 8, 16, 8)
            socketsContainer.addView(noSocketsText)
            socketsLabel.visibility = View.GONE
        } else {
            socketsLabel.visibility = View.VISIBLE

            roomSockets.forEach { socket ->
                val checkBox = CheckBox(requireContext())
                checkBox.text = socket.deviceName
                checkBox.textSize = 14f
                checkBox.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_primary))
                checkBox.setPadding(16, 8, 16, 8)

                checkBox.setOnCheckedChangeListener(null)
                checkBox.isChecked = settings.turnOffSockets.contains(socket.id)

                checkBox.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        settings.turnOffSockets.add(socket.id)
                    } else {
                        settings.turnOffSockets.remove(socket.id)
                    }
                    saveAutoOffSettings()
                }

                socketsContainer.addView(checkBox)
            }
        }

        updateRoomChildElements(lightCheckBox, socketsContainer, settings)
        return roomView
    }

    private fun updateRoomChildElements(lightCheckBox: CheckBox, socketsContainer: LinearLayout, settings: RoomAutoOffSettings) {
        val enabled = settings.enabled && autoOffSettings.enabled
        val alpha = if (enabled) 1.0f else 0.5f

        lightCheckBox.isEnabled = enabled
        lightCheckBox.alpha = alpha

        for (i in 0 until socketsContainer.childCount) {
            val view = socketsContainer.getChildAt(i)
            if (view is CheckBox) {
                view.isEnabled = enabled
                view.alpha = alpha
            } else if (view is TextView) {
                view.alpha = alpha
            }
        }
    }

    private fun updateAutoOffUI() {
        val enabled = autoOffSettings.enabled
        val alpha = if (enabled) 1.0f else 0.5f

        autoOffDelaySpinner.isEnabled = enabled
        autoOffDelaySpinner.alpha = alpha
        roomsAutoOffContainer.alpha = alpha

        // Обновляем все дочерние элементы
        createRoomSettingsViews()
    }

    private fun showTimePickerDialog() {
        val calendar = Calendar.getInstance()
        val currentHour = alarmSettings.hour
        val currentMinute = alarmSettings.minute

        val timePickerDialog = TimePickerDialog(
            requireContext(),
            { _, hourOfDay, minute ->
                alarmSettings.hour = hourOfDay
                alarmSettings.minute = minute
                updateAlarmTimeText()
                saveAlarmSettings()
            },
            currentHour,
            currentMinute,
            true
        )

        timePickerDialog.show()
    }

    private fun updateAlarmTimeText() {
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, alarmSettings.hour)
        calendar.set(Calendar.MINUTE, alarmSettings.minute)
        alarmTimeText.text = timeFormat.format(calendar.time)
    }

    private fun loadSettings() {
        // Загрузка настроек будильника
        alarmEnabledSwitch.isChecked = alarmSettings.enabled
        updateAlarmTimeText()
        setupDayCheckboxes()

        // Загрузка настроек автовыключения
        autoOffEnabledSwitch.isChecked = autoOffSettings.enabled
        updateAutoOffUI()

        // Установка выбранной задержки в спиннере
        val delayValues = arrayOf(5, 10, 15, 30, 60)
        val delayIndex = delayValues.indexOf(autoOffSettings.delayMinutes)
        if (delayIndex >= 0) {
            autoOffDelaySpinner.setSelection(delayIndex)
        }
    }

    private fun saveAlarmSettings() {
        // Здесь будет сохранение в SharedPreferences или базу данных
        println("Сохранение настроек будильника: $alarmSettings")
    }

    private fun saveAutoOffSettings() {
        // Здесь будет сохранение в SharedPreferences или базу данных
        println("Сохранение настроек автовыключения: $autoOffSettings")
        autoOffSettings.roomSettings.forEach { (roomId, settings) ->
            val room = rooms.find { it.id == roomId }
            println("Комната ${room?.name}: включено=${settings.enabled}, свет=${settings.turnOffLights}, розетки=${settings.turnOffSockets}")
        }
    }
}