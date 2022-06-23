package edu.wschina.flight.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import edu.wschina.flight.R
import edu.wschina.flight.databinding.FlightDetailItemViewBinding
import edu.wschina.flight.dto.Guests

class FlightAddAdapter(private val list: MutableList<Guests>) :
    RecyclerView.Adapter<FlightAddAdapter.AddHolder>() {
    private lateinit var binding: FlightDetailItemViewBinding

    inner class AddHolder(binding: FlightDetailItemViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun getChange(flightCard: Guests) {
            binding.name.text = flightCard.name
            binding.card.text = flightCard.id_card
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddHolder {
        binding = FlightDetailItemViewBinding.bind(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.flight_detail_item_view, parent, false)
        )
        return AddHolder(binding)
    }

    override fun onBindViewHolder(holder: AddHolder, position: Int) {
        val flight = list[position]
        holder.getChange(flight)
        holder.itemView.setOnLongClickListener {
            MaterialAlertDialogBuilder(holder.itemView.context).apply {
                setTitle("是否要删除该成员？")
                setNegativeButton(
                    "是"
                ) { _, _ ->
                    list.remove(flight)
                    notifyDataSetChanged()
                }
                setPositiveButton("否") { _, _ -> }
            }.show()
            true
        }
    }

    override fun getItemCount(): Int = list.size
}