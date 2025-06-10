package com.example.deliverygo.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.deliverygo.Data.CourierOrder
import com.example.deliverygo.R

class CourierOrderHistoryAdapter(
    private val orders: MutableList<CourierOrder>
) : RecyclerView.Adapter<CourierOrderHistoryAdapter.HistoryViewHolder>() {

    class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val historyOrderId: TextView = itemView.findViewById(R.id.historyOrderId)
        val historyOrderAddress: TextView = itemView.findViewById(R.id.historyOrderAddress)
        val historyOrderAmount: TextView = itemView.findViewById(R.id.historyOrderAmount)
        val historyOrderStatus: TextView = itemView.findViewById(R.id.historyOrderStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_courier_order_history, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val order = orders[position]
        holder.historyOrderId.text = "Tapsyrys #${order.serverOrderId}"
        holder.historyOrderAddress.text = "Jetkızw adresi: ${order.deliveryAddress}"
        holder.historyOrderAmount.text = "Tólem: ${order.paymentAmount} tg"
        holder.historyOrderStatus.text = "Märtebe: ${order.status}"

        // Опционально: изменить цвет статуса в зависимости от его значения
        // if (order.status == "delivered") {
        //     holder.historyOrderStatus.setTextColor(holder.itemView.context.resources.getColor(android.R.color.holo_green_dark))
        // } else if (order.status == "cancelled") {
        //     holder.historyOrderStatus.setTextColor(holder.itemView.context.resources.getColor(android.R.color.holo_red_dark))
        // }
    }

    override fun getItemCount(): Int = orders.size

    fun updateOrders(newOrders: List<CourierOrder>) {
        orders.clear()
        orders.addAll(newOrders)
        notifyDataSetChanged()
    }
}
