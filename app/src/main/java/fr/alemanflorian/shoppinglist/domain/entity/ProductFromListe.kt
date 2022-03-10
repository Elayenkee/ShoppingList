package fr.alemanflorian.shoppinglist.domain.entity

class ProductFromListe(val product: Product, var nb : Int, var count:Int){

    companion object{
        fun create(product: Product):ProductFromListe{
            return ProductFromListe(product, 0, 0);
        }

        fun create(product: Product, nb: Int):ProductFromListe{
            return ProductFromListe(product, nb, 0)
        }

        fun create(product: Product, nb: Int, ok:Int):ProductFromListe{
            return ProductFromListe(product, nb, ok)
        }
    }

}