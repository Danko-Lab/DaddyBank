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
import android.util.Log
import androidx.fragment.app.viewModels
import com.github.mikephil.charting.components.XAxis

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

    private fun generateProjectionData(interestRate: Double, principal: Double, years: Int): List<Entry> {
        val data = mutableListOf<Entry>()
        for (i in 0..years) {
            val futureValue = principal * Math.pow(1 + interestRate, i.toDouble())
            data.add(Entry(i.toFloat(), futureValue.toFloat()))
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
                setLabelCount(data.size, true)
                textSize = 14f
                setLabelRotationAngle(-45f)
            }
            axisLeft.apply {
                setDrawGridLines(false)
                setDrawAxisLine(true)
                axisLineWidth = 2f
                textSize = 14f
            }
            axisRight.isEnabled = false
            description.isEnabled = false
            legend.isEnabled = false
            invalidate()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = ProjectionsFragment()
    }
}
