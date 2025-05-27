package com.example.diplom.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.diplom.R
import com.example.diplom.activities.RoomsManagementActivity
import com.example.diplom.activities.RfidManagementActivity
import com.example.diplom.activities.UserAuthActivity

class ProfileFragment : Fragment() {

    private lateinit var currentUserName: TextView
    private lateinit var currentUserStatus: TextView
    private lateinit var userAuthButton: Button
    private lateinit var roomsManagementButton: LinearLayout
    private lateinit var rfidManagementButton: LinearLayout
    private lateinit var devicesCount: TextView
    private lateinit var roomsCount: TextView

    // Временные данные для демонстрации
    private var isUserLoggedIn = false
    private var currentUser: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        setupClickListeners()
        updateUserInfo()
        updateSystemInfo()
    }

    private fun initViews(view: View) {
        currentUserName = view.findViewById(R.id.current_user_name)
        currentUserStatus = view.findViewById(R.id.current_user_status)
        userAuthButton = view.findViewById(R.id.user_auth_button)
        roomsManagementButton = view.findViewById(R.id.rooms_management_button)
        rfidManagementButton = view.findViewById(R.id.rfid_management_button)
        devicesCount = view.findViewById(R.id.devices_count)
        roomsCount = view.findViewById(R.id.rooms_count)
    }

    private fun setupClickListeners() {
        // Кнопка авторизации/выхода
        userAuthButton.setOnClickListener {
            if (isUserLoggedIn) {
                logoutUser()
            } else {
                openUserAuthActivity()
            }
        }

        // Управление комнатами
        roomsManagementButton.setOnClickListener {
            openRoomsManagement()
        }

        // Управление RFID метками
        rfidManagementButton.setOnClickListener {
            openRfidManagement()
        }
    }

    private fun openUserAuthActivity() {
        val intent = Intent(requireContext(), UserAuthActivity::class.java)
        startActivityForResult(intent, REQUEST_USER_AUTH)
    }

    private fun openRoomsManagement() {
        val intent = Intent(requireContext(), RoomsManagementActivity::class.java)
        startActivity(intent)
    }

    private fun openRfidManagement() {
        val intent = Intent(requireContext(), RfidManagementActivity::class.java)
        startActivity(intent)
    }

    private fun logoutUser() {
        isUserLoggedIn = false
        currentUser = null
        updateUserInfo()

        // Здесь можно добавить очистку сохраненных данных
        println("Пользователь вышел из системы")
    }

    private fun updateUserInfo() {
        if (isUserLoggedIn && currentUser != null) {
            currentUserName.text = currentUser
            currentUserStatus.text = "Авторизован"
            userAuthButton.text = "Выйти из системы"
        } else {
            currentUserName.text = "Не авторизован"
            currentUserStatus.text = "Войдите в систему"
            userAuthButton.text = "Войти в систему"
        }
    }

    private fun updateSystemInfo() {
        // Здесь можно получать реальные данные о системе
        devicesCount.text = "6" // Количество подключенных розеток
        roomsCount.text = "4"   // Количество настроенных комнат
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_USER_AUTH && resultCode == android.app.Activity.RESULT_OK) {
            // Успешная авторизация
            data?.let {
                val userName = it.getStringExtra("user_name")
                if (userName != null) {
                    isUserLoggedIn = true
                    currentUser = userName
                    updateUserInfo()
                }
            }
        }
    }

    companion object {
        private const val REQUEST_USER_AUTH = 1001
    }
}