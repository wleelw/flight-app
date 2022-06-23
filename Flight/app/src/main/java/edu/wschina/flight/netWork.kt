package edu.wschina.flight

import com.google.gson.Gson
import edu.wschina.flight.dto.Guests
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.util.*
import java.util.concurrent.TimeUnit


private val client = OkHttpClient.Builder().apply {
    connectTimeout(10, TimeUnit.SECONDS)
    writeTimeout(10, TimeUnit.SECONDS)
    readTimeout(10, TimeUnit.SECONDS)
}.build()

private const val ROOT = "http://119.3.175.152:81/api/v1"

val gson = Gson()

object netWork {
    /**
     * 登陆验证
     * @param user 用户名
     * @param pwd 密码
     * @param callback 返回操作对象
     */
    fun LoginVerify(user: String, pwd: String, callback: Callback) {
        val body = FormBody.Builder().apply {
            add("name", user)
            add("password", pwd)
        }.build()

        val request = Request.Builder().apply {
            url("$ROOT/login")
            post(body)
        }.build()

        client.newCall(request).enqueue(callback)
    }

    /**
     * 注册验证
     * @param user 用户名
     * @param phone 手机
     * @param pwd 密码
     * @param callback 返回操作对象
     */
    fun RegisterVerify(user: String, phone: String, pwd: String, callback: Callback) {
        val body = FormBody.Builder().apply {
            add("name", user)
            add("password", pwd)
            add("phone", phone)
        }.build()

        val request = Request.Builder().apply {
            url("$ROOT/register")
            post(body)
        }.build()

        client.newCall(request).enqueue(callback)
    }

    /**
     * 获取用户信息
     * @param token 用户登录之后获取的token
     * @param callback 返回操作对象
     */
    fun getUserInfo(token: String, callback: Callback) {
        val request = Request.Builder().apply {
            url("$ROOT/self?token=$token")
            get()
        }.build()
        client.newCall(request).enqueue(callback)
    }

    /**
     * 注销登录
     * @param token 用户登录之后获取的token
     * @param callback 返回操作对象
     */
    fun logout(token: String, callback: Callback) {
        val body = FormBody.Builder().build()
        val request = Request.Builder().apply {
            url("$ROOT/logout?token=$token")
            post(body)
        }.build()
        client.newCall(request).enqueue(callback)
    }

    /**
     * 获取地名和id
     * @param callback 返回操作对象
     */
    fun getCites(callback: Callback) {
        val request = Request.Builder().apply {
            url("$ROOT/cities")
            get()
        }.build()

        client.newCall(request).enqueue(callback)
    }

    /**
     *  获取机票信息
     *  @param from 出发地id
     *  @param to 到达地id
     *  @param time 出发时间
     */
    fun FlightInfo(from: Int, to: Int, time: String?, callback: Callback) {
        val request = Request.Builder().apply {
            url("$ROOT/search?time=$time&from=$from&to=$to")
            get()
        }.build()
        client.newCall(request).enqueue(callback)
    }

    /**
     * 提交新订单
     * @param cardList 乘客信息
     * @param flight_type 舱位类型
     * @param token 用户登录时的token
     * @param flight_id 航班id
     * @param callback 返回操作对象
     */
    fun submitInfo(
        cardList: List<Guests>,
        flight_type: String,
        token: String,
        flight_id: String,
        callback: Callback
    ) {
        val guests = arrayListOf<String>()
        val json = gson.toJson(cardList)
        val mediaType = "application/json".toMediaTypeOrNull()
        val body = RequestBody.create(
            mediaType,
            "{\r\n\"token\":\"$token\",\r\n\"newGuests\":$json,\r\n\"guests\": $guests,\r\n\"flight_id\":$flight_id,\r\n\"flight_type\": \"$flight_type\"\r\n}"
        )
        val request = Request.Builder().apply {
            url("$ROOT/add_orders")
            addHeader("Content-Type", "application/json")
            post(body)
        }.build()
        client.newCall(request).enqueue(callback)
    }

    /**
     * 删除指定用户
     * @param id
     */
    fun delUser(id: String, token: String, callback: Callback) {
        val mediaType = "application/json".toMediaTypeOrNull()
        val body = RequestBody.create(
            mediaType,
            "{\r\n\"token\" : \"$token\"\r\n}"
        )
        val request: Request = Request.Builder()
            .url("http://119.3.175.152:81/api/v1/guests/$id")
            .method("DELETE", body)
            .addHeader("Content-Type", "application/json")
            .build()
        client.newCall(request).enqueue(callback)
    }

    /**
     * 修改指定用户
     * @param id
     */
    fun changeUser(id: String, name: String, id_card: String, token: String, callback: Callback) {
        val body = FormBody.Builder()
            .add("name", name)
            .add("id_card", id_card)
            .add("token", token)
            .build()
        val request: Request = Request.Builder()
            .url("http://119.3.175.152:81/api/v1/guests/$id")
            .post(body)
            .build()
        client.newCall(request).enqueue(callback)
    }

    /**
     * 修改指定用户账号信息
     * @param id
     */
    fun changeUserInfo(
        name: String,
        phone: String,
        password: String,
        token: String,
        callback: Callback
    ) {
        val body = FormBody.Builder()
            .add("name", name)
            .add("phone", phone)
            .add("token", token)
            .add("password", password)
            .build()
        val request: Request = Request.Builder()
            .url("http://119.3.175.152:81/api/v1/users")
            .post(body)
            .build()
        client.newCall(request).enqueue(callback)
    }

    /**
     * 获取已经存在的订单
     */
    fun getInfo(token: String, callback: Callback) {
        val client = OkHttpClient().newBuilder()
            .build()
        val mediaType = "text/plain".toMediaTypeOrNull()
        val body = RequestBody.create(mediaType, "")
        val request: Request = Request.Builder()
            .url("http://119.3.175.152:81/api/v1/orders?token=$token")
            .method("GET", body)
            .build()
        client.newCall(request).enqueue(callback)
    }

}