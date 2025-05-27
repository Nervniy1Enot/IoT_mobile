package com.example.diplom.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.diplom.R
import com.example.diplom.adapters.AdvancedHistoryAdapter
import com.example.diplom.models.EventType
import com.example.diplom.models.HistoryEvent
import java.text.SimpleDateFormat
import java.util.*

class HistoryFragment : Fragment() {

    private lateinit var dateFilterButton: Button
    private lateinit var clearDateFilterButton: Button
    private lateinit var eventTypeFilterSpinner: Spinner
    private lateinit var sortOrderSpinner: Spinner
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var emptyStateLayout: LinearLayout
    private lateinit var historyAdapter: AdvancedHistoryAdapter

    private val allEvents = mutableListOf<HistoryEvent>()
    private var filteredEvents = mutableListOf<HistoryEvent>()
    private val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    private var selectedDate: Date? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        setupMockData()
        setupFilters()
        setupRecyclerView()
        updateEventsList()
    }

    private fun initViews(view: View) {
        dateFilterButton = view.findViewById(R.id.date_filter_button)
        clearDateFilterButton = view.findViewById(R.id.clear_date_filter_button)
        eventTypeFilterSpinner = view.findViewById(R.id.event_type_filter_spinner)
        sortOrderSpinner = view.findViewById(R.id.sort_order_spinner)
        historyRecyclerView = view.findViewById(R.id.history_recycler_view)
        emptyStateLayout = view.findViewById(R.id.empty_state_layout)
    }

    private fun setupMockData() {
        val calendar = Calendar.getInstance()

        // Создаем тестовые события за последние несколько дней
        allEvents.clear()
        allEvents.addAll(listOf(
            // Сегодня
            HistoryEvent(
                1, calendar.time, EventType.RFID_ENTRY,
                "Вход в дом", "Иван вошел в дом", userName = "Иван"
            ),
            HistoryEvent(
                2, calendar.apply { add(Calendar.HOUR, -1) }.time, EventType.LIGHT_ON,
                "Включение света", "Свет включен в гостиной", roomName = "Гостиная"
            ),
            HistoryEvent(
                3, calendar.apply { add(Calendar.MINUTE, -30) }.time, EventType.SOCKET_ON,
                "Включение розетки", "Телевизор включен", roomName = "Гостиная", deviceName = "Телевизор"
            ),

            // Вчера
            HistoryEvent(
                4, calendar.apply { add(Calendar.DAY_OF_MONTH, -1); set(Calendar.HOUR_OF_DAY, 7); set(Calendar.MINUTE, 0) }.time,
                EventType.ALARM_TRIGGERED, "Сработал будильник", "Утренний будильник в 07:00"
            ),
            HistoryEvent(
                5, calendar.apply { add(Calendar.MINUTE, 15) }.time, EventType.LIGHT_ON,
                "Включение света", "Свет включен в спальне", roomName = "Спальня"
            ),
            HistoryEvent(
                6, calendar.apply { set(Calendar.HOUR_OF_DAY, 22); set(Calendar.MINUTE, 30) }.time,
                EventType.RFID_EXIT, "Выход из дома", "Иван покинул дом", userName = "Иван"
            ),
            HistoryEvent(
                7, calendar.apply { add(Calendar.MINUTE, 2) }.time, EventType.AUTO_OFF_TRIGGERED,
                "Автовыключение", "Выключены свет и розетки при уходе"
            ),

            // 2 дня назад
            HistoryEvent(
                8, calendar.apply { add(Calendar.DAY_OF_MONTH, -1); set(Calendar.HOUR_OF_DAY, 18); set(Calendar.MINUTE, 45) }.time,
                EventType.SOCKET_ON, "Включение розетки", "Микроволновка включена", roomName = "Кухня", deviceName = "Микроволновка"
            ),
            HistoryEvent(
                9, calendar.apply { add(Calendar.MINUTE, 5) }.time, EventType.SOCKET_OFF,
                "Выключение розетки", "Микроволновка выключена", roomName = "Кухня", deviceName = "Микроволновка"
            ),
            HistoryEvent(
                10, calendar.apply { set(Calendar.HOUR_OF_DAY, 23); set(Calendar.MINUTE, 15) }.time,
                EventType.LIGHT_OFF, "Выключение света", "Свет выключен в гостиной", roomName = "Гостиная"
            ),

            // 3 дня назад
            HistoryEvent(
                11, calendar.apply { add(Calendar.DAY_OF_MONTH, -1); set(Calendar.HOUR_OF_DAY, 16); set(Calendar.MINUTE, 20) }.time,
                EventType.RFID_ENTRY, "Вход в дом", "Мария вошла в дом", userName = "Мария"
            ),
            HistoryEvent(
                12, calendar.apply { add(Calendar.HOUR, 3) }.time, EventType.SOCKET_ON,
                "Включение розетки", "Чайник включен", roomName = "Кухня", deviceName = "Чайник"
            ),
            HistoryEvent(
                13, calendar.apply { set(Calendar.HOUR_OF_DAY, 12); set(Calendar.MINUTE, 0) }.time,
                EventType.SYSTEM_EVENT, "Обновление системы", "Обновлено ПО контроллера до версии 1.2.3"
            )
        ))

        // Сортируем по времени (сначала новые)
        allEvents.sortByDescending { it.timestamp }
    }

    private fun setupFilters() {
        // Настройка кнопки выбора даты
        dateFilterButton.setOnClickListener {
            showDatePickerDialog()
        }

        clearDateFilterButton.setOnClickListener {
            selectedDate = null
            dateFilterButton.text = "Выбрать дату"
            updateEventsList()
        }

        // Настройка фильтра по типу события
        val eventTypeOptions = mutableListOf("Все события").apply {
            addAll(EventType.values().map { it.displayName })
        }
        val eventTypeAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, eventTypeOptions)
        eventTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        eventTypeFilterSpinner.adapter = eventTypeAdapter

        eventTypeFilterSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                updateEventsList()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Настройка сортировки
        val sortOptions = arrayOf("Сначала новые", "Сначала старые")
        val sortAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, sortOptions)
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sortOrderSpinner.adapter = sortAdapter

        sortOrderSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                updateEventsList()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupRecyclerView() {
        historyAdapter = AdvancedHistoryAdapter()
        historyRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        historyRecyclerView.adapter = historyAdapter
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        if (selectedDate != null) {
            calendar.time = selectedDate!!
        }

        DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(year, month, dayOfMonth)
                selectedDate = selectedCalendar.time
                dateFilterButton.text = dateFormat.format(selectedDate!!)
                updateEventsList()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun updateEventsList() {
        filteredEvents.clear()

        // Фильтруем события
        var eventsToShow = allEvents.toList()

        // Фильтр по дате
        selectedDate?.let { date ->
            val startOfDay = Calendar.getInstance().apply {
                time = date
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.time

            val endOfDay = Calendar.getInstance().apply {
                time = date
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
                set(Calendar.MILLISECOND, 999)
            }.time

            eventsToShow = eventsToShow.filter { event ->
                event.timestamp.after(startOfDay) && event.timestamp.before(endOfDay)
            }
        }

        // Фильтр по типу события
        val selectedTypePosition = eventTypeFilterSpinner.selectedItemPosition
        if (selectedTypePosition > 0) { // 0 = "Все события"
            val selectedEventType = EventType.values()[selectedTypePosition - 1]
            eventsToShow = eventsToShow.filter { it.type == selectedEventType }
        }

        // Сортировка
        val sortOrder = sortOrderSpinner.selectedItemPosition
        eventsToShow = if (sortOrder == 0) { // Сначала новые
            eventsToShow.sortedByDescending { it.timestamp }
        } else { // Сначала старые
            eventsToShow.sortedBy { it.timestamp }
        }

        filteredEvents.addAll(eventsToShow)
        historyAdapter.updateItems(filteredEvents)

        // Показываем/скрываем пустое состояние
        if (filteredEvents.isEmpty()) {
            historyRecyclerView.visibility = View.GONE
            emptyStateLayout.visibility = View.VISIBLE
        } else {
            historyRecyclerView.visibility = View.VISIBLE
            emptyStateLayout.visibility = View.GONE
        }
    }
}