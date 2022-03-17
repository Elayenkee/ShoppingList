package fr.alemanflorian.shoppinglist.presentation.common

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import fr.alemanflorian.shoppinglist.Header
import fr.alemanflorian.shoppinglist.MainActivity
import fr.alemanflorian.shoppinglist.R
import fr.alemanflorian.shoppinglist.domain.entity.ProductFromListe
import fr.alemanflorian.shoppinglist.presentation.listes.viewmodel.ListesViewModel

open class CustomFragment  : Fragment()
{
    lateinit var header: Header

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
        header = (requireActivity() as MainActivity).header
        header.reset()
        (requireActivity() as MainActivity).overridedBackButton = null
    }

    fun overrideBackButton(overridedBackButton:()->Boolean){
        (requireActivity() as MainActivity).overridedBackButton = overridedBackButton
    }

    fun openDetailsProduit(listesViewModel: ListesViewModel, product: ProductFromListe){
        DetailsProduct(listesViewModel, viewLifecycleOwner, product).show(requireContext())
    }

    /**
     * Popup pour changer le nombre du produit
     */
    class DetailsProduct(val listesViewModel: ListesViewModel, val viewLifecycleOwner: LifecycleOwner, val product: ProductFromListe){
        private lateinit var context: Context
        private lateinit var dialog: Dialog

        fun show(context: Context) {
            this.context = context
            dialog = Dialog(context)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.popup_details_item)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()

            update()
            dialog.findViewById<View>(R.id.popupDetailsItemMinus).setOnClickListener {
                listesViewModel.decrementeProductToCurrentListe(product)
            }
            dialog.findViewById<View>(R.id.popupDetailsItemPlus).setOnClickListener {
                listesViewModel.incrementeProductToCurrentListe(product)
            }

            dialog.setOnDismissListener {
                listesViewModel.getCurrentListe()
            }

            listesViewModel.incrementeProductResult.observe(viewLifecycleOwner){
                update()
            }
            listesViewModel.decrementeProductResult.observe(viewLifecycleOwner){
                update()
            }
        }

        private fun update(){
            dialog.findViewById<TextView>(R.id.popupDetailsProductTxtName).text = product.product.name
            dialog.findViewById<TextView>(R.id.popupDetailsProductTxtNb).text = product.nb.toString()
            val minus = dialog.findViewById<View>(R.id.popupDetailsItemMinus)
            if(product.nb <= 0)
                minus.alpha = .5f
            else
                minus.alpha = 1f
            minus.isEnabled = product.nb > 0
        }
    }
}