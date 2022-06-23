package edu.wschina.flight.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import edu.wschina.flight.R
import edu.wschina.flight.activity.MainActivity
import edu.wschina.flight.databinding.DetailItemViewBinding
import edu.wschina.flight.dto.Guests
import okhttp3.*
import org.json.JSONObject
import java.io.IOException


class UserInfoAdapter(private val list: List<Guests>) :
    RecyclerView.Adapter<UserInfoAdapter.UserHolder>() {
    private lateinit var binding: DetailItemViewBinding

    inner class UserHolder(val binding: DetailItemViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun getChange(guests: Guests) {
            binding.name.setText(guests.name)
            binding.idCard.setText(guests.id_card)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserHolder {
        binding = DetailItemViewBinding.bind(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.detail_item_view, parent, false)
        )
        return UserHolder(binding)
    }

    override fun onBindViewHolder(holder: UserHolder, position: Int) =
        holder.getChange(list[position])

    override fun getItemCount(): Int = list.size
}