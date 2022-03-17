package fr.alemanflorian.shoppinglist.presentation.shopping.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import fr.alemanflorian.shoppinglist.R
import fr.alemanflorian.shoppinglist.domain.entity.Liste
import fr.alemanflorian.shoppinglist.domain.entity.ProductFromListe
import kotlinx.android.synthetic.main.fragment_shopping_item_liste.view.*
import kotlinx.android.synthetic.main.fragment_shopping_item_product.view.*

/**
 * Adapter des liste dans le 1er panneau
 */
class ListesAdapter(private val interactor: Interactor) : RecyclerView.Adapter<ListesAdapter.ListeViewHolder>()
{
    private val listes = ArrayList<Liste>()

    fun setData(data: List<Liste>) {
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
        holder.bind(listes[position])
    }

    override fun getItemCount():Int {
        return listes.size
    }

    class ListeViewHolder(inflater: LayoutInflater, parent: ViewGroup, private val interactor: Interactor) : RecyclerView.ViewHolder(inflater.inflate(
        R.layout.fragment_shopping_item_liste, parent, false))
    {
        private var liste: Liste? = null

        init
        {
            itemView.setOnClickListener{liste?.let { liste -> interactor.onClicked(liste)}}
        }

        fun bind(pListe: Liste)
        {
            liste = pListe
            itemView.itemListeName.text = pListe.name
        }
    }

    interface Interactor
    {
        fun onClicked(liste: Liste)
    }
}

/**
 * Adapter des produits de la liste en cours
 */
class ProductsAdapter(private val interactor: Interactor) : RecyclerView.Adapter<ProductsAdapter.ProductViewHolder>()
{
    private val products = ArrayList<ProductFromListe>()

    fun setData(data: List<ProductFromListe>) {
        products.clear()
        products.addAll(data)
        notifyDataSetChanged()
    }

    fun notifyItemChanged(productFromListe: ProductFromListe){
        val index = products.indexOf(productFromListe)
        notifyItemChanged(index, productFromListe)
        products.sortWith(object : Comparator<ProductFromListe>{
            override fun compare(o1: ProductFromListe?, o2: ProductFromListe?): Int {
                return o1!!.count.compareTo(o2!!.count)
            }
        })
        val newIndex = products.indexOf(productFromListe)
        notifyItemMoved(index, newIndex)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder
    {
        val inflater = LayoutInflater.from(parent.context)
        return ProductViewHolder(inflater, parent, interactor)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int)
    {
        holder.bind(products[position])
    }

    override fun getItemCount() = products.size

    class ProductViewHolder(inflater: LayoutInflater, parent: ViewGroup, private val interactor: Interactor) : RecyclerView.ViewHolder(inflater.inflate(R.layout.fragment_shopping_item_product, parent, false))
    {
        private var product: ProductFromListe? = null

        init
        {
            itemView.setOnClickListener{product?.let { liste -> interactor.onClicked(liste)}}
        }

        fun bind(productFromListe: ProductFromListe)
        {
            product = productFromListe
            itemView.itemProductName.text = "${productFromListe.product.name} x${productFromListe.nb}"
            itemView.itemProductName.alpha = if(productFromListe.count != 0) .5f else 1f
            itemView.itemProductOK.visibility = if(productFromListe.count != 0) View.VISIBLE else View.GONE
        }
    }

    interface Interactor
    {
        fun onClicked(productFromListe: ProductFromListe)
    }
}