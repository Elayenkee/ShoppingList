package fr.alemanflorian.shoppinglist.presentation.home.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import fr.alemanflorian.shoppinglist.R
import fr.alemanflorian.shoppinglist.presentation.common.CustomFragment
import fr.alemanflorian.shoppinglist.presentation.common.extension.mainNavController
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : CustomFragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        homeBtnCreateProduct.setOnClickListener {
            mainNavController().navigate(HomeFragmentDirections.actionHomeToProductCreate())
        }

        homeBtnAllProduct.setOnClickListener {
            mainNavController().navigate(HomeFragmentDirections.actionHomeToProductAll())
        }

        header.hide()
    }
}