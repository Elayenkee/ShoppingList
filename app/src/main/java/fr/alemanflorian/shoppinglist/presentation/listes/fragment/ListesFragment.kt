package fr.alemanflorian.shoppinglist.presentation.listes.fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.LifecycleOwner
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
import fr.alemanflorian.shoppinglist.presentation.common.extension.toPx
import fr.alemanflorian.shoppinglist.presentation.listes.viewmodel.ListesViewModel
import fr.alemanflorian.shoppinglist.presentation.product.adapter.ChangeListeAdapter
import fr.alemanflorian.shoppinglist.presentation.product.adapter.ProductAllAdapter
import fr.alemanflorian.shoppinglist.presentation.product.adapter.ProductFilteredAdapter
import fr.alemanflorian.shoppinglist.presentation.product.adapter.ProductListeAdapter
import fr.alemanflorian.shoppinglist.presentation.product.viewmodel.ProductViewModel
import kotlinx.android.synthetic.main.fragment_listes.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.viewModel

class ListesFragment : CustomFragment()
{
    private val productViewModel : ProductViewModel by viewModel()
    private val listesViewModel : ListesViewModel by viewModel()

    private var allProducts = ArrayList<ProductFromListe>()

    private var popupChangeListe: ChangeListe? = null

    private val adapterAll = ProductAllAdapter(object : ProductAllAdapter.Interactor {
        override fun onProductClicked(product: ProductFromListe) {
            mainNavController().navigate(
                    ListesFragmentDirections.actionListesToProduct(
                            product.product
                    )
            )
        }

        override fun onProductAddToCurrentList(product: ProductFromListe) {
            listesViewModel.addProductToCurrentListe(product)
        }
    })

    private val adapterFiltered = ProductFilteredAdapter(object :ProductFilteredAdapter.Interactor {
        override fun onProductClicked(product: ProductFromListe) {
            listesViewModel.addProductToCurrentListe(product)
            containerFiltre.visibility = View.GONE
            txtSearchProduct.hideKeyboard()
            txtSearchProduct.setText("")
        }
    })

    private val adapterListe = ProductListeAdapter(object : ProductListeAdapter.Interactor{
        override fun onItemDismiss(product: ProductFromListe) {
            listesViewModel.deleteProductFromCurrentListe(product)
        }

        override fun onItemClick(product: ProductFromListe) {
            DetailsProduct(listesViewModel, viewLifecycleOwner, product).show(requireContext())
        }
    })

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View?
    {
        return inflater.inflate(R.layout.fragment_listes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
        initViewObserver()
        initView()
        refresh()
    }

    private fun initViewObserver() {
        productViewModel.saveNewProductResult.observe(viewLifecycleOwner){
            if(it is Resource.Success)
            {
                containerFiltre.visibility = View.GONE
                txtSearchProduct.hideKeyboard()
                txtSearchProduct.setText("")
                refresh()
            }
        }

        productViewModel.getAllProductsWithCurrentNbResult.observe(viewLifecycleOwner) {
            if (it is Resource.Success)
            {
                adapterAll.setData(it.data)
                allProducts.clear()
                allProducts.addAll(it.data)
            }
        }

        listesViewModel.addProductResult.observe(viewLifecycleOwner){
            if(it is Resource.Success)
                refresh()
        }

        listesViewModel.decrementeProductResult.observe(viewLifecycleOwner){
            if(it is Resource.Success)
                refresh()
        }

        listesViewModel.getCurrentListeResult.observe(viewLifecycleOwner){
            if(it is Resource.Success)
            {
                header.setTitle(it.data.liste.name)

                adapterListe.setData(it.data.products)
                layoutEmpty.visibility = if(it.data.products.size > 0) View.GONE else View.VISIBLE
                productListeRecyclerViewParent.visibility = if(it.data.products.size > 0) View.VISIBLE else View.INVISIBLE
                fragmentListesBtnGoShopping.isEnabled = it.data.products.size > 0
                fragmentListesBtnGoShopping.alpha = if (it.data.products.size > 0) 1f else .4f
            }
        }

        listesViewModel.deleteListeResult.observe(viewLifecycleOwner){
            if(it is Resource.Success)
                refresh()
        }

        listesViewModel.saveCurrentListeResult.observe(viewLifecycleOwner){
            if(it is Resource.Success)
                refresh()
        }

        listesViewModel.deleteProductFromCurrentListeResult.observe(viewLifecycleOwner){
            if(it is Resource.Success)
                refresh()
        }

        listesViewModel.saveNewListeResult.observe(viewLifecycleOwner){
            if(it is Resource.Success)
            {
                refresh()
                popupChangeListe?.dismiss()
                popupChangeListe = null
            }
        }
    }

    @SuppressLint("WrongConstant")
    private fun initView()
    {
        //region Header
        header.addView(R.layout.button_change_liste).setOnClickListener {
            hideKeyboard()
            popupChangeListe = ChangeListe(listesViewModel, viewLifecycleOwner)
            popupChangeListe!!.show(requireContext())
        }
        //endregion

        //region Liste en cours
        productListeRecyclerView.adapter = adapterListe
        adapterListe.addSwipeListener(productListeRecyclerView)
        //endregion

        //region Tous les produits
        productAllRecyclerView.adapter = adapterAll
        @Suppress("DEPRECATION")
        drawer.setScrimColor(getResources().getColor(android.R.color.transparent))
        drawer.setDrawerShadow(android.R.color.transparent, GravityCompat.START)
        drawer.elevation = 15f
        drawer.addDrawerListener(object : DrawerLayout.DrawerListener{
            var opened = false
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}
            override fun onDrawerOpened(drawerView: View) {
                opened = true
                productListeRecyclerViewParent.translationX = -70f.toPx
            }
            override fun onDrawerClosed(drawerView: View) {
                opened = false
                productListeRecyclerViewParent.translationX = 0f
            }
            override fun onDrawerStateChanged(newState: Int) {
                if(newState == 2)
                    opened = !opened
                productListeRecyclerViewParent.translationX = if(opened)-70f.toPx else 0f
            }

        })
        btnAllProducts.setOnClickListener{
            hideKeyboard()
            drawer.openDrawer(Gravity.END)
        }
        //endregion

        //region Recherche
        productFilteredRecyclerView.adapter = adapterFiltered
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
        txtSearchProduct.setOnFocusChangeListener { _, hasFocus ->  if(hasFocus) onTextChanged(
            txtSearchProduct.text
        ) else containerFiltre.performClick()}
        txtSearchProduct.setOnEditorActionListener { _, actionId, _ ->
            if(actionId == EditorInfo.IME_ACTION_DONE)
            {
                val name = txtSearchProduct.text.toString()
                val product = productExists(name)
                if(product != null)
                {
                    listesViewModel.addProductToCurrentListe(ProductFromListe(product))
                    containerFiltre.visibility = View.GONE
                    txtSearchProduct.hideKeyboard()
                    txtSearchProduct.setText("")
                }
                else
                {
                    questionYesNo("Voulez-vous ajouter '$name' dans l'application ?", {productViewModel.saveNewProduct(Product(name))}, {})
                }
            }
            false
        }
        containerFiltre.setOnClickListener{
            containerFiltre.visibility = View.GONE
            txtSearchProduct.hideKeyboard()
        }
        //endregion

        //region Bouton Finish
        fragmentListesBtnGoShopping.isEnabled = false
        fragmentListesBtnGoShopping.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO) {
                listesViewModel.setCurrentListeAsListeEnCours()
                withContext(Dispatchers.Main)
                {
                    mainNavController().navigate(ListesFragmentDirections.actionListesToShopping())
                }
            }
        }
        //endregion
    }

    private fun refresh()
    {
        productViewModel.getAllProductsWithCurrentNb()
        listesViewModel.getCurrentListe()
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

    class ChangeListe(val listesViewModel : ListesViewModel, val viewLifecycleOwner: LifecycleOwner){
        private lateinit var context: Context
        private lateinit var dialog: Dialog
        private val adapterListe = ChangeListeAdapter(object : ChangeListeAdapter.Interactor{
            override fun onClicked(liste: Liste)
            {
                listesViewModel.saveCurrentListe(liste)
                dialog.dismiss()
            }

            override fun delete(liste: Liste)
            {
                val builder = AlertDialog.Builder(context)
                builder.setMessage("Supprimer cette liste ?")
                builder.setPositiveButton(android.R.string.ok) { _, _ ->
                    listesViewModel.deleteListe(liste)
                }
                builder.setNegativeButton(android.R.string.cancel) { _, _ -> }
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
            listesViewModel.getAllListeResult.observe(viewLifecycleOwner){
                if (it is Resource.Success)
                {
                    adapterListe.setData(it.data)
                }
            }
            listesViewModel.getAllListe()

            listesViewModel.deleteListeResult.observe(viewLifecycleOwner){
                listesViewModel.getAllListe()
            }

            val txtListe = dialog.findViewById<TextView>(R.id.popupChangeListeTxtListe)
            val createNewListe = {
                if(txtListe.text.toString().length > 0)
                {
                    val newListe = Liste(0, txtListe.text.toString(), LinkedHashMap())
                    listesViewModel.saveNewListe(newListe)
                    txtListe.text = ""
                }
            }
            txtListe.setOnEditorActionListener { _, actionId, _ ->
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
                var height = MeasureSpec.makeMeasureSpec(500, MeasureSpec.AT_MOST)
                super.onMeasure(widthSpec, height)
            }
        }
    }

    class DetailsProduct(val listesViewModel: ListesViewModel, val viewLifecycleOwner: LifecycleOwner, val product:ProductFromListe){
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
                listesViewModel.addProductToCurrentListe(product)
            }

            dialog.setOnDismissListener {
                listesViewModel.getCurrentListe()
            }

            listesViewModel.addProductResult.observe(viewLifecycleOwner){
                update()
            }
            listesViewModel.decrementeProductResult.observe(viewLifecycleOwner){
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