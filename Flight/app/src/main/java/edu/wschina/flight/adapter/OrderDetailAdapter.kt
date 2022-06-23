package edu.wschina.flight.adapter

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import edu.wschina.flight.R
import edu.wschina.flight.activity.MainActivity
import edu.wschina.flight.databinding.DetailItemViewBinding
import edu.wschina.flight.databinding.FlightDetailItemViewBinding
import edu.wschina.flight.databinding.OrderItemViewBinding
import edu.wschina.flight.dto.Guests
import edu.wschina.flight.dto.User
import edu.wschina.flight.gson
import edu.wschina.flight.netWork
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.nio.charset.Charset

class OrderDetailAdapter(private val list: MutableList<Guests>) :
    RecyclerView.Adapter<OrderDetailAdapter.DetailHolder>() {
    private lateinit var binding: FlightDetailItemViewBinding

    inner class DetailHolder(val binding: FlightDetailItemViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun getChange(guests: Guests) {
            binding.name.text = guests.name
            binding.card.text = guests.id_card
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailHolder {
        binding = FlightDetailItemViewBinding.bind(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.flight_detail_item_view, parent, false)
        )
        return DetailHolder(binding)
    }

    override fun onBindViewHolder(holder: DetailHolder, position: Int) {
        val activity = holder.itemView.context as Activity
        val guest = list[position]
        holder.getChange(guest)
        holder.itemView.setOnClickListener {
            val view = CardView.inflate(activity, R.layout.detail_item_view, null)
            val name = view.findViewById<TextInputEditText>(R.id.name)
            val id_card = view.findViewById<TextInputEditText>(R.id.id_card)
            name.setText(guest.name)
            id_card.setText(guest.id_card)
            MaterialAlertDialogBuilder(holder.itemView.context).apply {
                setView(view)
                setTitle("修改")
                setNegativeButton("确定") { _, _ ->
                    netWork.changeUser(
                        guest.id.toString(),
                        name.text.toString(),
                        id_card.text.toString(),
                        MainActivity.token,
                        object : Callback {
                            override fun onFailure(call: Call, e: IOException) {
                                activity.runOnUiThread {
                                    Toast.makeText(activity, "网络请求失败", Toast.LENGTH_SHORT).show()
                                }
                            }

                            override fun onResponse(call: Call, response: Response) {
                                val string = JSONObject(response.body?.string()).getString("msg")
                                val infoStr = String(string.toByteArray(), Charset.defaultCharset())
                                if (infoStr == "修改成功!") {
                                    guest.id_card = id_card.text.toString()
                                    guest.name = name.text.toString()
                                    activity.runOnUiThread {
                                        Toast.makeText(
                                            activity,
                                            "$infoStr",
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                        notifyDataSetChanged()
                                    }
                                } else {
                                    Toast.makeText(activity, string, Toast.LENGTH_SHORT).show()
                                }
                            }
                        })
                }
                setPositiveButton("取消") { _, _ ->
                }
            }.show()
        }
        holder.itemView.setOnLongClickListener {
            MaterialAlertDialogBuilder(holder.itemView.context).apply {
                setTitle("删除")
                setMessage("是否要删除该乘客？")
                setNegativeButton("确定") { _, _ ->
                    netWork.delUser(guest.id.toString(), MainActivity.token, object : Callback {
                        override fun onFailure(call: Call, e: IOException) {
                            activity.runOnUiThread {
                                Toast.makeText(activity, "网络请求失败", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onResponse(call: Call, response: Response) {
                            val string = JSONObject(response.body?.string()).getString("msg")
                            activity.runOnUiThread {
                                Toast.makeText(activity, string, Toast.LENGTH_SHORT).show()
                                if (string == "删除成功") {
                                    Toast.makeText(activity, string, Toast.LENGTH_SHORT).show()
                                    list.remove(guest)
                                    notifyDataSetChanged()
                                } else {
                                    Toast.makeText(activity, string, Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    })
                }
                setPositiveButton("取消") { _, _ -> }
            }.show()
            true
        }
    }

    override fun getItemCount(): Int = list.size
}