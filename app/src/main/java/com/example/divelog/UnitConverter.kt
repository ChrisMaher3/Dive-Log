object UnitConverter {

    // Conversion functions for meters/feet
    fun metersToFeet(meters: Float): Float {
        return meters * 3.28084f
    }

    fun feetToMeters(feet: Float): Float {
        return feet / 3.28084f
    }

    // Conversion functions for Celsius/Fahrenheit
    fun celsiusToFahrenheit(celsius: Float): Float {
        return (celsius * 9/5) + 32
    }

    fun fahrenheitToCelsius(fahrenheit: Float): Float {
        return (fahrenheit - 32) * 5/9
    }
}
