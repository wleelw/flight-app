package edu.wschina.flight.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import edu.wschina.flight.databinding.ActivityRegisterBinding
import edu.wschina.flight.netWork
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.registerButtonRegister.setOnClickListener {
            binding.progress.visibility = View.VISIBLE
            val user = binding.registerEditUser.text.toString()
            val phone = binding.registerEditPhone.text.toString()
            Log.d("tag", "onCreate: $phone")
            val pwd1 = binding.registerEditPassword1.text.toString()
            val pwd2 = binding.registerEditPassword2.text.toString()
            if (user.isNotEmpty() && pwd1.isNotEmpty() && phone.isNotEmpty() && pwd2.isNotEmpty()) {

                if (pwd1 != pwd2) {
                    Toast.makeText(this, "两次密码不一致", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                // 输入完整，进行登录验证
                netWork.RegisterVerify(user = user, phone = phone, pwd = pwd1, object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        runOnUiThread {
                            binding.progress.visibility = View.INVISIBLE
                            Toast.makeText(this@RegisterActivity, "网络请求失败~飞了~~", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }

                    override fun onResponse(call: Call, response: Response) {
                        val json = JSONObject(response.body?.string())
                        if (json.getString("msg") == "注册成功") {
                            val token = json.getJSONObject("data").getString("token")
                            val shared = getSharedPreferences("login_info", MODE_PRIVATE)
                            shared.edit().apply {
                                putString("token", token)
                                putString("name", user)
                            }.apply()
                            runOnUiThread {
                                binding.progress.visibility = View.INVISIBLE
                                Toast.makeText(
                                    this@RegisterActivity,
                                    "注册成功，$user",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                finish()
                            }
                        } else {
                            runOnUiThread {
                                binding.progress.visibility = View.INVISIBLE
                                Toast.makeText(
                                    this@RegisterActivity,
                                    json.getString("msg"),
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        }
                    }

                })
            } else {
                binding.progress.visibility = View.INVISIBLE
                Toast.makeText(this, "请填写完整信息", Toast.LENGTH_SHORT).show()
            }
        }
    }
}