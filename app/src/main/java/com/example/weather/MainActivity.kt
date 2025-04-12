package com.example.weather

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView

import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.weather.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class MainActivity : AppCompatActivity() {
    private val binding:ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets

        }
        fetchWeatherData("Jaipur")
        searchCity()

    }
    private fun searchCity(){
        val searchView=binding.searchView
        searchView.isIconified=false
        searchView.clearFocus()
        searchView.setOnQueryTextListener(object : SearchView .OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherData(query)
                    searchView.clearFocus()
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })




    }
    private fun fetchWeatherData(cityname:String){
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)
        val response=retrofit.getWeatherData(cityname,"f15a89ae8baedc16f44d5a5720fcef71","metric")

         response.enqueue(object :Callback<WeatherApp>{
         @SuppressLint("SuspiciousIndentation")
         override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
        val responseBody =response.body()
             if(response.isSuccessful){
                 val temperature= responseBody?.main?.temp.toString()
                 val humidity = responseBody?.main?.humidity
                 val sunrise=responseBody?.sys!!.sunrise.toLong()
                 val sunset= responseBody?.sys!!.sunset.toLong()
                 val windSpeed= responseBody.wind.speed
                 val seaLevel=responseBody.main.pressure
                 val condition=responseBody.weather.firstOrNull()?.main?:"unknown"
                 val maxTemp= responseBody.main.temp_max
                 val minTemp= responseBody.main.temp_min
                 binding.temp.text="$temperature °C"
                 binding.weather.text=condition
                 binding.maxTemp.text="Max temp : $maxTemp °C"
                 binding.minTemp.text="Min temp : $minTemp °C"
                 binding.humidity.text="$humidity %"
                 binding.sunset.text="${time(sunset)}"
                 binding.sunrise.text="${time(sunrise)}"
                 binding.windspeed.text="$windSpeed m/s"
                 binding.sea.text="$seaLevel hPa"
                 binding.conditions.text=condition
                 binding.date.text=date()
                 binding.day.text=dayName(System.currentTimeMillis())
                 binding.cityName.text="$cityname"
                 changeImage(condition)
             }
     }

             override fun onFailure(call: Call<WeatherApp>, t: Throwable) {
                 Toast.makeText(this@MainActivity, "Failed to get weather: ${t.message}", Toast.LENGTH_SHORT).show()
             }


         })


    }

    private fun changeImage(conditions:String) {
        when(conditions){
            "Clear Sky","Sunny","Clear"->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)

            }
            "Partly Clouds","Clouds","Overcast","Mist","Foggy"->{
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)

            }
            "Light Rain","Drizzle","Moderate Rain","Showers","Heavy Rain"->{
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.lottieAnimationView.setAnimation(R.raw.rain)

            }
            "Light Snow","Moderate Snow","Heavy Snow","Blizzard"->{
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.lottieAnimationView.setAnimation(R.raw.snow)

            }
            else ->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)

            }
        }
        binding.lottieAnimationView.playAnimation()

    }

    fun dayName(timestamp:Long):String{
        val sdf=SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format((Date()))

    }
    private fun date():String{
        val sdf=SimpleDateFormat("dd mm yyyy", Locale.getDefault())
        return sdf.format((Date()))

    }
    private fun time(timestamp: Long):String{
        val sdf=SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format((Date(timestamp*1000)))}
}