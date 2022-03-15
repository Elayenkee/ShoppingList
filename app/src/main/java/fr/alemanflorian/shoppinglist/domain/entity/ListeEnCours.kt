package fr.alemanflorian.shoppinglist.domain.entity

import fr.alemanflorian.shoppinglist.domain.repository.Repository

class ListeEnCours(liste:Liste, repository: Repository):ListeAvecProduits(liste, repository){
    init {
        products.sortWith(object : Comparator<ProductFromListe>{
            override fun compare(o1: ProductFromListe?, o2: ProductFromListe?): Int {
                return o1!!.count.compareTo(o2!!.count)
            }
        })
    }
}