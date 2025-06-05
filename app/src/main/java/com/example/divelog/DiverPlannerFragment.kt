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

    private lateinit var depthInput: EditText
    private lateinit var oxygenInput: EditText
    private lateinit var resultText: TextView
    private lateinit var calculateButton: Button

    // Hardcoded no-deco limits for air per depth (meters)
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

        depthInput = view.findViewById(R.id.depthInput)
        oxygenInput = view.findViewById(R.id.oxygenInput)
        resultText = view.findViewById(R.id.resultText)
        calculateButton = view.findViewById(R.id.calculateButton)

        calculateButton.setOnClickListener { calculateDive() }

        return view
    }

    private fun calculateDive() {
        try {
            val depth = depthInput.text.toString().toInt()
            val oxygenPercent = oxygenInput.text.toString().toInt()

            // Input validation
            if (depth < 0 || depth > 51) {
                showError("Depth must be between 0 and 51 meters.")
                return
            }
            if (oxygenPercent < 21 || oxygenPercent > 40) {
                showError("Oxygen % must be between 21 and 40.")
                return
            }

            // Calculate PO2 at depth
            val ambientPressure = depth / 10.0 + 1.0 // bar
            val oxygenFraction = oxygenPercent / 100.0
            val po2 = ambientPressure * oxygenFraction

            // Check PO2 limit
            if (po2 > maxPO2) {
                showWarning("Warning: PO2 (${String.format("%.2f", po2)} bar) exceeds max safe limit of $maxPO2 bar.\nReduce depth or oxygen %.")
                return
            }

            // Calculate Maximum Operating Depth (MOD) for given oxygen mix
            val mod = ((maxPO2 / oxygenFraction) - 1.0) * 10.0

            // Calculate Equivalent Air Depth (EAD)
            val ead = calculateEAD(depth, oxygenFraction)

            // Get NDL based on EAD (rounded up conservatively)
            val ndl = getNDLForAir(kotlin.math.ceil(ead).toInt())

            if (ndl == -1) {
                showError("Depth exceeds recreational dive limits.")
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

            // Set text color based on safety
            when {
                po2 > maxPO2 -> resultText.setTextColor(Color.RED)
                depth > mod -> resultText.setTextColor(Color.RED)
                ndl < 10 -> resultText.setTextColor(Color.parseColor("#FFA500")) // Orange for caution
                else -> resultText.setTextColor(Color.GREEN)
            }

            resultText.text = builder.toString()

        } catch (e: NumberFormatException) {
            showError("Please enter valid numeric values for depth and oxygen %.")
        }
    }

    private fun calculateEAD(depth: Int, oxygenFraction: Double): Double {
        val nitrogenFraction = 1.0 - oxygenFraction
        val ambientPressure = depth / 10.0 + 1.0 // in bar
        val eadBar = ((nitrogenFraction * (ambientPressure - 1.0)) / 0.79) + 1.0
        return (eadBar - 1.0) * 10.0
    }

    private fun getNDLForAir(depth: Int): Int {
        val depths = airNDLTable.keys.sorted()

        for (d in depths) {
            if (depth <= d) {
                return airNDLTable[d] ?: -1
            }
        }
        return -1 // Exceeds limits
    }

    private fun showError(message: String) {
        resultText.setTextColor(Color.RED)
        resultText.text = message
    }

    private fun showWarning(message: String) {
        resultText.setTextColor(Color.parseColor("#FFA500")) // Orange color
        resultText.text = message
    }
}
