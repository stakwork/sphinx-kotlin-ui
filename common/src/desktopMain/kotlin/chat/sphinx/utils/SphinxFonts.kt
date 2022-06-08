package chat.sphinx.utils

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.platform.Font
import chat.sphinx.common.Res

actual object SphinxFonts {
    actual val greatVibesFamily: FontFamily
        get() = FontFamily(
            Font(Res.Font.Others.great_vibes_regular, FontWeight.Normal),
        )

    actual val montserratFamily: FontFamily
        get() = FontFamily(
            Font(Res.Font.Montserrat.black, FontWeight.Black),
            Font(Res.Font.Montserrat.blackitalic, FontWeight.Black, FontStyle.Italic),
            Font(Res.Font.Montserrat.bold, FontWeight.Bold),
            Font(Res.Font.Montserrat.bolditalic, FontWeight.Bold, FontStyle.Italic),
            Font(Res.Font.Montserrat.extrabold, FontWeight.ExtraBold),
            Font(Res.Font.Montserrat.extrabolditalic, FontWeight.ExtraBold, FontStyle.Italic),
            Font(Res.Font.Montserrat.extralight, FontWeight.ExtraLight),
            Font(Res.Font.Montserrat.extralightitalic, FontWeight.ExtraLight, FontStyle.Italic),
            Font(Res.Font.Montserrat.italic, FontWeight.Normal, FontStyle.Italic),
            Font(Res.Font.Montserrat.light, FontWeight.Light),
            Font(Res.Font.Montserrat.lightitalic, FontWeight.Light, FontStyle.Italic),
            Font(Res.Font.Montserrat.medium, FontWeight.Medium),
            Font(Res.Font.Montserrat.mediumitalic, FontWeight.Medium, FontStyle.Italic),
            Font(Res.Font.Montserrat.regular, FontWeight.Normal),
            Font(Res.Font.Montserrat.semibold, FontWeight.SemiBold),
            Font(Res.Font.Montserrat.semibolditalic, FontWeight.SemiBold, FontStyle.Italic),
            Font(Res.Font.Montserrat.thin, FontWeight.Thin),
            Font(Res.Font.Montserrat.thin, FontWeight.Thin, FontStyle.Italic),
        )
}