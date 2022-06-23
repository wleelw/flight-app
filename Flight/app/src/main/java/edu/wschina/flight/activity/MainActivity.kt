package edu.wschina.flight.activity

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.core.view.GravityCompat
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.dialog.MaterialDialogs
import com.google.android.material.navigation.NavigationView
import edu.wschina.flight.R
import edu.wschina.flight.databinding.ActivityMainBinding
import edu.wschina.flight.databinding.MainNavHeadViewBinding
import edu.wschina.flight.dto.Order
import edu.wschina.flight.fragment.HomeFragment
import edu.wschina.flight.fragment.OrderFragment
import edu.wschina.flight.fragment.UserInfoFragment
import edu.wschina.flight.netWork
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

class MainActivity : AppCompatActivity() {
    companion object {
        var token = ""
        lateinit var order: Order
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var headViewBinding: MainNavHeadViewBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        headViewBinding = MainNavHeadViewBinding.bind(binding.drawerNav.getHeaderView(0))
        setContentView(binding.root)
        val shared = getSharedPreferences("login_info", MODE_PRIVATE)
        token = shared.getString("token", "")!!
        val name = shared.getString("name", "")
        headViewBinding.userName.text = name

        val transition = supportFragmentManager.beginTransaction()
        transition.replace(R.id.main_content, HomeFragment())
        transition.commit()
        binding.drawerNav.setNavigationItemSelectedListener(NavigationView.OnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_logout -> {
                    MaterialAlertDialogBuilder(this).apply {
                        setMessage("是否要退出？")
                        setNegativeButton("退出") { _, _ ->
                            netWork.logout(token, object : Callback {
                                override fun onFailure(call: Call, e: IOException) {

                                }

                                override fun onResponse(call: Call, response: Response) {
                                    runOnUiThread {
                                        startActivity(
                                            Intent(
                                                this@MainActivity,
                                                LoginActivity::class.java
                                            )
                                        )
                                        finish()
                                    }
                                }

                            })
                        }
                        setPositiveButton("取消") { _, _ ->
                        }
                    }.show()
                }
                R.id.nav_home -> {
                    val transition = supportFragmentManager.beginTransaction()
                    transition.replace(R.id.main_content, HomeFragment())
                    transition.commit()
                }
                R.id.nav_order -> {
                    val transition = supportFragmentManager.beginTransaction()
                    transition.replace(R.id.main_content, OrderFragment())
                    transition.commit()
                }
                R.id.user_info ->{
                    val transition = supportFragmentManager.beginTransaction()
                    transition.replace(R.id.main_content, UserInfoFragment())
                    transition.commit()
                }
            }
            true
        })
    }
}