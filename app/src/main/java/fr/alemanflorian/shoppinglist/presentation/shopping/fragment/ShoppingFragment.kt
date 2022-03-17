package fr.alemanflorian.shoppinglist.presentation.shopping.fragment

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.animation.Animation
import android.view.animation.Transformation
import fr.alemanflorian.shoppinglist.R
import fr.alemanflorian.shoppinglist.domain.entity.Liste
import fr.alemanflorian.shoppinglist.domain.entity.ProductFromListe
import fr.alemanflorian.shoppinglist.domain.resource.Resource
import fr.alemanflorian.shoppinglist.presentation.common.CustomFragment
import fr.alemanflorian.shoppinglist.presentation.common.extension.mainNavController
import fr.alemanflorian.shoppinglist.presentation.home.fragment.HomeFragmentDirections
import fr.alemanflorian.shoppinglist.presentation.shopping.adapter.ProductsAdapter
import fr.alemanflorian.shoppinglist.presentation.shopping.adapter.ListesAdapter
import fr.alemanflorian.shoppinglist.presentation.shopping.viewmodel.ShoppingViewModel
import kotlinx.android.synthetic.main.fragment_shopping.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.viewModel


class ShoppingFragment  : CustomFragment(){
    private val shoppingViewModel : ShoppingViewModel by viewModel()

    private val adapterListes = ListesAdapter(object : ListesAdapter.Interactor{
        override fun onClicked(liste: Liste) {
            shoppingViewModel.setListeEnCours(liste)
            shoppingViewModel.initListeEnCours()
        }
    })

    private val adapterListeProducts = ProductsAdapter(object : ProductsAdapter.Interactor{
        override fun onClicked(productFromListe: ProductFromListe) {
            shoppingViewModel.clickProduct(productFromListe)
        }
    })

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?{
        return inflater.inflate(R.layout.fragment_shopping, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)
        initViewObserver()
        initView()
        refresh()
    }

    private fun initViewObserver(){
        shoppingViewModel.initListeEnCoursResult.observe(viewLifecycleOwner){
            if(it is Resource.Success)
            {
                if(it.data.listeEnCours != null)
                {
                    collapse(pan1)
                    header.show()
                    header.hideBackButton()
                    header.setTitle(it.data.listeEnCours.liste.name)
                    adapterListeProducts.setData(it.data.listeEnCours.products)
                    if(it.data.listeEnCours.liste.isFinished())
                        onListeFinished()
                }
                else
                {
                    pan1.visibility = View.VISIBLE
                    adapterListes.setData(it.data.listes)
                }
            }
        }
        shoppingViewModel.clickProductResult.observe(viewLifecycleOwner){
            if(it is Resource.Success) {
                adapterListeProducts.notifyItemChanged(it.data.product)
                if(it.data.listeFinished)
                    onListeFinished()
            }
        }
    }

    private fun initView(){
        header.hide()
        pan1.visibility = View.INVISIBLE
        FragmentShoppingListeRecyclerView.adapter = adapterListeProducts
        FragmentShoppingListesRecyclerView.adapter = adapterListes

        overrideBackButton {
            System.err.println("TEST")
            pan1.visibility != View.VISIBLE
        }
    }

    private fun refresh(){
        shoppingViewModel.initListeEnCours()
    }

    private fun onListeFinished(){
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.popup_shopping_finished)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        dialog.findViewById<View>(R.id.PopupShoppingFinishedBtnContinuer).setOnClickListener {
            dialog.dismiss()
        }
        dialog.findViewById<View>(R.id.PopupShoppingFinishedBtnQuitter).setOnClickListener {
            GlobalScope.launch {
                shoppingViewModel.finishListeEnCours()
                withContext(Dispatchers.Main)
                {
                    dialog.dismiss()
                    mainNavController().navigate(ShoppingFragmentDirections.actionShoppingToHome())
                }
            }
        }
    }

    private fun collapse(v: View) {
        if(v.visibility == View.INVISIBLE)
        {
            v.visibility = View.GONE
            return
        }

        v.alpha = .5f
        val initialHeight = v.measuredHeight
        val a: Animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                if (interpolatedTime == 1f) {
                    v.visibility = View.GONE
                } else {
                    v.layoutParams.height =
                        initialHeight - (initialHeight * interpolatedTime).toInt()
                    v.requestLayout()
                }
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }

        // Collapse speed of 1dp/ms
        a.setDuration(500)
        v.startAnimation(a)
    }
}