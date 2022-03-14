package fr.alemanflorian.shoppinglist.presentation.common.extension

import android.animation.ValueAnimator
import android.annotation.TargetApi
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.res.Resources
import android.graphics.drawable.LayerDrawable
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import fr.alemanflorian.shoppinglist.R
import fr.alemanflorian.shoppinglist.domain.entity.ProductFromListe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

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

val Number.toPx get() = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    this.toFloat(),
    Resources.getSystem().displayMetrics
)

fun View.translate(translationXTo: Float, translationYTo: Float, pDuration: Long){
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

fun questionYesNo(context: Context, message: String, onYes: () -> Unit, onNo: () -> Unit)
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
    builder.setPositiveButton("OUI", dialogClickListener)
    builder.setNegativeButton("NON", dialogClickListener)
    dialog = builder.create()
    dialog.show()
}

/*@TargetApi(21)
fun View.addRippleEffect() {
    val layerDrawable = context.resources.getDrawable(R.drawable.dummy_ripple, null) as LayerDrawable
    layerDrawable.mutate()
    val background = background
    if (background != null) {
        layerDrawable.setDrawableByLayerId(R.id.dummy_ripple_drawable, background)
    }
    setBackground(layerDrawable)
}*/

/*fun View.alphaEffect(parent: View?) {
    setOnTouchListener(object : OnTouchListener {
        override fun onTouch(view: View, event: MotionEvent): Boolean {
            val action = event.action
            if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_HOVER_EXIT || action == MotionEvent.ACTION_BUTTON_RELEASE || action == MotionEvent.ACTION_OUTSIDE) {
                up()
            }
            if (action == MotionEvent.ACTION_DOWN) {
                down()
                parent?.setOnTouchListener { _, motionEvent ->
                    val action = motionEvent.action
                    if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_HOVER_EXIT || action == MotionEvent.ACTION_BUTTON_RELEASE || action == MotionEvent.ACTION_OUTSIDE) {
                        parent.setOnTouchListener(null)
                        up()
                    }
                    false
                }
            }
            return false
        }

        private fun up() {
            alpha = 1f
        }

        private fun down() {
            alpha = .8f
            alpha = .8f
        }
    })
}*/

fun View.clickEffect(parent: View?) {
    setOnTouchListener(object : OnTouchListener {
        override fun onTouch(view: View, event: MotionEvent): Boolean {
            val action = event.action
            if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_HOVER_EXIT || action == MotionEvent.ACTION_BUTTON_RELEASE || action == MotionEvent.ACTION_OUTSIDE) {
                up()
            }
            if (action == MotionEvent.ACTION_DOWN) {
                down()
                parent?.setOnTouchListener { _, motionEvent ->
                    val a = motionEvent.action
                    if (a == MotionEvent.ACTION_UP || a == MotionEvent.ACTION_HOVER_EXIT || a == MotionEvent.ACTION_BUTTON_RELEASE || a == MotionEvent.ACTION_OUTSIDE) {
                        parent.setOnTouchListener(null)
                        up()
                    }
                    false
                }
            }
            return false
        }

        private fun up() {
            scale(1f)
        }

        private fun down() {
            scale(.95f)
        }

        private fun scale(scaleTo : Float)
        {
            val valueAnimator = ValueAnimator.ofFloat(scaleX, scaleTo).apply {
                interpolator = DecelerateInterpolator()
                duration = 50
            }
            valueAnimator.addUpdateListener {
                val value = it.animatedValue as Float
                scaleX = value
                scaleY = value
            }
            valueAnimator.start()
        }
    })
}