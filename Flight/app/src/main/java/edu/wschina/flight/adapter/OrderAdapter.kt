package edu.wschina.flight.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import edu.wschina.flight.R
import edu.wschina.flight.activity.MainActivity
import edu.wschina.flight.databinding.OrderItemViewBinding
import edu.wschina.flight.dto.Order
import edu.wschina.flight.fragment.OrderDetailFragment

class OrderAdapter(private val list: List<Order>, private val fragmentManager: FragmentManager) :
    RecyclerView.Adapter<OrderAdapter.OrderHolder>() {
    private lateinit var binding: OrderItemViewBinding

    companion object{
        var currentPosition = 0
    }

    inner class OrderHolder(binding: OrderItemViewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun getChange(order: Order) {
            binding.time.text = order.created_at
            binding.num.text = order.guests.size.toString()
            binding.type.text = order.flight_type
            binding.number.text = order.number
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHolder {
        binding = OrderItemViewBinding.bind(
            LayoutInflater.from(parent.context).inflate(R.layout.order_item_view, parent, false)
        )
        return OrderHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderHolder, position: Int) {
        holder.itemView.setBackgroundColor(Color.parseColor("#FFFFFF"))
        val order = list[position]
        if(currentPosition == position){
            holder.itemView.setBackgroundColor(Color.parseColor("#ECE0FD"))
        }
        holder.getChange(list[position])
        holder.itemView.setOnClickListener {
            currentPosition = position
            notifyDataSetChanged()
            MainActivity.order = order
            val transition = fragmentManager.beginTransaction()
            transition.replace(R.id.order_content, OrderDetailFragment())
            transition.commit()
        }
    }

    override fun getItemCount(): Int = list.size
}