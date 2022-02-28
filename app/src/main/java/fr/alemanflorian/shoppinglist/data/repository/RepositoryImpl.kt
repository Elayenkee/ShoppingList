package fr.alemanflorian.shoppinglist.data.repository

import android.app.Application
import android.content.Context
import fr.alemanflorian.shoppinglist.data.database.ListeDao
import fr.alemanflorian.shoppinglist.data.database.ProductDao
import fr.alemanflorian.shoppinglist.domain.entity.Liste
import fr.alemanflorian.shoppinglist.domain.entity.Product
import fr.alemanflorian.shoppinglist.domain.repository.Repository
import java.lang.Exception

class RepositoryImpl(private val productDao: ProductDao, private val listeDao : ListeDao, appContext: Application) : Repository{
    private val sharedPrefs = appContext.getSharedPreferences(PREFERENCE_FILE_NAME, Context.MODE_PRIVATE)

    val cachedProducts = mutableMapOf<Long, Product>()
    val cachedListes = mutableMapOf<Long, Liste>()

    var cachedCurrentListe: Liste? = null

    override fun onStart() {
        val count = productDao.count()
        if(count <= 10 || true)
        {
            System.err.println("RepositoryImpl :: onStart : " + System.currentTimeMillis())
            insertAll("ail", "bagels", "baguette", "brioches", "chapelure", "croissant", "croûtons", "crêpes", "donuts",
                "gauffres", "pain", "pain complet", "pain de mie", "abricot", "amandes", "ananas", "artichaut", "asperges", "aubergine", "avocat",
                "banane", "basilic", "betterave", "blettes", "brocoli", "cacahuètes", "carottes", "cerises", "champignons", "chou", "ciboulette",
                "citron", "concombre", "coriande", "cornichons", "courgette", "céleri", "figues", "fraises", "kiwi", "melon", "oignons",
                "olives noires", "orange", "petits pois", "poire", "pomme", "patates", "raisin", "couscous", "farine", "lentilles", "pâtes", "riz",
                "avoine", "couscous", "moules", "frites", "chorizo", "langouste", "poulet", "saucisses", "chocolat", "miel", "beurre", "gruyère",
                "lait", "oeufs", "mozarella", "parmesan", "lasagnes", "pâté", "sushis", "soupe", "huile d'olive", "ketchup", "mayonnaise",
                "moutarde", "vinaigre", "lentilles", "poivre", "sel", "sucre", "bières", "café", "eau", "dentifrice", "coton", "couches",
                "gel douche", "shampooing", "éponges", "ampoule", "ciseaux"
            )
            System.err.println("RepositoryImpl :: onStart > " + System.currentTimeMillis())
        }
    }

    private fun insertAll(vararg all : String)
    {
        for(name : String in all)
        {
            try {
                productDao.insert(Product(0, name).toResponse())
            }
            catch(e:Exception){}
        }
    }

    override fun setCachedProduct(product: Product) {
        cachedProducts.put(product.id, product)
    }

    override fun getCachedProduct(id: Long): Product? {
        return cachedProducts.get(id)
    }

    override fun saveProduct(product: Product) {
        val productResponse = product.toResponse()
        val id = productDao.insert(productResponse)
        productResponse.id = id
        product.id = id
        setCachedProduct(product)
    }

    override fun getProduct(id: Long): Product? {
        return productDao.find(id)?.toModel()
    }

    override fun getAllProducts(): List<Product> {
        return productDao.getAll().map { it.toModel() }
    }

    override fun setCachedListe(liste: Liste) {
        cachedListes.put(liste.id, liste)
    }

    override fun getCachedListe(id: Long): Liste? {
        return cachedListes.get(id)
    }

    override suspend fun saveListe(liste: Liste) {
        System.err.println("RepoImpl::saveListe {$liste}")
        val listeResponse = liste.toResponse()
        val id = listeDao.insert(listeResponse)
        listeResponse.id = id
        liste.id = id
        setCachedListe(liste)
    }

    override fun getListe(id: Long): Liste? {
        return listeDao.find(id)?.toModel()
    }

    override fun getAllListes(): List<Liste> {
        return listeDao.getAll().map { it.toModel() }
    }

    override suspend fun deleteListe(liste: Liste) {
        listeDao.delete(liste = liste.toResponse())
        val listeID = liste.id;
        val currentListeID = getCurrentListe(false, false)?.id;
        if(listeID == currentListeID)
        {
            val newCurrentListe = listeDao.findFirstListe()
            if(newCurrentListe != null)
            {
                saveCurrentListe(newCurrentListe!!.toModel())
            }
            else
            {
                sharedPrefs.edit().putLong(CURRENT_LISTE_ID, 0).apply()
                cachedCurrentListe = null
                getCurrentListe()
            }
        }
    }

    suspend fun getCurrentListe(getFirstListe:Boolean, createIfNeeded: Boolean):Liste?{
        if(cachedCurrentListe == null)
        {
            val listeID = sharedPrefs.getLong(CURRENT_LISTE_ID, -1)
            if(listeID > 0)
            {
                cachedCurrentListe = listeDao.find(listeID)?.toModel()
            }

            if(cachedCurrentListe == null && getFirstListe)
            {
                cachedCurrentListe = listeDao.findFirstListe()?.toModel()
            }

            if(cachedCurrentListe == null && createIfNeeded)
            {
                cachedCurrentListe = Liste(0, "Ma liste", LinkedHashMap())
                saveListe(cachedCurrentListe!!)
                saveCurrentListe(cachedCurrentListe!!)
            }
        }
        return cachedCurrentListe
    }

    override suspend fun getCurrentListe(): Liste {
        return getCurrentListe(true, true)!!
    }

    override fun saveCurrentListe(liste:Liste)
    {
        System.err.println("RepoImpl::saveCurrentListe {$liste}")
        cachedCurrentListe = liste
        sharedPrefs.edit().putLong(CURRENT_LISTE_ID, liste.id).apply()
    }

    companion object {
        private const val PREFERENCE_FILE_NAME = "MyListe"
        private const val CURRENT_LISTE_ID = "currentListeID"
    }
}