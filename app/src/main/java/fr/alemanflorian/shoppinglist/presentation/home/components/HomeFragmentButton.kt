package fr.alemanflorian.shoppinglist.presentation.home.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import fr.alemanflorian.shoppinglist.R

class HomeFragmentButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr)
{
    init
    {
        if(attrs != null)
        {
            val a = context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.FragmentHomeButton,
                0,
                0
            )

            try
            {
                val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                inflater.inflate(R.layout.fragment_home_button, this)

                val txtButton = a.getString(R.styleable.FragmentHomeButton_text) ?: ""
                val textView = findViewById<TextView>(R.id.FragmentHomeButtonTxt)
                textView.text = txtButton

                val lottie = a.getString(R.styleable.FragmentHomeButton_lottie) ?: ""
                if(lottie.length > 0)
                {
                    val animationView: LottieAnimationView = findViewById(R.id.FragmentHomeButtonLottie)
                    animationView.setAnimation(lottie)
                    animationView.repeatCount = LottieDrawable.INFINITE
                    animationView.playAnimation()
                }
                else
                {
                    val image = a.getDrawable(R.styleable.FragmentHomeButton_imageSrc)
                    if(image != null)
                    {
                        val imageView = findViewById<ImageView>(R.id.FragmentHomeButtonImage)
                        imageView.setImageDrawable(image)
                        imageView.contentDescription = txtButton
                    }
                }

                val backgroundColor = a.getInteger(R.styleable.FragmentHomeButton_backgroundColor, ContextCompat.getColor(context, R.color.main))
                val background = findViewById<FrameLayout>(R.id.FragmentHomeButtonBackground)
                background.setBackgroundColor(backgroundColor)
            }
            catch (e:Exception)
            {
                e.printStackTrace()
            }
            finally
            {
                a.recycle()
            }
        }
    }
}