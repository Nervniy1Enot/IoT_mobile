package com.example.diplom.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.diplom.R
import com.example.diplom.models.RfidTag

class RfidTagsAdapter(
    private val tags: List<RfidTag>,
    private val onEditTag: (RfidTag) -> Unit,
    private val onDeleteTag: (RfidTag) -> Unit,
    private val onToggleTag: (RfidTag) -> Unit
) : RecyclerView.Adapter<RfidTagsAdapter.RfidTagViewHolder>() {

    class RfidTagViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ownerName: TextView = itemView.findViewById(R.id.owner_name)
        val tagUid: TextView = itemView.findViewById(R.id.tag_uid)
        val dateAdded: TextView = itemView.findViewById(R.id.date_added)
        val activeSwitch: Switch = itemView.findViewById(R.id.active_switch)
        val editButton: Button = itemView.findViewById(R.id.edit_button)
        val deleteButton: Button = itemView.findViewById(R.id.delete_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RfidTagViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_rfid_tag, parent, false)
        return RfidTagViewHolder(view)
    }

    override fun onBindViewHolder(holder: RfidTagViewHolder, position: Int) {
        val tag = tags[position]

        holder.ownerName.text = tag.ownerName
        holder.tagUid.text = "UID: ${tag.uid}"
        holder.dateAdded.text = "Добавлена: ${tag.dateAdded}"

        holder.activeSwitch.setOnCheckedChangeListener(null)
        holder.activeSwitch.isChecked = tag.isActive
        holder.activeSwitch.setOnCheckedChangeListener { _, _ ->
            onToggleTag(tag)
        }

        holder.editButton.setOnClickListener {
            onEditTag(tag)
        }

        holder.deleteButton.setOnClickListener {
            onDeleteTag(tag)
        }

        // Изменяем внешний вид в зависимости от активности
        val alpha = if (tag.isActive) 1.0f else 0.5f
        holder.ownerName.alpha = alpha
        holder.tagUid.alpha = alpha
        holder.dateAdded.alpha = alpha
    }

    override fun getItemCount(): Int = tags.size
}