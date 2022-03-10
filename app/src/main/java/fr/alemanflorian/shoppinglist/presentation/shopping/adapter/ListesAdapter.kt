package fr.alemanflorian.shoppinglist.presentation.shopping.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import fr.alemanflorian.shoppinglist.R
import fr.alemanflorian.shoppinglist.domain.entity.Liste
import kotlinx.android.synthetic.main.fragment_shopping_item_liste.view.*

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

    class ListeViewHolder(inflater: LayoutInflater, parent: ViewGroup, private val interactor: Interactor) : RecyclerView.ViewHolder(inflater.inflate(R.layout.fragment_shopping_item_liste, parent, false))
    {
        private var liste: Liste? = null

        init
        {
            System.err.println(liste)
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