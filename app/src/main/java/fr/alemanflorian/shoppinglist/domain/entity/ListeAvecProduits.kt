package fr.alemanflorian.shoppinglist.domain.entity

import fr.alemanflorian.shoppinglist.domain.repository.Repository

open class ListeAvecProduits(val liste:Liste, val products:ArrayList<ProductFromListe> = ArrayList()){
    constructor(liste:Liste, repository: Repository) : this(liste) {
        for(p in liste.products)
        {
            val product = repository.getProduct(p.key)
            if(product != null)
            {
                val productFromListe = ProductFromListe.create(product, p.value.first, p.value.second)
                products.add(productFromListe)
            }
        }
        products.reverse()
    }
}