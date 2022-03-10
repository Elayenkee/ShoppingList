package fr.alemanflorian.shoppinglist.domain.entity

import fr.alemanflorian.shoppinglist.domain.repository.Repository

data class ListeEnCours(val liste:Liste, val products:List<ProductFromListe>){
    companion object{
        fun create(liste:Liste, repository:Repository):ListeEnCours{
            val products = ArrayList<ProductFromListe>()
            for(p in liste.products)
            {
                val product = repository.getProduct(p.key)
                if(product != null)
                {
                    val productFromListe = ProductFromListe.create(product, p.value.first, p.value.second)
                    products.add(productFromListe)
                }
            }
            products.sortWith(object : Comparator<ProductFromListe>{
                override fun compare(o1: ProductFromListe?, o2: ProductFromListe?): Int {
                    return o1!!.count.compareTo(o2!!.count)
                }
            })
            return ListeEnCours(liste, products)
        }
    }
}