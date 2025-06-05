package com.chris.divelog

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import kotlin.math.roundToInt

class DiverPlannerFragment : Fragment() {

    // Nitrox Planner UI elements
    private lateinit var depthInput: EditText
    private lateinit var oxygenInput: EditText
    private lateinit var resultText: TextView
    private lateinit var calculateButton: Button

    // Gas Consumption Calculator UI elements
    private lateinit var tankSizeInput: EditText
    private lateinit var startPressureInput: EditText
    private lateinit var endPressureInput: EditText
    private lateinit var targetDepthInput: EditText
    private lateinit var sacRateInput: EditText
    private lateinit var gasResultText: TextView
    private lateinit var calculateGasButton: Button

    // Hardcoded no-deco limits for air per depth (meters) for Nitrox planner
    private val airNDLTable = mapOf(
        12 to 125,
        15 to 75,
        18 to 47,
        21 to 34,
        24 to 25,
        27 to 20,
        30 to 17,
        33 to 14,
        36 to 12,
        39 to 10,
        42 to 9,
        45 to 9,
        48 to 6,
        51 to 5
    )

    private val maxPO2 = 1.4  // Max partial pressure O2 in bar for Nitrox (Diving Ireland max)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_diver_planner, container, false)

        // Initialize Nitrox Planner UI elements
        depthInput = view.findViewById(R.id.depthInput)
        oxygenInput = view.findViewById(R.id.oxygenInput)
        resultText = view.findViewById(R.id.resultText)
        calculateButton = view.findViewById(R.id.calculateButton)

        // Initialize Gas Consumption Calculator UI elements
        tankSizeInput = view.findViewById(R.id.tankSizeInput)
        startPressureInput = view.findViewById(R.id.startPressureInput)
        endPressureInput = view.findViewById(R.id.endPressureInput)
        targetDepthInput = view.findViewById(R.id.targetDepthInput)
        sacRateInput = view.findViewById(R.id.sacRateInput)
        gasResultText = view.findViewById(R.id.gasResultText)
        calculateGasButton = view.findViewById(R.id.calculateGasButton)

        // Set OnClickListener for Nitrox Calculator button
        calculateButton.setOnClickListener { calculateNitroxDive() } // Renamed to clarify

        // Set OnClickListener for Gas Consumption Calculator button
        calculateGasButton.setOnClickListener { calculateGasConsumption() }

        return view
    }

    /**
     * Calculates Nitrox dive parameters (PO2, MOD, EAD, NDL).
     */
    private fun calculateNitroxDive() { // Renamed from calculateDive() for clarity
        try {
            val depth = depthInput.text.toString().toInt()
            val oxygenPercent = oxygenInput.text.toString().toInt()

            // Input validation for Nitrox Planner
            if (depth < 0 || depth > 51) {
                showNitroxError("Depth must be between 0 and 51 meters.")
                return
            }
            if (oxygenPercent < 21 || oxygenPercent > 40) {
                showNitroxError("Oxygen % must be between 21 and 40.")
                return
            }

            // Calculate PO2 at depth
            val ambientPressure = depth / 10.0 + 1.0 // bar
            val oxygenFraction = oxygenPercent / 100.0
            val po2 = ambientPressure * oxygenFraction

            // Check PO2 limit
            if (po2 > maxPO2) {
                showNitroxWarning("Warning: PO2 (${String.format("%.2f", po2)} bar) exceeds max safe limit of $maxPO2 bar.\nReduce depth or oxygen %.")
                return
            }

            // Calculate Maximum Operating Depth (MOD) for given oxygen mix
            val mod = ((maxPO2 / oxygenFraction) - 1.0) * 10.0

            // Calculate Equivalent Air Depth (EAD)
            val ead = calculateEAD(depth, oxygenFraction)

            // Get NDL based on EAD (rounded up conservatively)
            val ndl = getNDLForAir(kotlin.math.ceil(ead).toInt())

            if (ndl == -1) {
                showNitroxError("Depth exceeds recreational dive limits.")
                return
            }

            // Build result message with color-coded safety info
            val builder = StringBuilder()
            builder.append("Depth: $depth m\n")
            builder.append("Oxygen %: $oxygenPercent%\n")
            builder.append("PO2 at depth: ${"%.2f".format(po2)} bar\n")
            builder.append("Max Operating Depth (MOD): ${"%.1f".format(mod)} m\n")
            builder.append("Equivalent Air Depth (EAD): ${"%.1f".format(ead)} m\n")
            builder.append("Max No-Decompression Limit: $ndl minutes\n")
            builder.append("\nSafety Stop: Recommended 3 min at 5 m\n")

            // Set text color based on safety for Nitrox planner
            when {
                po2 > maxPO2 -> resultText.setTextColor(Color.RED)
                depth > mod -> resultText.setTextColor(Color.RED)
                ndl < 10 -> resultText.setTextColor(Color.parseColor("#FFA500")) // Orange for caution
                else -> resultText.setTextColor(Color.GREEN)
            }

            resultText.text = builder.toString()

        } catch (e: NumberFormatException) {
            showNitroxError("Please enter valid numeric values for depth and oxygen %.")
        }
    }

    /**
     * Calculates Equivalent Air Depth (EAD) for Nitrox.
     * @param depth The actual depth of the dive in meters.
     * @param oxygenFraction The fraction of oxygen in the gas mix (e.g., 0.32 for 32% O2).
     * @return The Equivalent Air Depth in meters.
     */
    private fun calculateEAD(depth: Int, oxygenFraction: Double): Double {
        val nitrogenFraction = 1.0 - oxygenFraction
        val ambientPressure = depth / 10.0 + 1.0 // in bar
        val eadBar = ((nitrogenFraction * (ambientPressure - 1.0)) / 0.79) + 1.0
        return (eadBar - 1.0) * 10.0
    }

    /**
     * Retrieves the No-Decompression Limit (NDL) for a given EAD from the hardcoded air NDL table.
     * @param depth The Equivalent Air Depth (EAD) in meters.
     * @return The NDL in minutes, or -1 if depth exceeds recreational limits.
     */
    private fun getNDLForAir(depth: Int): Int {
        val depths = airNDLTable.keys.sorted()

        for (d in depths) {
            if (depth <= d) {
                return airNDLTable[d] ?: -1
            }
        }
        return -1 // Exceeds limits
    }

    /**
     * Displays an error message for the Nitrox planner.
     * @param message The error message to display.
     */
    private fun showNitroxError(message: String) {
        resultText.setTextColor(Color.RED)
        resultText.text = message
    }

    /**
     * Displays a warning message for the Nitrox planner.
     * @param message The warning message to display.
     */
    private fun showNitroxWarning(message: String) {
        resultText.setTextColor(Color.parseColor("#FFA500")) // Orange color
        resultText.text = message
    }

    /**
     * Calculates gas consumption and estimated dive time.
     */
    private fun calculateGasConsumption() {
        try {
            val tankSize = tankSizeInput.text.toString().toFloat() // Liters
            val startPressure = startPressureInput.text.toString().toFloat() // Bar
            val endPressure = endPressureInput.text.toString().toFloat() // Bar
            val targetDepth = targetDepthInput.text.toString().toFloat() // Meters
            val sacRate = sacRateInput.text.toString().toFloat() // Liters/min at surface

            // Input validation for Gas Consumption Calculator
            if (tankSize <= 0 || startPressure <= 0 || endPressure < 0 || targetDepth < 0 || sacRate <= 0) {
                showGasError("Please enter positive values for all inputs, and end pressure cannot be negative.")
                return
            }
            if (endPressure >= startPressure) {
                showGasError("End pressure must be less than start pressure.")
                return
            }
            if (targetDepth > 60) { // Practical recreational limit for this calculator
                showGasError("Target depth too great for recreational diving. Max 60m.")
                return
            }

            // Calculate total usable air in liters
            val usablePressure = startPressure - endPressure
            val totalUsableAir = usablePressure * tankSize

            // Calculate ambient pressure factor at target depth
            val ambientPressureFactor = (targetDepth / 10.0) + 1.0 // Add 1 for surface pressure

            // Calculate air consumption rate at target depth (liters/min)
            val airConsumptionRateAtDepth = sacRate * ambientPressureFactor

            // Calculate estimated dive time in minutes
            val estimatedDiveTime = totalUsableAir / airConsumptionRateAtDepth

            // Build result message
            val builder = StringBuilder()
            builder.append("Tank Size: ${String.format("%.1f", tankSize)} L\n")
            builder.append("Start Pressure: ${String.format("%.1f", startPressure)} bar\n")
            builder.append("End Pressure: ${String.format("%.1f", endPressure)} bar\n")
            builder.append("Usable Pressure: ${String.format("%.1f", usablePressure)} bar\n")
            builder.append("Total Usable Air: ${String.format("%.1f", totalUsableAir)} L\n")
            builder.append("Target Depth: ${String.format("%.1f", targetDepth)} m\n")
            builder.append("Ambient Pressure Factor: ${String.format("%.1f", ambientPressureFactor)}\n")
            builder.append("SAC Rate (Surface): ${String.format("%.1f", sacRate)} L/min\n")
            builder.append("Consumption at Depth: ${String.format("%.1f", airConsumptionRateAtDepth)} L/min\n")
            builder.append("\nEstimated Dive Time: ${String.format("%.1f", estimatedDiveTime)} minutes")

            gasResultText.setTextColor(Color.GREEN) // Indicate success
            gasResultText.text = builder.toString()

        } catch (e: NumberFormatException) {
            showGasError("Please enter valid numeric values for all gas calculator inputs.")
        }
    }

    /**
     * Displays an error message for the Gas Consumption Calculator.
     * @param message The error message to display.
     */
    private fun showGasError(message: String) {
        gasResultText.setTextColor(Color.RED)
        gasResultText.text = message
    }
}
