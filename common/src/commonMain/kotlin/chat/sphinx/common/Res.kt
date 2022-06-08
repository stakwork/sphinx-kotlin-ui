package chat.sphinx.common

/**
 * Reference of all the resources we are using in Sphinx
 *
 * We are using this because we don't have resource identifiers in kotlin multiplatform projects yet
 */
object Res {
    object drawable {
        val sphinx_logo = "drawable/sphinx_logo.xml"
        val IC_KEY = "drawable/ic_key.xml"
        val existing_user_image = "drawable/existinguserimage.png"
        val new_user_image = "drawable/newuserimage.png"
        val enter_pin = "drawable/ic_enter_pin.xml"
        val landing_page_image = "drawable/landingpageimage.png"
        val LANDING_WORD_MARK =  "drawable/ic_wordmark.xml"
        val connection_image =  "drawable/ic_preloader.xml"
        val ic_done =  "drawable/ic_done.xml"
        val paste_your_invitation =  "drawable/ic_paste_your_invitation__code_into_sphinx.xml"
        val copy_paste_your_keys =  "drawable/ic_copy_your_keys_from_your__mobile_app_and_paste_it_here_.xml"
    }

    object string {

    }
    object Font{
        object Montserrat {
            val black = "font/montserrate/montserrat_black.ttf"
            val blackitalic = "font/montserrate/montserrat_blackitalic.ttf"
            val bold = "font/montserrate/montserrat_bold.ttf"
            val bolditalic = "font/montserrate/montserrat_bolditalic.ttf"
            val extrabold = "font/montserrate/montserrat_extrabold.ttf"
            val extrabolditalic = "font/montserrate/montserrat_extrabolditalic.ttf"
            val extralight = "font/montserrate/montserrat_extralight.ttf"
            val extralightitalic = "font/montserrate/montserrat_extralightitalic.ttf"
            val italic = "font/montserrate/montserrat_italic.ttf"
            val light = "font/montserrate/montserrat_light.ttf"
            val lightitalic = "font/montserrate/montserrat_lightitalic.ttf"
            val medium = "font/montserrate/montserrat_medium.ttf"
            val mediumitalic = "font/montserrate/montserrat_mediumitalic.ttf"
            val regular = "font/montserrate/montserrat_regular.ttf"
            val semibold = "font/montserrate/montserrat_semibold.ttf"
            val semibolditalic = "font/montserrate/montserrat_semibolditalic.ttf"
            val thin = "font/montserrate/montserrat_thin.ttf"
            val thinitalic = "font/montserrate/montserrat_thinitalic.ttf"
        }

        object Others {
            val great_vibes_regular = "font/others/great_vibes_regular.ttf"
        }
    }
}