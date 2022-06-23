package edu.wschina.flight.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.reflect.TypeToken
import edu.wschina.flight.R
import edu.wschina.flight.activity.MainActivity
import edu.wschina.flight.adapter.FlightAddAdapter
import edu.wschina.flight.adapter.OrderAdapter
import edu.wschina.flight.databinding.FragmentFlightDetailBinding
import edu.wschina.flight.dto.Flight
import edu.wschina.flight.dto.Guests
import edu.wschina.flight.dto.Order
import edu.wschina.flight.gson
import edu.wschina.flight.netWork
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.text.DecimalFormat

class FlightDetailFragment : Fragment() {
    companion object {
        lateinit var flight: Flight
        var flight_type = "economic"
    }

    /**
     * 存储身份证
     * @author card 身份者id
     * @author name 姓名
     */
    private val cardList = mutableListOf<Guests>()

    private lateinit var binding: FragmentFlightDetailBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFlightDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        try {
            val jiulist = mutableListOf<Guests>()
            val jiuadapter = FlightAddAdapter(jiulist)
            binding.yuanOrder.adapter = jiuadapter
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
                        if (info.isNotEmpty()) {
                            info.forEach {
                                if(it.flight_id == flight.id){
                                    jiulist.addAll(it.guests)
                                    jiuadapter.notifyDataSetChanged()
                                }
                            }
                        }
                    }
                }
            })
            binding.flightsInfo.layoutManager =
                LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
            val adapter = FlightAddAdapter(cardList)
            binding.flightsInfo.adapter = adapter
            binding.UserBack.setOnClickListener {
                activity?.supportFragmentManager?.popBackStack()
            }
            changeFlight()

            binding.add.setOnClickListener {
                val name = binding.name.text.toString()
                val card = binding.idCard.text.toString()
                if (name == "") {
                    binding.name.error = "不能提交空的！"
                    return@setOnClickListener
                }
                if (card == "" || card.length < 18) {
                    binding.idCard.error = "身份证格式有误"
                    return@setOnClickListener
                }
                val new = Guests(name = name, id_card = card)
                if (cardList.contains(new)) {
                    Toast.makeText(activity, "已经添加过了", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                cardList.add(new)
                adapter.notifyDataSetChanged()
            }

            binding.flightTypes.setOnCheckedChangeListener(object :
                RadioGroup.OnCheckedChangeListener {
                override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
                    when (checkedId) {
                        R.id.economic -> {
                            flight_type = "economic"
                        }
                        R.id.business -> {
                            flight_type = "business"
                        }
                        R.id.first -> {
                            flight_type = "first"
                        }
                    }
                }

            })

            if (flight.first_count == 0 || flight.first_count == null) binding.first.isEnabled =
                false
            if (flight.business_count == 0 || flight.business_count == null) binding.business.isEnabled =
                false
            if (flight.economic_count == 0 || flight.economic_count == null) binding.business.isEnabled =
                false

            binding.submit.setOnClickListener {
                netWork.submitInfo(
                    cardList,
                    flight_type,
                    MainActivity.token,
                    flight.id.toString(),
                    object : okhttp3.Callback {
                        override fun onFailure(call: Call, e: IOException) {
                            activity?.runOnUiThread {
                                Toast.makeText(activity, "网络请求失败~飞了~~", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onResponse(call: Call, response: Response) {
                            val string = response.body?.string()
                            try {
                                val json = JSONObject(string)
                                if (json.getString("msg") == "添加成功") {
                                    activity?.runOnUiThread {
                                        Toast.makeText(
                                            activity,
                                            json.getString("msg"),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        jiulist.addAll(cardList)
                                        jiuadapter.notifyDataSetChanged()
                                        cardList.clear()
                                        adapter.notifyDataSetChanged()
                                    }
                                } else {
                                    activity?.runOnUiThread {
                                        Toast.makeText(
                                            activity,
                                            json.getString("msg"),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            } catch (e: Exception) {
                                activity?.runOnUiThread {
                                    Toast.makeText(
                                        activity,
                                        "提交失败",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }

                    })
            }
        } catch (e: Exception) {
            Toast.makeText(activity, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * 初始化机票
     */
    private fun changeFlight() {
        if (flight == null) return
        binding.flightId.text = flight.name
        binding.flightTime.text = flight.start_time
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