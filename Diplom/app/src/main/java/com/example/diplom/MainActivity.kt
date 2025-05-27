package com.example.diplom

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.diplom.fragments.HomeFragment
import com.example.diplom.fragments.SettingsFragment
import com.example.diplom.fragments.HistoryFragment
import com.example.diplom.fragments.ProfileFragment

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigation = findViewById(R.id.bottom_navigation)

        // Загружаем главный фрагмент по умолчанию
        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
        }

        // Обработка навигации
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    loadFragment(HomeFragment())
                    true
                }
                R.id.nav_settings -> {
                    loadFragment(SettingsFragment())
                    true
                }
                R.id.nav_history -> {
                    loadFragment(HistoryFragment())
                    true
                }
                R.id.nav_profile -> {
                    loadFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}