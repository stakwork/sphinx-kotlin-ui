package chat.sphinx.utils.linkify

import androidx.annotation.IntDef
import androidx.annotation.RestrictTo

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP_PREFIX)
@IntDef(
    flag = true,
    value = [SphinxLinkify.WEB_URLS, SphinxLinkify.EMAIL_ADDRESSES, SphinxLinkify.PHONE_NUMBERS, SphinxLinkify.LIGHTNING_NODE_PUBLIC_KEY, SphinxLinkify.VIRTUAL_NODE_ADDRESS, SphinxLinkify.ALL]
)
@Retention(AnnotationRetention.SOURCE)
internal annotation class LinkifyMask