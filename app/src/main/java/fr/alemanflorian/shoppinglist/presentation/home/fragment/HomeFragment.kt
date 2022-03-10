package fr.alemanflorian.shoppinglist.presentation.home.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import fr.alemanflorian.shoppinglist.R
import fr.alemanflorian.shoppinglist.presentation.common.CustomFragment
import fr.alemanflorian.shoppinglist.presentation.common.extension.clickEffect
import fr.alemanflorian.shoppinglist.presentation.common.extension.mainNavController
import fr.alemanflorian.shoppinglist.presentation.listes.viewmodel.ListesViewModel
import fr.alemanflorian.shoppinglist.presentation.shopping.viewmodel.ShoppingViewModel
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : CustomFragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        homeBtnToListes.setOnClickListener {mainNavController().navigate(HomeFragmentDirections.actionHomeToListes(false))}
        homeBtnToListes.clickEffect(view)

        homeBtnToShopping.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO) {
                val shoppingViewModel : ShoppingViewModel by viewModel()
                val hasListes = shoppingViewModel.hasListes()
                withContext(Dispatchers.Main)
                {
                    if(hasListes)
                        mainNavController().navigate(HomeFragmentDirections.actionHomeToShopping())
                    else
                        mainNavController().navigate(HomeFragmentDirections.actionHomeToListes(true))
                }
            }
        }
        homeBtnToShopping.clickEffect(view)

        header.hide()
    }
}