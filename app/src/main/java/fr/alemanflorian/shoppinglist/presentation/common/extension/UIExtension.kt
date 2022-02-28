package fr.alemanflorian.shoppinglist.presentation.common.extension

import android.animation.ValueAnimator
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.res.Resources
import android.util.TypedValue
import android.view.View
import android.view.animation.LinearInterpolator
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import fr.alemanflorian.shoppinglist.R
import fr.alemanflorian.shoppinglist.domain.entity.Product
import kotlinx.coroutines.Dispatchers

fun ViewModel.contextIO() = viewModelScope.coroutineContext + Dispatchers.IO

fun Fragment.mainNavController() = requireActivity().findNavController(R.id.nav_host_fragment)

fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}

fun Activity.hideKeyboard() {
    hideKeyboard(currentFocus ?: View(this))
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun View.hideKeyboard() {
    val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputManager.hideSoftInputFromWindow(windowToken, 0)
}

val Number.toPx get() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), Resources.getSystem().displayMetrics)

fun View.translate(translationXTo : Float, translationYTo : Float, pDuration: Long){
    val valueAnimatorX = ValueAnimator.ofFloat(translationX, translationXTo.toPx).apply {
        interpolator = LinearInterpolator()
        duration = pDuration
    }
    valueAnimatorX.addUpdateListener {
        val value = it.animatedValue as Float
        translationX = value
    }
    valueAnimatorX.start()

    val valueAnimatorY = ValueAnimator.ofFloat(translationY, translationYTo.toPx).apply {
        interpolator = LinearInterpolator()
        duration = pDuration
    }
    valueAnimatorY.addUpdateListener {
        val value = it.animatedValue as Float
        translationY = value
    }
    valueAnimatorY.start()
}

fun questionYesNo(context: Context, message:String, onYes: () -> Unit, onNo: () -> Unit)
{
    lateinit var dialog: AlertDialog
    val builder = AlertDialog.Builder(context)
    builder.setMessage(message)

    val dialogClickListener = DialogInterface.OnClickListener{ _, which ->
        when(which){
            DialogInterface.BUTTON_POSITIVE -> onYes()
            DialogInterface.BUTTON_NEGATIVE -> onNo()
        }
    }
    builder.setPositiveButton("OUI",dialogClickListener)
    builder.setNegativeButton("NON",dialogClickListener)
    dialog = builder.create()
    dialog.show()
}