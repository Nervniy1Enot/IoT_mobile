package com.example.diplom.activities

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.diplom.R
import com.example.diplom.adapters.RfidTagsAdapter
import com.example.diplom.models.RfidTag
import com.google.android.material.floatingactionbutton.FloatingActionButton

class RfidManagementActivity : AppCompatActivity() {

    private lateinit var rfidRecyclerView: RecyclerView
    private lateinit var addRfidFab: FloatingActionButton
    private lateinit var rfidAdapter: RfidTagsAdapter

    private val rfidTags = mutableListOf<RfidTag>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rfid_management)

        initViews()
        setupMockData()
        setupRecyclerView()
        setupToolbar()
    }

    private fun initViews() {
        rfidRecyclerView = findViewById(R.id.rfid_recycler_view)
        addRfidFab = findViewById(R.id.add_rfid_fab)

        addRfidFab.setOnClickListener {
            showAddRfidDialog()
        }
    }

    private fun setupToolbar() {
        supportActionBar?.apply {
            title = "RFID метки"
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun setupMockData() {
        rfidTags.clear()
        rfidTags.addAll(listOf(
            RfidTag("A1B2C3D4", "Иван Петров", true, "2025-01-15"),
            RfidTag("E5F6G7H8", "Мария Сидорова", true, "2025-02-20"),
            RfidTag("I9J0K1L2", "Гостевая карта", false, "2025-03-10")
        ))
    }

    private fun setupRecyclerView() {
        rfidAdapter = RfidTagsAdapter(
            tags = rfidTags,
            onEditTag = { tag: RfidTag -> showEditRfidDialog(tag) },
            onDeleteTag = { tag: RfidTag -> showDeleteRfidDialog(tag) },
            onToggleTag = { tag: RfidTag -> toggleTagStatus(tag) }
        )

        rfidRecyclerView.layoutManager = LinearLayoutManager(this)
        rfidRecyclerView.adapter = rfidAdapter
    }

    private fun showAddRfidDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_rfid_tag, null)
        val ownerNameEdit = dialogView.findViewById<EditText>(R.id.owner_name_edit)

        AlertDialog.Builder(this)
            .setTitle("Привязать новую RFID метку")
            .setMessage("Приложите RFID метку к считывателю и введите имя владельца")
            .setView(dialogView as View)
            .setPositiveButton("Привязать") { _, _ ->
                val ownerName = ownerNameEdit.text.toString().trim()
                if (ownerName.isNotEmpty()) {
                    // Симуляция считывания метки
                    val newTag = RfidTag(
                        uid = generateRandomUid(),
                        ownerName = ownerName,
                        isActive = true,
                        dateAdded = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
                    )
                    rfidTags.add(newTag)
                    rfidAdapter.notifyItemInserted(rfidTags.size - 1)
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun showEditRfidDialog(tag: RfidTag) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_rfid_tag, null)
        val ownerNameEdit = dialogView.findViewById<EditText>(R.id.owner_name_edit)

        ownerNameEdit.setText(tag.ownerName)

        AlertDialog.Builder(this)
            .setTitle("Редактировать RFID метку")
            .setView(dialogView as View)
            .setPositiveButton("Сохранить") { _, _ ->
                val newName = ownerNameEdit.text.toString().trim()
                if (newName.isNotEmpty()) {
                    val tagIndex = rfidTags.indexOfFirst { it.uid == tag.uid }
                    if (tagIndex != -1) {
                        rfidTags[tagIndex] = tag.copy(ownerName = newName)
                        rfidAdapter.notifyItemChanged(tagIndex)
                    }
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun showDeleteRfidDialog(tag: RfidTag) {
        AlertDialog.Builder(this)
            .setTitle("Удалить RFID метку")
            .setMessage("Вы уверены, что хотите удалить метку \"${tag.ownerName}\"?\nUID: ${tag.uid}")
            .setPositiveButton("Удалить") { _, _ ->
                val tagIndex = rfidTags.indexOfFirst { it.uid == tag.uid }
                if (tagIndex != -1) {
                    rfidTags.removeAt(tagIndex)
                    rfidAdapter.notifyItemRemoved(tagIndex)
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun toggleTagStatus(tag: RfidTag) {
        val tagIndex = rfidTags.indexOfFirst { it.uid == tag.uid }
        if (tagIndex != -1) {
            rfidTags[tagIndex] = tag.copy(isActive = !tag.isActive)
            rfidAdapter.notifyItemChanged(tagIndex)
        }
    }

    private fun generateRandomUid(): String {
        val chars = "0123456789ABCDEF"
        return (1..8).map { chars.random() }.joinToString("")
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}