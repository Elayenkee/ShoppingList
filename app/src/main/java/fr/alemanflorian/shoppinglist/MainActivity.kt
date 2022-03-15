package fr.alemanflorian.shoppinglist

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.Transformation
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import fr.alemanflorian.shoppinglist.presentation.common.extension.toPx


class MainActivity : AppCompatActivity() {
    lateinit var header: Header
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        header = Header(findViewById<FrameLayout>(R.id.header))

        val backgroundOne = findViewById<ImageView>(R.id.background_one)
        val backgroundTwo = findViewById<ImageView>(R.id.background_two)

        /*val animator = ValueAnimator.ofFloat(0.0f, 1.0f)
        animator.repeatCount = ValueAnimator.INFINITE
        animator.interpolator = LinearInterpolator()
        animator.duration = 10000L
        animator.addUpdateListener { animation ->
            val progress = animation.animatedValue as Float
            val width = backgroundOne.width
            val translationX = width * progress
            backgroundOne.setTranslationX(translationX)
            backgroundTwo.setTranslationX(translationX - width)
        }
        animator.start()*/
    }
}

class Header(val view: View){
    val container = view.findViewById<ViewGroup>(R.id.header_container)
    var initialHeight:Float = 60.toPx

    fun reset(){
        show()
        setTitle("")
        container.removeAllViews()
    }

    fun show(){
        view.visibility = View.VISIBLE
        //translate(0f)
        //collapse(false)
    }

    fun hide(){
        view.visibility = View.GONE
        //translate(-120f)
        //collapse(true)
    }

    fun setTitle(title: String){
        view.findViewById<TextView>(R.id.header_title)?.setText(title)
    }

    fun addView(layoutID: Int):View
    {
        val v = LayoutInflater.from(view.context).inflate(layoutID, container, false)
        container.addView(v)
        return v
    }

    /*private fun translate(translationYTo : Float)
    {
        val valueAnimator = ValueAnimator.ofFloat(view.translationY, translationYTo.toPx).apply {
            interpolator = LinearInterpolator()
            duration = 300
        }
        valueAnimator.addUpdateListener {
            val value = it.animatedValue as Float
            view.translationY = value
        }
        valueAnimator.start()
    }*/

    private fun collapse(b: Boolean) {
        val h = view.measuredHeight
        val a: Animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                if(b)
                {
                    if (interpolatedTime == 1f)
                    {
                        view.visibility = View.GONE
                    }
                    else
                    {
                        view.layoutParams.height = h - (h * interpolatedTime).toInt()
                        view.requestLayout()
                        System.err.println(">> " + view.layoutParams.height)
                    }
                }
                else
                {
                    if (interpolatedTime == 0f)
                    {
                        view.visibility = View.VISIBLE
                    }
                    else
                    {
                        view.layoutParams.height = h + ((initialHeight - h)* interpolatedTime).toInt()
                        view.requestLayout()
                    }
                }
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }

        // Collapse speed of 1dp/ms
        a.setDuration(150)//(initialHeight / v.context.resources.displayMetrics.density).toLong())
        view.startAnimation(a)
    }
}