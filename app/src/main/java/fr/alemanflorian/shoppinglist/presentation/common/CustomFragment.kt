package fr.alemanflorian.shoppinglist.presentation.common

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import fr.alemanflorian.shoppinglist.Header
import fr.alemanflorian.shoppinglist.MainActivity

open class CustomFragment  : Fragment()
{
    lateinit var header: Header

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
        header = (requireActivity() as MainActivity).header
        header.reset()
    }
}