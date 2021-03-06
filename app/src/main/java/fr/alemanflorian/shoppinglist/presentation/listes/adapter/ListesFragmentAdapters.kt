package fr.alemanflorian.shoppinglist.presentation.listes.adapter

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import fr.alemanflorian.shoppinglist.R
import fr.alemanflorian.shoppinglist.domain.entity.Liste
import fr.alemanflorian.shoppinglist.domain.entity.ProductFromListe
import kotlinx.android.synthetic.main.fragment_listes_item_liste.view.*
import kotlinx.android.synthetic.main.item_product.view.*
import kotlinx.android.synthetic.main.item_product_filtered.view.*
import kotlinx.android.synthetic.main.item_product_liste.view.*
import java.util.*
import kotlin.collections.ArrayList

/**
 * Adapter pour les listes dans la popup de changement de liste
 */
class ChangeListeAdapter(private val interactor: Interactor) : RecyclerView.Adapter<ChangeListeAdapter.ListeViewHolder>()
{
    private val listes = ArrayList<Liste>()

    fun setData(data: List<Liste>)
    {
        listes.clear()
        listes.addAll(data)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListeViewHolder
    {
        val inflater = LayoutInflater.from(parent.context)
        return ListeViewHolder(inflater, parent, interactor)
    }

    override fun onBindViewHolder(holder: ListeViewHolder, position: Int)
    {
        holder.bind(listes[position], itemCount)
    }

    override fun getItemCount() = listes.size

    class ListeViewHolder(inflater: LayoutInflater, parent: ViewGroup, private val interactor: Interactor) : RecyclerView.ViewHolder(inflater.inflate(R.layout.fragment_listes_item_liste, parent, false))
    {
        private var data: Liste? = null

        init
        {
            itemView.setOnClickListener{data?.let { liste -> interactor.onClicked(liste)}}
            itemView.itemListeBtnDelete.setOnClickListener{data?.let { liste -> interactor.delete(liste)}}
        }

        fun bind(liste: Liste, size:Int)
        {
            data = liste
            itemView.itemListeName.text = liste.name

            if(size > 1)
                itemView.itemListeBtnDelete.visibility = View.VISIBLE
            else
                itemView.itemListeBtnDelete.visibility = View.GONE
        }
    }

    interface Interactor
    {
        fun onClicked(liste:Liste)
        fun delete(liste:Liste)
    }
}

/**
 * Adapter pour la liste de tous les produits dans le drawer
 */
class ProductAllAdapter(private val interactor: Interactor) : RecyclerView.Adapter<ProductAllAdapter.ProductViewHolder>()
{
    private val products = ArrayList<Any>()

    fun setData(data: List<ProductFromListe>) {
        products.clear()

        val list = ArrayList<Any>()
        var current = ""
        for(d in data)
        {
            val firstLetter = d.product.uniqueName[0].toString()
            if(firstLetter != current)
            {
                current = firstLetter
                list.add(firstLetter.toUpperCase(Locale.getDefault()))
            }
            list.add(d)
        }

        products.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder
    {
        val inflater = LayoutInflater.from(parent.context)
        return ProductViewHolder(inflater, parent, interactor)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int)
    {
        holder.bind(products[position], position)
    }

    override fun getItemCount() = products.size

    class ProductViewHolder(
        inflater: LayoutInflater,
        parent: ViewGroup,
        private val interactor: Interactor
    ) : RecyclerView.ViewHolder(inflater.inflate(R.layout.item_product, parent, false))
    {
        private var data: ProductFromListe? = null

        init
        {
            init(parent.context)
            /*itemView.setOnLongClickListener { data?.let { product -> interactor.onProductClicked(
                product) };true }*/
            itemView.setOnClickListener {
                data?.let { product ->  interactor.onProductAddToCurrentList(product)}
            }
        }

        fun bind(o: Any, position: Int) {
            if(o is ProductFromListe)
            {
                data = o
                val strNb = (if(o.nb > 0)" x " + o.nb else "")
                itemView.productName.text = "${o.product.name} $strNb"
                itemView.productName.textSize = 16f
                itemView.productName.setTypeface(null, if(o.nb > 0) Typeface.BOLD else Typeface.NORMAL)

            }
            else if(o is String)
            {
                itemView.productName.text = o
                itemView.productName.setTypeface(null, Typeface.BOLD_ITALIC)
                itemView.productName.textSize = 25f
            }
            itemView.setBackgroundColor(if (position % 2 == 0) COLOR1 else COLOR2)
        }

        companion object
        {
            var COLOR1:Int = 0
            var COLOR2:Int = 0

            fun init(context: Context){
                if(COLOR1 == 0)
                {
                    COLOR1 = ContextCompat.getColor(context, R.color.main)
                    COLOR1 = ColorUtils.setAlphaComponent(COLOR1, 55)

                    COLOR2 = ContextCompat.getColor(context, R.color.second)
                    COLOR2 = ColorUtils.setAlphaComponent(COLOR2, 55)
                }
            }
        }
    }

    interface Interactor {
        fun onProductAddToCurrentList(product: ProductFromListe)
    }
}

/**
 * Adapter pour les produits filtr??s dans la recherche
 */
class ProductFilteredAdapter(private val interactor: Interactor) : RecyclerView.Adapter<ProductFilteredAdapter.ProductViewHolder>()
{
    private val products = ArrayList<ProductFromListe>()

    fun setData(data: List<ProductFromListe>) {
        products.clear()
        products.addAll(data)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ProductViewHolder(inflater, parent, { interactor.onProductClicked(it) })
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(products[position])
    }

    override fun getItemCount() = products.size

    class ProductViewHolder(
        inflater: LayoutInflater,
        parent: ViewGroup,
        private val onClick: (ProductFromListe) -> Unit
    ) : RecyclerView.ViewHolder(inflater.inflate(R.layout.item_product_filtered, parent, false))
    {
        private var data: ProductFromListe? = null

        init {
            itemView.setOnClickListener {
                data?.let { product ->  onClick.invoke(product)}
            }
        }

        fun bind(product: ProductFromListe) {
            data = product
            itemView.productNameFiltered.text = product.product.name
        }
    }

    interface Interactor {
        fun onProductClicked(product: ProductFromListe)
    }
}

/**
 * Adapter pour les produits de la liste affich??e
 */
class ProductListeAdapter(private val interactor: Interactor) : RecyclerView.Adapter<ProductListeAdapter.ProductViewHolder>()
{
    private val products = ArrayList<ProductFromListe>()

    fun addSwipeListener(listView:RecyclerView){
        val callback = SwipeHelperCallback(this)
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(listView)
    }

    fun setData(data: List<ProductFromListe>) {
        products.clear()
        products.addAll(data)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ProductViewHolder(inflater, parent, interactor)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(products[position])
    }

    override fun getItemCount() = products.size

    fun onItemDismiss(position: Int)
    {
        val product = products[position]
        products.removeAt(position)
        notifyItemRemoved(position)
        interactor.onItemDismiss(product)
    }

    interface Interactor {
        fun onItemDismiss(product: ProductFromListe)
        fun onItemClick(product: ProductFromListe)
    }

    class ProductViewHolder(inflater: LayoutInflater, parent: ViewGroup, interactor: Interactor) : RecyclerView.ViewHolder(
        inflater.inflate(
            R.layout.item_product_liste,
            parent,
            false))
    {
        private var data: ProductFromListe? = null

        init
        {
            itemView.setOnClickListener {
                data?.let { product ->  interactor.onItemClick(product)}
            }
        }

        fun bind(product: ProductFromListe)
        {
            data = product
            itemView.productNameListe.text = "${product.product.name} x ${product.nb}"
        }
    }

    class SwipeHelperCallback(val adapter: ProductListeAdapter) : ItemTouchHelper.Callback()
    {
        override fun getMovementFlags(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
        ): Int
        {
            val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
            val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
            return makeMovementFlags(dragFlags, swipeFlags)
        }

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean
        {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int)
        {
            adapter.onItemDismiss(viewHolder.bindingAdapterPosition)
        }
    }
}