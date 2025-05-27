package com.example.diplom.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.diplom.R

class UserAuthActivity : AppCompatActivity() {

    private lateinit var firstNameEdit: EditText
    private lateinit var lastNameEdit: EditText
    private lateinit var passwordEdit: EditText
    private lateinit var loginButton: Button

    // Тестовые пользователи (в реальном приложении будут храниться на сервере)
    private val users = mapOf(
        "ivan.petrov" to "password123",
        "maria.sidorova" to "qwerty456",
        "admin" to "admin"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_auth)

        initViews()
        setupToolbar()
    }

    private fun initViews() {
        firstNameEdit = findViewById(R.id.first_name_edit)
        lastNameEdit = findViewById(R.id.last_name_edit)
        passwordEdit = findViewById(R.id.password_edit)
        loginButton = findViewById(R.id.login_button)

        loginButton.setOnClickListener {
            attemptLogin()
        }
    }

    private fun setupToolbar() {
        supportActionBar?.apply {
            title = "Вход в систему"
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun attemptLogin() {
        val firstName = firstNameEdit.text.toString().trim()
        val lastName = lastNameEdit.text.toString().trim()
        val password = passwordEdit.text.toString().trim()

        if (firstName.isEmpty() || lastName.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show()
            return
        }

        // Формируем логин из имени и фамилии
        val username = "${firstName.lowercase()}.${lastName.lowercase()}"
        val fullName = "$firstName $lastName"

        // Проверяем учетные данные
        if (users[username] == password) {
            // Успешная авторизация
            val resultIntent = Intent()
            resultIntent.putExtra("user_name", fullName)
            setResult(RESULT_OK, resultIntent)
            finish()
        } else {
            // Неверные учетные данные
            Toast.makeText(this, "Неверное имя пользователя или пароль", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}