package com.chris.divelog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment

class DiverPlannerFragment : Fragment() {

    private lateinit var depthInput: EditText
    private lateinit var oxygenInput: EditText
    private lateinit var resultText: TextView
    private lateinit var calculateButton: Button

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

            val ndl = getNDL(depth)
            resultText.text = if (ndl == -1) {
                "Depth exceeds recreational dive limits."
            } else {
                "Max no-deco time: $ndl minutes. Safe to dive."
            }
        } catch (e: NumberFormatException) {
            resultText.text = "Please enter valid numbers for depth and oxygen percentage."
        }
    }

    private fun getNDL(depth: Int): Int {
        return when {
            depth <= 10 -> 219
            depth <= 12 -> 147
            depth <= 15 -> 100
            depth <= 18 -> 60
            depth <= 21 -> 45
            depth <= 25 -> 30
            depth <= 30 -> 20
            depth <= 35 -> 15
            depth <= 40 -> 10
            else -> -1 // Exceeds recreational dive limits
        }
    }
}
