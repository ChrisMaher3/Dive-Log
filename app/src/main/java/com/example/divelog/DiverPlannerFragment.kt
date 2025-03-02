package com.chris.divelog

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

    private val maxPO2 = 1.2  // Maximum partial pressure of oxygen for this case (1.2 bar)

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
            val oxygen = oxygenInput.text.toString().toInt()

            if (oxygen < 21 || oxygen > 40) {
                resultText.text = "Unsafe oxygen percentage. Use between 21% and 40%."
                return
            }

            // Check if the oxygen mix exceeds the PO2 limit (1.2 bar)
            val po2 = (depth / 10.0) * (oxygen / 100.0)
            if (po2 > maxPO2) {
                resultText.text = "Warning: Depth exceeds the safe PO2 limit of 1.2 bar for the given oxygen mix."
                return
            }

            val increasedNdl = calculateIncreasedNDL(depth, oxygen)

            resultText.text = if (increasedNdl == -1) {
                "Depth exceeds recreational dive limits."
            } else {
                "Max no-deco time: $increasedNdl minutes. Safe to dive."
            }
        } catch (e: NumberFormatException) {
            resultText.text = "Please enter valid numbers for depth and oxygen percentage."
        }
    }

    private fun calculateMOD(oxygen: Int): Int {
        // Calculate MOD based on the formula for PO2 = 1.2 bar
        return ((maxPO2 / (oxygen / 100.0)) * 10).roundToInt()
    }

    private fun calculateIncreasedNDL(depth: Int, oxygen: Int): Int {
        val baseNdl = getNDL(depth)
        if (baseNdl == -1) return -1 // Depth exceeds limits

        val ead = ((depth + 10) * (1 - oxygen / 100.0) / 0.79) - 10
        val adjustedNdl = getNDL(ead.roundToInt())

        return adjustedNdl
    }

    private fun getNDL(depth: Int): Int {
        return when {
            depth <= 12 -> 125
            depth <= 15 -> 75
            depth <= 18 -> 47
            depth <= 21 -> 34
            depth <= 24 -> 25
            depth <= 27 -> 20
            depth <= 33 -> 14
            depth <= 36 -> 12
            depth <= 39 -> 10
            depth <= 42 -> 9
            depth <= 45 -> 9
            depth <= 48 -> 6
            depth <= 51 -> 5
            else -> -1 // Exceeds recreational dive limits
        }
    }
}
