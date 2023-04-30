package com.example.daddybank

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.components.XAxis

class HistoryFragment : Fragment() {

    private lateinit var historyLineChart: LineChart

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        historyLineChart = view.findViewById(R.id.history_line_chart)

        // Example data, replace with real data from the XML file
        val historyData = listOf(
            Entry(0f, 1000f),
            Entry(1f, 1100f),
            Entry(2f, 1300f),
            Entry(3f, 1500f)
        )

        // Example date labels corresponding to the data points in historyData
        val dateLabels = listOf("2023-01-01", "2023-02-01", "2023-03-01", "2023-04-01")

        setupHistoryChart(historyData, dateLabels)
    }

    private fun setupHistoryChart(data: List<Entry>, dateLabels: List<String>) {
        val lineDataSet = LineDataSet(data, "Historical Principal Balance").apply {
            valueTextSize = 12f
            lineWidth = 3f
        }
        val lineData = LineData(lineDataSet)

        historyLineChart.apply {
            this.data = lineData
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                setDrawAxisLine(true)
                axisLineWidth = 2f
                valueFormatter = IndexAxisValueFormatter(dateLabels)
                labelCount = dateLabels.size
                textSize = 14f
                setLabelRotationAngle(-45f)
            }
            axisLeft.apply {
                setDrawGridLines(false)
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
        fun newInstance() = HistoryFragment()
    }
}
