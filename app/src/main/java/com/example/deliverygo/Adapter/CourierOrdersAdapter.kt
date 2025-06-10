package com.example.deliverygo.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.deliverygo.Data.CourierOrder
import com.example.deliverygo.R

class CourierOrdersAdapter(
    private val orders: MutableList<CourierOrder>,
    private val onAcceptClickListener: (CourierOrder) -> Unit
) : RecyclerView.Adapter<CourierOrdersAdapter.OrderViewHolder>() {

    class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val orderIdTextView: TextView = itemView.findViewById(R.id.orderIdTextView)
        val pickupAddressTextView: TextView = itemView.findViewById(R.id.pickupAddressTextView)
        val deliveryAddressTextView: TextView = itemView.findViewById(R.id.deliveryAddressTextView)
        val paymentAmountTextView: TextView = itemView.findViewById(R.id.paymentAmountTextView)
        val distanceTextView: TextView = itemView.findViewById(R.id.distanceTextView)
        val acceptOrderBtn: Button = itemView.findViewById(R.id.acceptOrderBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_available_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        holder.orderIdTextView.text = "Tapsyrys #${order.serverOrderId}"
        holder.pickupAddressTextView.text = "Alw adresi: ${order.pickupAddress}"
        holder.deliveryAddressTextView.text = "Jetkızw adresi: ${order.deliveryAddress}"
        holder.paymentAmountTextView.text = "Tólem: ${order.paymentAmount} tg"
        holder.distanceTextView.text = "Qashyqtyq: ${order.distanceKm} km"

        holder.acceptOrderBtn.setOnClickListener {
            onAcceptClickListener(order)
        }
    }

    override fun getItemCount(): Int = orders.size

    // Метод для обновления данных в адаптере
    fun updateOrders(newOrders: List<CourierOrder>) {
        orders.clear()
        orders.addAll(newOrders)
        notifyDataSetChanged()
    }
}
