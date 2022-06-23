package edu.wschina.flight.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import edu.wschina.flight.R
import edu.wschina.flight.activity.MainActivity
import edu.wschina.flight.adapter.OrderDetailAdapter
import edu.wschina.flight.databinding.FragmentOrderDetailBinding

class OrderDetailFragment : Fragment() {
    private lateinit var binding: FragmentOrderDetailBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrderDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        try {
            binding.flightId.text = MainActivity.order.flight_id.toString()
            binding.flightFrom.text = MainActivity.order.flight.from.name
            binding.flightTo.text = MainActivity.order.flight.to.name
            binding.flightTime.text = MainActivity.order.created_at
            binding.orderDetailRev.layoutManager =
                LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
            binding.orderDetailRev.layoutManager = GridLayoutManager(activity, 2)
            binding.orderDetailRev.adapter = OrderDetailAdapter(MainActivity.order.guests)
        } catch (e: Exception) {
            Toast.makeText(activity, e.message, Toast.LENGTH_SHORT).show()
        }
    }
}