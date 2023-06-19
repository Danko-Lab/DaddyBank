package com.example.daddybank

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import android.util.Log
import androidx.fragment.app.viewModels
import com.github.mikephil.charting.components.XAxis
import kotlin.math.roundToInt

class ProjectionsFragment : Fragment() {
    private val sharedViewModel: SharedViewModel by viewModels({ requireActivity() })

    private lateinit var projectionsLineChart: LineChart
    private lateinit var yearsSpinner: Spinner
    private lateinit var generateButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_projections, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("ProjectionsFragment", "onViewCreated called")

        projectionsLineChart = view.findViewById(R.id.projections_line_chart)
        yearsSpinner = view.findViewById(R.id.years_spinner)
        generateButton = view.findViewById(R.id.generate_button)

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.projections_time_spans,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            yearsSpinner.adapter = adapter
        }

        generateButton.setOnClickListener {
            updateChart()
        }
    }

    private fun updateChart() {
        val interestRate = sharedViewModel.currentInterestRate.value ?: 0.0 // Use interest rate from SharedViewModel
        val principal = sharedViewModel.accountValuesSeries.value?.last()?.second ?: 0.0
        val selectedItem = "\\d+".toRegex().find(yearsSpinner.selectedItem.toString())?.value?.toInt() ?: 1 //yearsSpinner.selectedItem.toString().toInt()

        Log.d("ProjectionsFragment", "Selected years: $selectedItem")

        val projectionData = generateProjectionData(interestRate, principal, selectedItem)
        setupProjectionChart(projectionData)
    }

    private fun generateProjectionData(interestRate: Double, initialPrincipal: Double, years: Int): List<Entry> {
        val data = mutableListOf<Entry>()
        val totalDays = years * 365
        var principal = initialPrincipal
        for (i in 0..totalDays) {
            principal *= 1 + interestRate / 365.0
            data.add(Entry(i.toFloat(), principal.toFloat()))
        }
        return data
    }

    private fun setupProjectionChart(data: List<Entry>) {
        val lineDataSet = LineDataSet(data, "Projected Principal Balance").apply {
            valueTextSize = 12f
            lineWidth = 3f
        }
        val lineData = LineData(lineDataSet)

        projectionsLineChart.apply {
            this.data = lineData
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                setDrawAxisLine(true)
                axisLineWidth = 2f
                // Check the range of x values, if more than 365 days (1 year), set labels to 11 (0 to 10)
                // If not, adjust to the range of the data
                val range = if(data.last().x > 365) 11 else 3
                setLabelCount(range, true)
                granularity = 1f // Set the minimum interval between the axis values
                textSize = 14f
                setLabelRotationAngle(-45f)

                // Set the value formatter to display years instead of days
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        // If the last x value is more than 365 days, round to integer. If not, keep the decimal representation.
                        return if(data.last().x > 365) (value / 365).roundToInt().toString() else (value / 365).toString()
                    }
                }
            }
            axisLeft.apply {
                setDrawGridLines(false)
                setDrawAxisLine(true)
                axisLineWidth = 2f
                textSize = 14f
            }
            axisRight.isEnabled = false
            description.text = "Years (X-axis) / Dollars (Y-axis)"
            description.textSize = 14f
            description.setPosition(1f, 1f)
            description.isEnabled = true
            legend.isEnabled = false
            // Set extra offsets to prevent labels from being cut off
            setExtraOffsets(10f, 0f, 10f, 10f)
            invalidate()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = ProjectionsFragment()
    }
}
