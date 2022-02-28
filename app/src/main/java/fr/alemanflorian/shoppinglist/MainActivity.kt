package fr.alemanflorian.shoppinglist

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import fr.alemanflorian.shoppinglist.presentation.common.extension.toPx

class MainActivity : AppCompatActivity() {
    lateinit var header: Header
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        header = Header(findViewById<FrameLayout>(R.id.header))
    }
}

class Header(val view : View){
    val container = view.findViewById<ViewGroup>(R.id.header_container)

    fun reset(){
        show()
        setTitle("")
        container.removeAllViews()
    }

    fun show(){
        //view.visibility = View.VISIBLE
        translate(0f)
    }

    fun hide(){
        //view.visibility = View.GONE
        translate(-120f)
    }

    fun setTitle(title: String){
        view.findViewById<TextView>(R.id.header_title)?.setText(title)
    }

    fun addView(layoutID : Int):View
    {
        val v = LayoutInflater.from(view.context).inflate(layoutID, container, false)
        container.addView(v)
        return v
    }

    private fun translate(translationYTo : Float)
    {
        val valueAnimator = ValueAnimator.ofFloat(view?.translationY, translationYTo.toPx).apply {
            interpolator = LinearInterpolator()
            duration = 300
        }
        valueAnimator.addUpdateListener {
            val value = it.animatedValue as Float
            view?.translationY = value
        }
        valueAnimator.start()
    }
}