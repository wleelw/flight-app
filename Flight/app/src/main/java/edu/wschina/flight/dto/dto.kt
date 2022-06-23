package edu.wschina.flight.dto

data class User(
    val id: Int,
    val name: String,
    val phone: String,
    val guests: ArrayList<Guests>
)

data class Guests(
    val id: Int = 0,
    var name: String,
    var id_card: String,
    val laravel_through_key: Int = 0
)

data class City(val id: Int, val name: String, val slug: String)

data class Flight(
    val id: Int,
    val price: String,
    val name: String,
    val start_time: String,
    val first_count: Int?,
    val business_count: Int?,
    val economic_count: Int?,
    val from: City,
    val to: City,
    val flight_type: FlightType,
)

data class FlightType(
    val id: Int,
    val name: String,
    val first: Int?,
    val business: Int?,
    val economic: Int?,
)

data class Order(
    val id: Int,
    val flight_id: Int,
    val flight_type: String,
    val user_id: Int,
    val guests_number: Int,
    val number: String,
    val created_at: String,
    val guests: MutableList<Guests>,
    val flight: Flight,
)