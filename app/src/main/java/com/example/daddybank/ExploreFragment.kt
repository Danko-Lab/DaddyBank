package com.example.daddybank

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.components.XAxis

class ExploreFragment : Fragment() {

    private lateinit var exploreLineChart: LineChart
    private lateinit var interestRateInput: EditText
    private lateinit var startingPrincipalInput: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_explore, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        exploreLineChart = view.findViewById(R.id.explore_line_chart)
        interestRateInput = view.findViewById(R.id.interest_rate_input)
        startingPrincipalInput = view.findViewById(R.id.starting_principal_input)

        interestRateInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                updateChart()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        startingPrincipalInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                updateChart()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Example data, replace with real data from the XML file
        val currentInterestRate = 0.05
        val currentPrincipal = 1000.0
        val projectionYears = 10

        interestRateInput.setText(currentInterestRate.toString())
        startingPrincipalInput.setText(currentPrincipal.toString())

        updateChart()
    }

    private fun updateChart() {
        val interestRate = interestRateInput.text.toString().toDoubleOrNull() ?: return
        val principal = startingPrincipalInput.text.toString().toDoubleOrNull() ?: return
        val years = 10

        val projectionData = generateProjectionData(interestRate, principal, years)
        setupProjectionChart(projectionData)
    }

    private fun generateProjectionData(interestRate: Double, principal: Double, years: Int): List<Entry> {
        val data = mutableListOf<Entry>()
        for (i in 0..years) {
            val futureValue = principal * Math.pow(1 + interestRate, i.toDouble())
            data.add(Entry(i.toFloat(), futureValue.toFloat()))
        }
        return data
    }

    private fun setupProjectionChart(data: List<Entry>) {
        val lineDataSet = LineDataSet(data, "Projected Principal Balance")
        val lineData = LineData(lineDataSet)

        exploreLineChart.apply {
            this.data = lineData
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM // Set the X axis position to bottom
                setDrawLabels(true)
                textSize = 12f // Increase the text size for the X axis labels
                setDrawGridLines(false)
                setDrawAxisLine(true) // Draw the X axis line
            }
            axisLeft.apply {
                setDrawLabels(true)
                textSize = 12f // Increase the text size for the Y axis labels
                setDrawGridLines(false)
                setDrawAxisLine(true) // Draw the Y axis line
            }
            axisRight.isEnabled = false
            description.isEnabled = false
            legend.isEnabled = false
            setDrawBorders(true) // Draw borders around the chart
            invalidate()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = ExploreFragment()
    }
}
