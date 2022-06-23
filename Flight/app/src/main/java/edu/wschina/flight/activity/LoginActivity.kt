package edu.wschina.flight.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import edu.wschina.flight.databinding.ActivityLoginBinding
import edu.wschina.flight.netWork
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val shared = getSharedPreferences("login_info", MODE_PRIVATE)
        binding.loginEditUser.setText(shared.getString("name", ""))

        binding.loginButtonRegister.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    RegisterActivity::class.java
                )
            )
        }
        binding.loginButtonLogin.setOnClickListener {
            binding.progress.visibility = View.VISIBLE
            val user = binding.loginEditUser.text.toString()
            val pwd = binding.loginEditPassword.text.toString()
            if (user.isNotEmpty() && pwd.isNotEmpty()) {
                // 输入完整，进行登录验证
                netWork.LoginVerify(user, pwd, object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        runOnUiThread {
                            binding.progress.visibility = View.INVISIBLE
                            Toast.makeText(this@LoginActivity, "网络请求失败~飞了~~", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }

                    override fun onResponse(call: Call, response: Response) {
                        val json = JSONObject(response.body?.string())
                        if (json.getString("msg") == "登录成功") {
                            val token = json.getJSONObject("data").getString("token")
                            val shared = getSharedPreferences("login_info", MODE_PRIVATE)
                            shared.edit().apply {
                                putString("token", token)
                                putString("name", user)
                                putString("password",pwd)
                            }.apply()
                            runOnUiThread {
                                binding.progress.visibility = View.INVISIBLE
                                Toast.makeText(this@LoginActivity, "欢迎登陆，$user", Toast.LENGTH_SHORT)
                                    .show()
                                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                                finish()
                            }
                        } else {
                            runOnUiThread {
                                binding.progress.visibility = View.INVISIBLE
                                Toast.makeText(
                                    this@LoginActivity,
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