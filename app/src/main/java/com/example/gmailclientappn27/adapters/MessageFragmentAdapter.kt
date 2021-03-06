package com.example.gmailclientappn27.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.gmailclientappn27.R
import com.example.gmailclientappn27.database.Messages
import com.example.gmailclientappn27.databinding.RecyclerViewLayoutBinding
import com.example.gmailclientappn27.models.UserMessagesModel


class MessageFragmentAdapter(
    val saveData: (messageId: String, attachmentId: String, filename: String) -> Unit
) :
    ListAdapter<UserMessagesModel, MessageFragmentAdapter.ItemViewHolder>(DiffCallback) {
    companion object DiffCallback : DiffUtil.ItemCallback<UserMessagesModel>() {

        override fun areItemsTheSame(oldItem: UserMessagesModel, newItem: UserMessagesModel): Boolean {
            return oldItem.messageId == newItem.messageId
        }

        override fun areContentsTheSame(oldItem: UserMessagesModel, newItem: UserMessagesModel): Boolean {
            return oldItem == newItem
        }
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val binding = RecyclerViewLayoutBinding.bind(itemView)

        fun bind(item: UserMessagesModel) {
            binding.fromFieldTextView.text = item.from
            binding.dateFieldTextView.text = item.date
            binding.subjectFieldTextView.text = item.subject

            binding.attachImageView.setOnClickListener {
                Log.d("asd", "clicked")
                saveData(item.messageId,item.attachmentId,item.filename)
            }
            if (item.attachmentId == "") {
                binding.attachImageView.visibility = View.INVISIBLE

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_view_layout, parent, false)
        return ItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}
