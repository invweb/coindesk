package com.app.coindesk.fragment

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.app.coindesk.R
import com.app.coindesk.mvvm.InitViewModel
import androidx.core.text.HtmlCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.app.coindesk.databinding.MainFragmentBinding
import com.app.coindesk.entity.Coins
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainFragment : Fragment() {
    lateinit var viewModelInit: InitViewModel
    private lateinit var  mainFragmentBinding: MainFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mainFragmentBinding = MainFragmentBinding.inflate(layoutInflater)
        return mainFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModelInit = ViewModelProvider(this).get(InitViewModel::class.java)

        mainFragmentBinding.button.setOnClickListener {
            val activity : Activity = requireActivity()
            val navController = Navigation.findNavController(activity, R.id.nav_host_fragment);
            navController.navigate(R.id.LaunchesFragment)
        }

        this.activity?.let { viewModelInit.saveCoinsToDB(it) }

        lifecycleScope.launch(Dispatchers.IO) {
            val coinsArray: Array<Coins> = viewModelInit.getCoins()

            if(viewModelInit.getCoins().isNotEmpty()) {
                val coins = coinsArray[0]

                lifecycleScope.launch(Dispatchers.Main) {
                    mainFragmentBinding.disclaimer.text = coins.disclaimer
                    mainFragmentBinding.time.text = coins.time.updated
                    mainFragmentBinding.chartName.text = coins.chartName

                    mainFragmentBinding.descriptionEuro.text = coins.bpi.eur.description
                    mainFragmentBinding.codeEuro.text = coins.bpi.eur.code
                    mainFragmentBinding.rateEuro.text = coins.bpi.eur.rate
                    mainFragmentBinding.symbolEuro.text = replaceToSign(coins.bpi.eur.symbol)

                    mainFragmentBinding.descriptionDollar.text = coins.bpi.usd.description
                    mainFragmentBinding.codeDollar.text = coins.bpi.usd.code
                    mainFragmentBinding.rateDollar.text = coins.bpi.usd.rate
                    mainFragmentBinding.symbolDollar.text = replaceToSign(coins.bpi.usd.symbol)

                    mainFragmentBinding.descriptionGbp.text = coins.bpi.gbp.description
                    mainFragmentBinding.codeGbp.text = coins.bpi.gbp.code
                    mainFragmentBinding.rateGbp.text = coins.bpi.gbp.rate
                    mainFragmentBinding.symbolGbp.text = replaceToSign(coins.bpi.gbp.symbol)
                }
            }
        }
    }

    private fun replaceToSign(symbol: String): CharSequence? {
        return HtmlCompat.fromHtml(symbol, HtmlCompat.FROM_HTML_MODE_LEGACY)
    }
}
