package edu.wschina.flight.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN
import androidx.recyclerview.widget.RecyclerView
import edu.wschina.flight.R
import edu.wschina.flight.databinding.TitckItemViewBinding
import edu.wschina.flight.dto.Flight
import edu.wschina.flight.fragment.FlightDetailFragment
import java.text.DecimalFormat

class AirAdapter(private val list: List<Flight>, val fragmentManager: FragmentManager) :
    RecyclerView.Adapter<AirAdapter.AirHolder>() {
    lateinit var binding: TitckItemViewBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AirHolder {
        binding = TitckItemViewBinding.bind(
            LayoutInflater.from(parent.context).inflate(R.layout.titck_item_view, parent, false)
        )
        return AirHolder(binding)
    }

    override fun onBindViewHolder(holder: AirHolder, position: Int) {
        holder.getChange(list[position])
    }

    override fun getItemCount(): Int = list.size
    inner class AirHolder(private val binding: TitckItemViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun getChange(flight: Flight) {
            binding.root.rootView.setOnClickListener {
                val transition = fragmentManager.beginTransaction()
                transition.replace(R.id.main_content, FlightDetailFragment())
                transition.setTransition(TRANSIT_FRAGMENT_OPEN)
                transition.addToBackStack(null)
                FlightDetailFragment.flight = flight
                transition.commit()
            }
            binding.flightId.text = flight.name
            binding.flightTime.text = flight.start_time
            binding.flightFrom.text = flight.from.name
            binding.flightTo.text = flight.to.name
            binding.flightEconomic.text =
                if (flight.economic_count != null) flight.economic_count.toString() else "无票"
            binding.flightEconomicPrice.text = flight.price
            if (flight.economic_count == null) {
                binding.flightEconomicPrice.text = "暂无价格"
            }
            binding.flightBusiness.text =
                if (flight.business_count != null) flight.business_count.toString() else "无票"
            val df = DecimalFormat("######.##")
            binding.flightBusinessPrice.text =
                df.format((flight.price.toDouble() + flight.price.toDouble() * 0.3))
            if (flight.business_count == null) {
                binding.flightBusinessPrice.text = "暂无价格"
            }
            binding.flightFirst.text =
                if (flight.first_count != null) flight.first_count.toString() else "无票"
            binding.flightFirstPrice.text =
                df.format((flight.price.toDouble() + flight.price.toDouble() * 0.5))
            if (flight.first_count == null) {
                binding.flightFirstPrice.text = "暂无价格"
            }
        }
    }
}