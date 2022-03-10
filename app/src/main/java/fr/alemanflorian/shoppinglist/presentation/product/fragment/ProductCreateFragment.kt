package fr.alemanflorian.shoppinglist.presentation.product.fragment

import android.database.sqlite.SQLiteConstraintException
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.Fragment
import fr.alemanflorian.shoppinglist.R
import fr.alemanflorian.shoppinglist.domain.entity.Product
import fr.alemanflorian.shoppinglist.domain.resource.Resource
import fr.alemanflorian.shoppinglist.presentation.common.CustomFragment
import fr.alemanflorian.shoppinglist.presentation.common.extension.mainNavController
import fr.alemanflorian.shoppinglist.presentation.product.viewmodel.ProductViewModel
import kotlinx.android.synthetic.main.fragment_product_create.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProductCreateFragment : CustomFragment()
{
    private val viewModel : ProductViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        return inflater.inflate(R.layout.fragment_product_create, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
        initViewObserver()
        productCreateTxtName.setOnEditorActionListener { _, actionId, _ ->
            if(actionId == EditorInfo.IME_ACTION_DONE)
            {
                saveProduct()
                true
            }
            else
            {
                false
            }
        }
        productCreateBtnSave.setOnClickListener {
            saveProduct()
        }
    }

    private fun saveProduct()
    {
        val pName = productCreateTxtName.text.toString()
        if(pName.length > 0)
            viewModel.saveProduct(Product(0, pName))
    }

    private fun initViewObserver()
    {
        viewModel.saveProductResult.observe(viewLifecycleOwner, {
            if (it is Resource.Success)
            {
                System.err.println("SUCCESS")
                mainNavController().navigate(ProductCreateFragmentDirections.actionProductCreateToHome())
            }
            else  if (it is Resource.Progress)
            {
                System.err.println("PROGRESS")
            }
            else  if (it is Resource.Failure)
            {
                System.err.println("FAILURE " + it)
                if(it.throwable is SQLiteConstraintException)
                {
                    Toast.makeText(requireContext(), "Ce produit existe déjà.", Toast.LENGTH_LONG).show()
                }
            }
        })
    }
}