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
import androidx.fragment.app.viewModels

class HistoryFragment : Fragment() {

    private val sharedViewModel: SharedViewModel by viewModels({ requireActivity() })

    private lateinit var historyLineChart: LineChart
    private lateinit var bankDataRepository: BankDataRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bankDataRepository = arguments?.getSerializable(BANK_DATA_REPOSITORY) as BankDataRepository

        historyLineChart = view.findViewById(R.id.history_line_chart)

        sharedViewModel.selectedUser.observe(viewLifecycleOwner, { user ->
            if (user != null) {
                val accountValuesSeries = bankDataRepository.getAccountValuesSeries(user)

                // Prepare data and labels for the chart
                val historyData = accountValuesSeries.mapIndexed { index, dataPoint ->
                    Entry(index.toFloat(), dataPoint.second.toFloat())
                }
                val dateLabels = accountValuesSeries.map { it.first }

                setupHistoryChart(historyData, dateLabels)
            } else {
                // Handle the case when the user is not found or selected
            }
            })
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
        private const val BANK_DATA_REPOSITORY = "bank_data_repository"

        fun newInstance(bankDataRepository: BankDataRepository): HistoryFragment {
            val fragment = HistoryFragment()
            val args = Bundle()
            args.putSerializable(BANK_DATA_REPOSITORY, bankDataRepository)
            fragment.arguments = args
            return fragment
        }
    }
}
