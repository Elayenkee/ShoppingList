package fr.alemanflorian.shoppinglist.presentation.product.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import fr.alemanflorian.shoppinglist.R
import fr.alemanflorian.shoppinglist.domain.entity.Liste
import kotlinx.android.synthetic.main.fragment_listes_item_liste.view.*

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