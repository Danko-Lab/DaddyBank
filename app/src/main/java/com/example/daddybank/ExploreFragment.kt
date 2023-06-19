package com.example.daddybank

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlin.math.roundToInt

class ExploreFragment : Fragment() {

    private val sharedViewModel: SharedViewModel by viewModels({ requireActivity() })

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
        val currentInterestRate = sharedViewModel.currentInterestRate.value ?: 0.0 // Use interest rate from SharedViewModel
        val currentPrincipal = sharedViewModel.accountValuesSeries.value?.last()?.second ?: 0.0
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

        exploreLineChart.apply {
            this.data = lineData
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                setDrawAxisLine(true)
                axisLineWidth = 2f
                val range = if(data.last().x > 365) 11 else 3
                setLabelCount(range, true)
                granularity = 1f
                textSize = 14f
                setLabelRotationAngle(-45f)

                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
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
            setExtraOffsets(10f, 0f, 10f, 10f)
            invalidate()
        }
    }


    companion object {
        @JvmStatic
        fun newInstance() = ExploreFragment()
    }
}
