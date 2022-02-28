package fr.alemanflorian.shoppinglist.presentation.product.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import fr.alemanflorian.shoppinglist.R
import fr.alemanflorian.shoppinglist.presentation.common.CustomFragment
import kotlinx.android.synthetic.main.fragment_product.*

class ProductFragment : CustomFragment() {
    private val arguments: ProductFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_product, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val product = arguments.product
    }
}