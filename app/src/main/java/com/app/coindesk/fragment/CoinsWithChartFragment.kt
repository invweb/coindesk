package com.app.coindesk.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.app.coindesk.entity.Coins
import com.app.coindesk.mvvm.InitViewModel
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.anychart.AnyChart
import com.anychart.chart.common.dataentry.DataEntry
import com.app.coindesk.databinding.CoindeskLayoutBinding
import kotlinx.android.synthetic.main.coindesk_layout.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CoinsWithChartFragment : Fragment() {

    private lateinit var viewModelInit: InitViewModel
    private lateinit var  coinDeskLayoutBinding: CoindeskLayoutBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        coinDeskLayoutBinding = CoindeskLayoutBinding.inflate(layoutInflater)
        return coinDeskLayoutBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModelInit = ViewModelProvider(this).get(InitViewModel::class.java)

        lifecycleScope.launch(Dispatchers.IO) {
            val coinsArray: Array<Coins> = viewModelInit.getCoins()

            if(viewModelInit.getCoins().isNotEmpty()) {
                val coins = coinsArray[0]

                lifecycleScope.launch(Dispatchers.Main) {
                    coinDeskLayoutBinding.disclaimer.text = coins.disclaimer
                    coinDeskLayoutBinding.time.text = coins.time.updated
                    coinDeskLayoutBinding.chartName.text = coins.chartName

                    coinDeskLayoutBinding.descriptionEuro.text = coins.bpi.eur.description
                    coinDeskLayoutBinding.codeEuro.text = coins.bpi.eur.code
                    coinDeskLayoutBinding.rateEuro.text = coins.bpi.eur.rate
                    coinDeskLayoutBinding.symbolEuro.text = replaceToSign(coins.bpi.eur.symbol)

                    coinDeskLayoutBinding.descriptionDollar.text = coins.bpi.usd.description
                    coinDeskLayoutBinding.codeDollar.text = coins.bpi.usd.code
                    coinDeskLayoutBinding.rateDollar.text = coins.bpi.usd.rate
                    coinDeskLayoutBinding.symbolDollar.text = replaceToSign(coins.bpi.usd.symbol)

                    coinDeskLayoutBinding.descriptionGbp.text = coins.bpi.gbp.description
                    coinDeskLayoutBinding.codeGbp.text = coins.bpi.gbp.code
                    coinDeskLayoutBinding.rateGbp.text = coins.bpi.gbp.rate
                    coinDeskLayoutBinding.symbolGbp.text = replaceToSign(coins.bpi.gbp.symbol)
                }
            }

            lifecycleScope.launch(Dispatchers.Main) {
                val bar = AnyChart.bar()
                val data = ArrayList<ValueDataEntry>()

                for (coins in coinsArray){
                    data.add(ValueDataEntry(coins.chartName  + "/" + coins.bpi.usd.code, coins.bpi.usd.rateFloat))
                    data.add(ValueDataEntry(coins.chartName  + "/" + coins.bpi.eur.code, coins.bpi.eur.rateFloat))
                    data.add(ValueDataEntry(coins.chartName  + "/" + coins.bpi.gbp.code, coins.bpi.gbp.rateFloat))
                }

                bar.data(data as List<DataEntry>?)
                any_chart_view.setChart(bar)
            }
        }
    }

    private fun replaceToSign(symbol: String): CharSequence? {
        return HtmlCompat.fromHtml(symbol, HtmlCompat.FROM_HTML_MODE_LEGACY)
    }
}
