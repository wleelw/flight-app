package edu.wschina.flight.fragment

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.reflect.TypeToken
import edu.wschina.flight.R
import edu.wschina.flight.adapter.AirAdapter
import edu.wschina.flight.databinding.FragmentHomeBinding
import edu.wschina.flight.dto.City
import edu.wschina.flight.dto.Flight
import edu.wschina.flight.gson
import edu.wschina.flight.netWork
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.text.DateFormat
import java.text.SimpleDateFormat

class HomeFragment : Fragment() {
    val Cities = mutableMapOf<String, Int>()
    private lateinit var binding: FragmentHomeBinding
    private val flights = mutableListOf<Flight>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.mainRev.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        val flightAdapter = AirAdapter(flights,activity?.supportFragmentManager!!)
        binding.mainRev.adapter = flightAdapter

        val array = arrayListOf<String>()

        binding.startTime.setOnClickListener {
            val datedialog = DatePickerDialog(requireContext())
            datedialog.datePicker.minDate = System.currentTimeMillis()
            datedialog.setOnDateSetListener(object : DatePickerDialog.OnDateSetListener {
                override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
                    binding.startTime.text = "$year-$month-$dayOfMonth"
                }
            })
            datedialog.show()
        }

        netWork.getCites(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                activity?.runOnUiThread {
                    Toast.makeText(activity, "网络请求失败~飞了~~", Toast.LENGTH_SHORT).show()
                    binding.progress.visibility = View.GONE
                }
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    val jsonArray =
                        JSONObject(response.body?.string()).getJSONObject("data")
                            .getJSONArray("cities")
                    for (i in 0 until jsonArray.length()) {
                        val name = jsonArray.getJSONObject(i).getString("name")
                        val id = jsonArray.getJSONObject(i).getInt("id")
                        Cities[name] = id
                        array.add(name)
                    }
                    activity?.runOnUiThread {
                        val adapter = ArrayAdapter<String>(context!!, R.layout.spinner_text, array)
                        binding.from.adapter = adapter
                        binding.to.adapter = adapter
                        binding.progress.visibility = View.GONE
                    }
                } catch (e: Exception) {
                    activity?.runOnUiThread {
                        Toast.makeText(activity, "获取失败", Toast.LENGTH_SHORT).show()
                        binding.progress.visibility = View.GONE
                    }
                }
            }

        })

        binding.cancelButtonTimer.setOnClickListener {
            binding.startTime.text = "0000-00-00"
        }

        binding.homeButtonSearch.setOnClickListener {
            binding.progress.visibility = View.VISIBLE
            val from = binding.from.selectedItem.toString()
            val to = binding.to.selectedItem.toString()
            if (from != to) {
                val fromId = Cities[from] ?: 0
                val toId = Cities[to] ?: 0
                val time =
                    if (binding.startTime.text.toString() == "0000-00-00") "" else binding.startTime.text.toString()
                netWork.FlightInfo(from = fromId, to = toId, time = time, object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        activity?.runOnUiThread {
                            Toast.makeText(context, "请求失败了", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onResponse(call: Call, response: Response) {
                        try {
                            val json =
                                JSONObject(response.body?.string()).getJSONObject("data")
                                    .getString("flights")

                            val info = gson.fromJson<List<Flight>>(
                                json,
                                object : TypeToken<List<Flight>>() {}.type
                            )
                            flights.clear()
                            flights.addAll(info)

                            activity?.runOnUiThread {
                                flightAdapter.notifyDataSetChanged()
                                binding.progress.visibility = View.GONE
                            }
                        } catch (e: Exception) {
                            activity?.runOnUiThread {
                                Toast.makeText(activity, "请求繁忙", Toast.LENGTH_SHORT).show()
                                binding.progress.visibility = View.GONE
                            }
                        }
                    }

                })
            } else {
                binding.progress.visibility = View.GONE
                Toast.makeText(activity, "你选择的出发地和到达地不能相同", Toast.LENGTH_SHORT).show()
            }
        }
    }
}