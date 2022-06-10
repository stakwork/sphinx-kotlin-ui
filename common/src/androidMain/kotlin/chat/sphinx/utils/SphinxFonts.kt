package chat.sphinx.utils

import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import chat.sphinx.common.Res
import chat.sphinx.utils.platform.File

actual object SphinxFonts {
    @OptIn(ExperimentalTextApi::class)
    actual val greatVibesFamily: FontFamily
        get() = FontFamily(
            Font(File(Res.Font.Others.great_vibes_regular), FontWeight.Normal),
        )
    @OptIn(ExperimentalTextApi::class)
    actual val montserratFamily: FontFamily
        get() = FontFamily(
            // TODO: The below needs to be a good mix for Android and Desktop... commenting out for the meantime
            Font(File(Res.Font.Montserrat.black), FontWeight.Black),
            Font(File(Res.Font.Montserrat.blackitalic), FontWeight.Black, FontStyle.Italic),
            Font(File(Res.Font.Montserrat.bold), FontWeight.Bold),
            Font(File(Res.Font.Montserrat.bolditalic), FontWeight.Bold, FontStyle.Italic),
            Font(File(Res.Font.Montserrat.extrabold), FontWeight.ExtraBold),
            Font(File(Res.Font.Montserrat.extrabolditalic), FontWeight.ExtraBold, FontStyle.Italic),
            Font(File(Res.Font.Montserrat.extralight), FontWeight.ExtraLight),
            Font(File(Res.Font.Montserrat.extralightitalic), FontWeight.ExtraLight, FontStyle.Italic),
            Font(File(Res.Font.Montserrat.italic), FontWeight.Normal, FontStyle.Italic),
            Font(File(Res.Font.Montserrat.light), FontWeight.Light),
            Font(File(Res.Font.Montserrat.lightitalic), FontWeight.Light, FontStyle.Italic),
            Font(File(Res.Font.Montserrat.medium), FontWeight.Medium),
            Font(File(Res.Font.Montserrat.mediumitalic), FontWeight.Medium, FontStyle.Italic),
            Font(File(Res.Font.Montserrat.regular), FontWeight.Normal),
            Font(File(Res.Font.Montserrat.semibold), FontWeight.SemiBold),
            Font(File(Res.Font.Montserrat.semibolditalic), FontWeight.SemiBold, FontStyle.Italic),
            Font(File(Res.Font.Montserrat.thin), FontWeight.Thin, FontStyle.Italic),
            Font(File(Res.Font.Montserrat.thinitalic), FontWeight.Thin, FontStyle.Italic),
        )
}