package fr.alemanflorian.shoppinglist.data.repository

import android.app.Application
import android.content.Context
import fr.alemanflorian.shoppinglist.data.database.ListeDao
import fr.alemanflorian.shoppinglist.data.database.ProductDao
import fr.alemanflorian.shoppinglist.domain.entity.Liste
import fr.alemanflorian.shoppinglist.domain.entity.Product
import fr.alemanflorian.shoppinglist.domain.repository.Repository

class RepositoryImpl(private val productDao: ProductDao, private val listeDao : ListeDao, appContext: Application) : Repository{
    private val sharedPrefs = appContext.getSharedPreferences(PREFERENCE_FILE_NAME, Context.MODE_PRIVATE)

    val cachedProducts = mutableMapOf<Long, Product>()
    val cachedListes = mutableMapOf<Long, Liste>()

    var cachedCurrentListe: Liste? = null

    var initialized = false

    override fun onStartApp() {
        if(initialized)
            return

        val count = productDao.count()
        if(count <= 10)
        {
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
        }
    }

    private fun insertAll(vararg all : String)
    {
        for(name : String in all)
        {
            try {
                productDao.insert(Product(name).toResponse())
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
        val listeResponse = liste.toResponse()
        val id = listeDao.insert(listeResponse)
        listeResponse.id = id
        liste.id = id
        setCachedListe(liste)
    }

    override fun getListe(id: Long): Liste? {
        return listeDao.find(id)?.toModel()
    }

    override suspend fun getAllListes(): List<Liste> {
        return listeDao.getAll().map { it.toModel() }
    }

    override suspend fun deleteListe(liste: Liste) {
        listeDao.delete(liste = liste.toResponse())
        val listeID = liste.id
        val currentListeID = getCurrentListe(false, false)?.id
        if(listeID == currentListeID)
        {
            val newCurrentListe = listeDao.findFirstListe()
            if(newCurrentListe != null)
            {
                saveCurrentListe(newCurrentListe.toModel())
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
                setListeEnCours(cachedCurrentListe!!)
            }
        }
        return cachedCurrentListe
    }

    override suspend fun getCurrentListe(): Liste {
        return getCurrentListe(true, true)!!
    }

    override fun saveCurrentListe(liste:Liste)
    {
        cachedCurrentListe = liste
        sharedPrefs.edit().putLong(CURRENT_LISTE_ID, liste.id).apply()
    }

    override suspend fun hasListeEnCours():Boolean{
        return getListeEnCours() != null
    }

    override suspend fun getListeEnCours(): Liste? {
        val listeID = sharedPrefs.getLong(LISTE_ENCOURS_ID, -1)
        var liste: Liste? = null
        if(listeID > 0)
        {
            liste = getListe(listeID)
            if(liste != null && liste.products.isEmpty())
                liste = null
        }
        return liste
    }

    override suspend fun finishListeEnCours() {
        val liste = getListeEnCours()
        if(liste != null)
        {
            liste.reset()
            saveListe(liste)
            sharedPrefs.edit().putLong(LISTE_ENCOURS_ID, -1).apply()
        }
    }

    override fun setListeEnCours(liste: Liste) {
        sharedPrefs.edit().putLong(LISTE_ENCOURS_ID, liste.id).apply()
    }

    companion object {
        private const val PREFERENCE_FILE_NAME = "MyListe"
        private const val CURRENT_LISTE_ID = "currentListeID"
        private const val LISTE_ENCOURS_ID = "listeEnCoursID"
    }
}