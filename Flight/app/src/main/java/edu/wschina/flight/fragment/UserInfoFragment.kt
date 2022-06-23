package edu.wschina.flight.fragment

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import edu.wschina.flight.R
import edu.wschina.flight.activity.MainActivity
import edu.wschina.flight.adapter.OrderDetailAdapter
import edu.wschina.flight.adapter.UserInfoAdapter
import edu.wschina.flight.databinding.FragmentHomeBinding
import edu.wschina.flight.databinding.FragmentUserInfoBinding
import edu.wschina.flight.dto.User
import edu.wschina.flight.gson
import edu.wschina.flight.netWork
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.nio.charset.Charset

class UserInfoFragment : Fragment() {
    private lateinit var binding: FragmentUserInfoBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        try {
            val shared = activity?.getSharedPreferences("login_info", Activity.MODE_PRIVATE)
            val password = shared?.getString("password", "")

            val token = MainActivity.token
            if (token.isEmpty()) return

            netWork.getUserInfo(token, object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    activity?.runOnUiThread {
                        Toast.makeText(activity, "网络请求失败", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    try {
                        val string = response.body?.string()
                        val json = JSONObject(string)
                        val userJson = json.getJSONObject("data").getString("user")
                        val user = gson.fromJson(userJson, User::class.java)
                        activity?.runOnUiThread {
                            binding.userName.setText(user.name)
                            binding.phone.setText(user.phone)
                            binding.password.setText(password)
                            binding.rev.adapter = OrderDetailAdapter(user.guests)
                        }
                    } catch (e: Exception) {
                        activity?.runOnUiThread {
                            Toast.makeText(activity, e.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            })
            binding.submitUserInfo.setOnClickListener {
                val name = binding.userName.text.toString()
                val phone = binding.phone.text.toString()
                val password = binding.password.text.toString()
                netWork.changeUserInfo(
                    name = name,
                    phone = phone,
                    password = password,
                    token = token,
                    object : Callback {
                        override fun onFailure(call: Call, e: IOException) {
                            activity?.runOnUiThread {
                                Toast.makeText(activity, "网络请求失败", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onResponse(call: Call, response: Response) {
                            val string = response.body?.string().toString()
                            val json = JSONObject(string).getString("msg")
                            val infoStr = String(json.toByteArray(), Charset.defaultCharset())
                            activity?.runOnUiThread {
                                Toast.makeText(activity, infoStr, Toast.LENGTH_SHORT).show()
                            }
                        }
                    })
            }
        } catch (e: Exception) {

        }
    }
}