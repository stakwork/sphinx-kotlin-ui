package chat.sphinx.common

/**
 * Reference of all the resources we are using in Sphinx
 *
 * We are using this because we don't have resource identifiers in kotlin multiplatform projects yet
 */
object Res {
    object drawable {
        const val sphinx_logo = "drawable/sphinx_logo.xml"
        const val sphinx_label = "drawable/ic_sphinx_wordmark.xml"
        const val profile_avatar = "drawable/profile_avatar_circle.xml"
        const val ic_key = "drawable/ic_key.xml"
        const val enter_pin = "drawable/ic_enter_pin.xml"
        const val connection_image =  "drawable/ic_preloader.xml"
        const val ic_done =  "drawable/ic_done.xml"
        const val paste_your_invitation =  "drawable/ic_paste_your_invitation__code_into_sphinx.xml"
        const val ic_giphy =  "drawable/ic_giphy_logo_1.xml"
        const val ic_boost_green =  "drawable/ic_boost_green.xml"
        const val ic_boost_gray =  "drawable/ic_boost_gray.xml"
        const val ic_sent =  "drawable/ic_sent.xml"
        const val ic_received =  "drawable/ic_received.xml"
        const val ic_coin =  "drawable/ic_coin.xml"
        const val ic_qr_code =  "drawable/ic_qr_code.xml"
        const val ic_received_image_not_available =  "drawable/received_image_not_available.xml"
        const val copy_paste_your_keys =  "drawable/ic_copy_your_keys_from_your__mobile_app_and_paste_it_here_.xml"

        const val existing_user_image = "drawable/existinguserimage.png"
        const val new_user_image = "drawable/newuserimage.png"
        const val splash_background = "drawable/splash_background.png"
        const val landing_page_image = "drawable/landingpageimage.png"

        const val connection_animation =  "drawable/connecting.gif"
    }

    object string {

    }
    object Font{
        object Montserrat {
            const val black = "font/montserrate/montserrat_black.ttf"
            const val blackitalic = "font/montserrate/montserrat_blackitalic.ttf"
            const val bold = "font/montserrate/montserrat_bold.ttf"
            const val bolditalic = "font/montserrate/montserrat_bolditalic.ttf"
            const val extrabold = "font/montserrate/montserrat_extrabold.ttf"
            const val extrabolditalic = "font/montserrate/montserrat_extrabolditalic.ttf"
            const val extralight = "font/montserrate/montserrat_extralight.ttf"
            const val extralightitalic = "font/montserrate/montserrat_extralightitalic.ttf"
            const val italic = "font/montserrate/montserrat_italic.ttf"
            const val light = "font/montserrate/montserrat_light.ttf"
            const val lightitalic = "font/montserrate/montserrat_lightitalic.ttf"
            const val medium = "font/montserrate/montserrat_medium.ttf"
            const val mediumitalic = "font/montserrate/montserrat_mediumitalic.ttf"
            const val regular = "font/montserrate/montserrat_regular.ttf"
            const val semibold = "font/montserrate/montserrat_semibold.ttf"
            const val semibolditalic = "font/montserrate/montserrat_semibolditalic.ttf"
            const val thin = "font/montserrate/montserrat_thin.ttf"
            const val thinitalic = "font/montserrate/montserrat_thinitalic.ttf"
        }

        object Others {
            const val great_vibes_regular = "font/others/great_vibes_regular.ttf"
        }
    }
}