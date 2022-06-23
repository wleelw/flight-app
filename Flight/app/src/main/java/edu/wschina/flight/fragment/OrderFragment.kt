package edu.wschina.flight.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.gson.reflect.TypeToken
import edu.wschina.flight.R
import edu.wschina.flight.activity.MainActivity
import edu.wschina.flight.adapter.OrderAdapter
import edu.wschina.flight.databinding.FragmentOrderBinding
import edu.wschina.flight.dto.Order
import edu.wschina.flight.gson
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.IOException


class OrderFragment : Fragment() {
    private val list = mutableListOf<Order>()
    private lateinit var binding: FragmentOrderBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        try {
            val adapter = OrderAdapter(list, activity?.supportFragmentManager!!)
            binding.orderList.adapter = adapter
            val client = OkHttpClient().newBuilder()
                .build()
            val request: Request = Request.Builder()
                .url("http://119.3.175.152:81/api/v1/orders?token=${MainActivity.token}")
                .get()
                .build()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    activity?.runOnUiThread {
                        Toast.makeText(activity, "网络请求失败~飞了~~", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    val string =
                        JSONObject(response.body?.string()).getJSONObject("data")
                            .getString("orders")
                    activity?.runOnUiThread {
                        val info =
                            gson.fromJson<List<Order>>(
                                string,
                                object : TypeToken<List<Order>>() {}.type
                            )
                        list.clear()
                        list.addAll(info)
                        adapter.notifyDataSetChanged()
                        if (info.isNotEmpty()) {
                            MainActivity.order = info[0]
                            val transition = activity?.supportFragmentManager?.beginTransaction()
                            transition?.replace(R.id.order_content, OrderDetailFragment())
                            transition?.commit()
                        }
                    }
                }
            })
        } catch (e: Exception) {
            Toast.makeText(activity, e.message, Toast.LENGTH_SHORT).show()
        }
    }
}