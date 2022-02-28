package fr.alemanflorian.shoppinglist.presentation.product.fragment

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import fr.alemanflorian.shoppinglist.R
import fr.alemanflorian.shoppinglist.domain.entity.Liste
import fr.alemanflorian.shoppinglist.domain.entity.Product
import fr.alemanflorian.shoppinglist.domain.entity.ProductFromListe
import fr.alemanflorian.shoppinglist.domain.resource.Resource
import fr.alemanflorian.shoppinglist.presentation.common.CustomFragment
import fr.alemanflorian.shoppinglist.presentation.common.extension.hideKeyboard
import fr.alemanflorian.shoppinglist.presentation.common.extension.mainNavController
import fr.alemanflorian.shoppinglist.presentation.common.extension.questionYesNo
import fr.alemanflorian.shoppinglist.presentation.liste.ListeViewModel
import fr.alemanflorian.shoppinglist.presentation.product.adapter.ChangeListeAdapter
import fr.alemanflorian.shoppinglist.presentation.product.adapter.ProductAllAdapter
import fr.alemanflorian.shoppinglist.presentation.product.adapter.ProductFilteredAdapter
import fr.alemanflorian.shoppinglist.presentation.product.adapter.ProductListeAdapter
import fr.alemanflorian.shoppinglist.presentation.product.viewmodel.ProductViewModel
import kotlinx.android.synthetic.main.fragment_product_all.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProductAllFragment : CustomFragment()
{
    private val productViewModel : ProductViewModel by viewModel()
    private val listeViewModel : ListeViewModel by viewModel()

    private var allProducts = mutableListOf<ProductFromListe>()

    private var popupChangeListe:ChangeListe? = null

    private val adapterAll = ProductAllAdapter(object : ProductAllAdapter.Interactor {
        override fun onProductClicked(product: ProductFromListe) {
            mainNavController().navigate(
                ProductAllFragmentDirections.actionProductAllToProduct(
                    product.product
                )
            )
        }

        override fun onProductAddToCurrentList(product: ProductFromListe) {
            listeViewModel.addProductToCurrentListe(product)
        }
    })

    private val adapterFiltered = ProductFilteredAdapter(object :ProductFilteredAdapter.Interactor {
        override fun onProductClicked(product: ProductFromListe) {
            listeViewModel.addProductToCurrentListe(product)
            containerFiltre.visibility = View.GONE
            txtSearchProduct.hideKeyboard()
            txtSearchProduct.setText("")
        }
    })

    private val adapterListe = ProductListeAdapter(object : ProductListeAdapter.Interactor{
        override fun onItemDismiss(product: ProductFromListe) {
            listeViewModel.deleteProductFromCurrentListe(product.product)
        }

        override fun onItemClick(product: ProductFromListe) {
            DetailsProduct(listeViewModel, viewLifecycleOwner, product).show(requireContext())
        }
    })

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View?
    {
        return inflater.inflate(R.layout.fragment_product_all, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
        initViewObserver()
        initView()
        refresh()
    }

    private fun initView()
    {
        productAllRecyclerView.adapter = adapterAll
        productFilteredRecyclerView.adapter = adapterFiltered
        productListeRecyclerView.adapter = adapterListe
        val callback = ProductListeAdapter.SwipeHelperCallback(adapterListe)
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(productListeRecyclerView)

        txtSearchProduct.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                onTextChanged(s)
            }
        })
        txtSearchProduct.setOnClickListener { onTextChanged(txtSearchProduct.text) }
        txtSearchProduct.setOnFocusChangeListener { _v, hasFocus ->  if(hasFocus) onTextChanged(
            txtSearchProduct.text
        ) else containerFiltre.performClick()}
        txtSearchProduct.setOnEditorActionListener { _v, actionId, _event ->
            if(actionId == EditorInfo.IME_ACTION_DONE)
            {
                val name = txtSearchProduct.text.toString()
                val product = productExists(name)
                if(product != null)
                {
                    val p = ProductFromListe.create(product)
                    listeViewModel.addProductToCurrentListe(p)
                    containerFiltre.visibility = View.GONE
                    txtSearchProduct.hideKeyboard()
                    txtSearchProduct.setText("")
                }
                else
                {
                    questionYesNo(
                        requireContext(),
                        "Voulez-vous ajouter '$name' dans l'application ?",
                        {
                            productViewModel.saveNewProduct(Product(0, name))
                        },
                        {})
                }
                true
            }
            false
        }

        containerFiltre.setOnClickListener{
            containerFiltre.visibility = View.GONE
            txtSearchProduct.hideKeyboard()
        }

        drawer.setScrimColor(getResources().getColor(android.R.color.transparent))
        drawer.setDrawerShadow(android.R.color.transparent, GravityCompat.START);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)drawer.elevation = 15f
        btnAllProducts.setOnClickListener{
            hideKeyboard();
            panelChangeListe.visibility = View.GONE
            panelAllProducts.visibility = View.VISIBLE
            drawer.openDrawer(Gravity.RIGHT)
        }

        header.addView(R.layout.button_change_liste).setOnClickListener {
            hideKeyboard()
            popupChangeListe = ChangeListe(listeViewModel, viewLifecycleOwner)
            popupChangeListe!!.show(requireContext())
        }
    }

    private fun productExists(s: String):Product?
    {
        for(str in allProducts)
        {
            if(str.product.uniqueName.contains(s))
                return str.product
        }
        return null
    }

    private fun onTextChanged(s: CharSequence?)
    {
        val found = mutableListOf<ProductFromListe>()
        if(s != null && s.length > 0)
            for(str in allProducts)
            {
                if(str.product.uniqueName.contains(s))
                    found.add(str)
            }
        if(found.size > 0)
        {
            containerFiltre.visibility = View.VISIBLE
            adapterFiltered.setData(found)
        }
        else
        {
            containerFiltre.visibility = View.GONE
        }
    }

    private fun refresh() {
        System.err.println("refresh")
        productViewModel.getAllProductsWithCurrentNb()
        listeViewModel.getProductsOfCurrentListe()
        listeViewModel.getCurrentListe()
    }

    private fun initViewObserver() {
        productViewModel.saveNewProductResult.observe(viewLifecycleOwner){
            System.err.println("on saveNewProductResult")
            if(it is Resource.Success)
            {
                containerFiltre.visibility = View.GONE
                txtSearchProduct.hideKeyboard()
                txtSearchProduct.setText("")
                refresh()
            }
        }

        productViewModel.getAllProductsWithCurrentNbResult.observe(viewLifecycleOwner) {
            System.err.println("on getAllProductsWithCurrentNbResult")
            if (it is Resource.Success)
            {
                adapterAll.setData(it.data)
                allProducts = it.data.toMutableList()
            }
        }

        listeViewModel.addProductResult.observe(viewLifecycleOwner){
            System.err.println("on addProductResult")
            if(it is Resource.Success)
                refresh()
        }

        listeViewModel.decrementeProductResult.observe(viewLifecycleOwner){
            System.err.println("on decrementeProductResult")
            if(it is Resource.Success)
                refresh()
        }

        listeViewModel.getProductsOfCurrentListeResult.observe(viewLifecycleOwner){
            System.err.println("on getProductsOfCurrentListeResult")
            if(it is Resource.Success)
            {
                adapterListe.setData(it.data)
                layoutEmpty.visibility = if(it.data.size > 0) View.GONE else View.VISIBLE
                productListeRecyclerView.visibility = if(it.data.size > 0) View.VISIBLE else View.GONE
            }
        }

        listeViewModel.getCurrentListeResult.observe(viewLifecycleOwner){
            System.err.println("on getCurrentListeResult")
            if(it is Resource.Success)
                header.setTitle(it.data.name)
        }

        listeViewModel.deleteListeResult.observe(viewLifecycleOwner){
            System.err.println("on deleteListeResult")
            if(it is Resource.Success)
            {
                listeViewModel.getCurrentListe()
                listeViewModel.getProductsOfCurrentListe()
            }
        }

        listeViewModel.saveCurrentListeResult.observe(viewLifecycleOwner){
            System.err.println("on saveCurrentListeResult")
            if(it is Resource.Success)
            {
                listeViewModel.getCurrentListe()
                listeViewModel.getProductsOfCurrentListe()
            }
        }

        listeViewModel.deleteProductFromCurrentListeResult.observe(viewLifecycleOwner){
            System.err.println("on deleteProductFromCurrentListeResult")
        }

        listeViewModel.saveNewListeResult.observe(viewLifecycleOwner){
            System.err.println("on saveNewListeResult")
            if(it is Resource.Success)
            {
                listeViewModel.getCurrentListe()
                listeViewModel.getProductsOfCurrentListe()
                popupChangeListe?.dismiss()
                popupChangeListe = null
            }
        }
    }
    
    class ChangeListe(val listeViewModel : ListeViewModel, val viewLifecycleOwner: LifecycleOwner){
        private lateinit var context: Context
        private lateinit var dialog: Dialog
        private val adapterListe = ChangeListeAdapter(object : ChangeListeAdapter.Interactor{
            override fun onClicked(liste: Liste)
            {
                listeViewModel.saveCurrentListe(liste)
                dialog.dismiss()
            }

            override fun delete(liste: Liste)
            {
                System.err.println("Delete " + liste.name)
                val builder = AlertDialog.Builder(context)
                builder.setMessage("Supprimer cette liste ?")
                builder.setPositiveButton(android.R.string.ok) { _dialog, _which ->
                    listeViewModel.deleteListe(liste)
                }
                builder.setNegativeButton(android.R.string.cancel) { _dialog, _which -> }
                builder.show()
            }
        })

        fun show(context: Context)
        {
            this.context = context
            dialog = Dialog(context)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.popup_change_liste)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()

            dialog.findViewById<RecyclerView>(R.id.popupChangeListeRecyclerView).adapter = adapterListe
            listeViewModel.getAllListeResult.observe(viewLifecycleOwner){
                if (it is Resource.Success)
                {
                    adapterListe.setData(it.data)
                }
            }
            listeViewModel.getAllListe()

            listeViewModel.deleteListeResult.observe(viewLifecycleOwner){
                listeViewModel.getAllListe()
            }

            val txtListe = dialog.findViewById<TextView>(R.id.popupChangeListeTxtListe)
            val createNewListe = {
                if(txtListe.text.toString().length > 0)
                {
                    val newListe = Liste(0, txtListe.text.toString(), LinkedHashMap())
                    listeViewModel.saveNewListe(newListe)
                    txtListe.text = ""
                }
            }
            txtListe.setOnEditorActionListener { _v, actionId, event ->
                if(actionId == EditorInfo.IME_ACTION_DONE)
                {
                    createNewListe()
                    true
                }
                else
                {
                    false
                }
            }
            dialog.findViewById<View>(R.id.popupChangeListeBtnCreate).setOnClickListener {
                createNewListe()
            }
        }

        fun dismiss(){
            dialog.dismiss()
        }

        open class PopupChangeListeRecyclerViewLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null,defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr)
        {
            override fun onMeasure(widthSpec: Int, heightSpec: Int)
            {
                var heightSpec = heightSpec
                heightSpec = MeasureSpec.makeMeasureSpec(500, MeasureSpec.AT_MOST)
                super.onMeasure(widthSpec, heightSpec)
            }
        }
    }

    class DetailsProduct(val listeViewModel: ListeViewModel, val viewLifecycleOwner: LifecycleOwner, val product:ProductFromListe){
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
                listeViewModel.decrementeProductToCurrentListe(product)
            }
            dialog.findViewById<View>(R.id.popupDetailsItemPlus).setOnClickListener {
                listeViewModel.addProductToCurrentListe(product)
            }

            dialog.setOnDismissListener {
                listeViewModel.getProductsOfCurrentListe()
            }

            listeViewModel.addProductResult.observe(viewLifecycleOwner){
                update()
            }
            listeViewModel.decrementeProductResult.observe(viewLifecycleOwner){
                update()
            }
        }

        private fun update(){
            dialog.findViewById<TextView>(R.id.popupDetailsProductTxtName).text = "${product.product.name} x ${product.nb}"

            if(product.nb <= 0)
                dialog.findViewById<View>(R.id.popupDetailsItemMinus).alpha = .5f
            else
                dialog.findViewById<View>(R.id.popupDetailsItemMinus).alpha = 1f
            dialog.findViewById<View>(R.id.popupDetailsItemMinus).isEnabled = product.nb > 0
        }
    }
}