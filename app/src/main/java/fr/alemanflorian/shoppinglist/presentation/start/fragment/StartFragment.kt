package fr.alemanflorian.shoppinglist.presentation.start.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import fr.alemanflorian.shoppinglist.R
import fr.alemanflorian.shoppinglist.presentation.common.extension.mainNavController
import fr.alemanflorian.shoppinglist.presentation.product.viewmodel.ProductViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.viewModel

class StartFragment : Fragment()
{
    private val viewModel : ProductViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        return inflater.inflate(R.layout.fragment_start, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        GlobalScope.launch(Dispatchers.Default) {
            viewModel.onStart()
            withContext(Dispatchers.Main)
            {
                mainNavController().navigate(StartFragmentDirections.actionStartToHome())
            }
        }
    }
}