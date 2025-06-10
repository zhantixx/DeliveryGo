package com.example.deliverygo.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.deliverygo.Data.OrderEntity
import com.example.deliverygo.R
import com.example.deliverygo.databinding.OrderItemBinding

class OrderAdapter(private val onDeleteClick: (OrderEntity) -> Unit) :
    ListAdapter<OrderEntity, OrderAdapter.OrderViewHolder>(DiffCallback()) {

    inner class OrderViewHolder(val binding: OrderItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = OrderItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = getItem(position)
        holder.binding.productTitle.text = order.productTitle
        holder.binding.productPrice.text = "${order.productPrice} tg"
        Glide.with(holder.binding.productImage.context).load(order.productImage).into(holder.binding.productImage)

        holder.binding.returnBtn.setOnClickListener {
            val context = holder.itemView.context
            val dialogView = LayoutInflater.from(context).inflate(R.layout.return_form, null)

            val dialog = android.app.AlertDialog.Builder(context)
                .setView(dialogView)
                .create()

            val nameEditText = dialogView.findViewById<EditText>(R.id.etName)
            val phoneEditText = dialogView.findViewById<EditText>(R.id.etPhone)
            val reasonEditText = dialogView.findViewById<EditText>(R.id.etReason)
            val submitBtn = dialogView.findViewById<Button>(R.id.btnSubmitReturn)

            submitBtn.setOnClickListener {
                val name = nameEditText.text.toString().trim()
                val phone = phoneEditText.text.toString().trim()
                val reason = reasonEditText.text.toString().trim()

                if (name.isEmpty() || phone.isEmpty() || reason.isEmpty()) {
                    Toast.makeText(context, "Barlyq oristerdi toltyrynyz", Toast.LENGTH_SHORT).show()
                } else {
                    dialog.dismiss()
                    Toast.makeText(context, "Qaitaru suranysy jiberildi", Toast.LENGTH_SHORT).show()

                    onDeleteClick(order)
                }
            }

            dialog.show()
        }

    }

    class DiffCallback : DiffUtil.ItemCallback<OrderEntity>() {
        override fun areItemsTheSame(oldItem: OrderEntity, newItem: OrderEntity): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: OrderEntity, newItem: OrderEntity): Boolean = oldItem == newItem
    }

}
