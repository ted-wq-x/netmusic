package wangqiang

import java.util.*


/**
 * Created by wangq on 2017/5/23.
 */
class WeatherData: Observable(){
    fun getTemperature(): Unit {

    }

    fun getPressure(): Unit {

    }

    fun getHumidity(): Unit {

    }
}

open class StatisticsDisplay : Observer {
    override fun update(o: Observable?, arg: Any?) {
        println()
    }
}

fun main(args: Array<String>) {
    val weatherData = WeatherData()
    weatherData.addObserver(StatisticsDisplay())
}