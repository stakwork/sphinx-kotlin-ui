package utils

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color



import com.example.compose.*



fun getRandomColorRes(): Color {
    return listOf<Color>(
      randomColor1,
      randomColor2,
      randomColor3,
      randomColor4,
      randomColor5,
      randomColor6,
      randomColor7,
      randomColor8,
      randomColor9,
      randomColor10,
      randomColor11,
      randomColor12,
      randomColor13,
      randomColor14,
      randomColor15,
      randomColor16,
      randomColor17,
      randomColor18,
      randomColor19,
      randomColor20,
    ).shuffled()[0]
}

fun Modifier.conditional(condition : Boolean, modifier : Modifier.() -> Modifier) : Modifier {
  return if (condition) {
    modifier.invoke(this)
  } else {
    this
  }
}
