package chat.sphinx.utils.linkify

import chat.sphinx.wrapper.lightning.LightningNodePubKey
import chat.sphinx.wrapper.lightning.VirtualLightningNodeAddress
import chat.sphinx.wrapper.tribe.TribeJoinLink
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern


/**
 * LinkifyCompat brings in `Linkify` improvements for URLs and email addresses to older API
 * levels.
 */
object SphinxLinkify {

    /**
     * Filters out web URL matches that occur after an at-sign (@).  This is
     * to prevent turning the domain name in an email address into a web link.
     */
    val sUrlMatchFilter: MatchFilter = object : MatchFilter {
        override fun acceptMatch(s: CharSequence, start: Int, end: Int): Boolean {
            if (start == 0) {
                return true
            }
            return s[start - 1] != '@'
        }
    }

    /**
     * Bit field indicating that web URLs should be matched in methods that
     * take an options mask
     */
    const val WEB_URLS = 0x01

    /**
     * Bit field indicating that email addresses should be matched in methods
     * that take an options mask
     */
    const val EMAIL_ADDRESSES: Int = 0x02

    /**
     * Bit field indicating that phone numbers should be matched in methods that
     * take an options mask
     */
    const val PHONE_NUMBERS: Int = 0x04

    /**
     * Bit field indicating that [LightningNodePubKey] should be matched in methods that
     * take an options mask
     */
    const val LIGHTNING_NODE_PUBLIC_KEY: Int = 0x08

    /**
     * Bit field indicating that [VirtualLightningNodeAddress] should be matched in methods that
     * take an options mask
     */
    const val VIRTUAL_NODE_ADDRESS: Int = 0x10

    /**
     * Bit field indicating that a Bitcoin address should be matched in methods that
     * take an options mask
     */
    const val BITCOIN_ADDRESS: Int = 0x20

    /**
     * Bit field indicating that a [TribeJoinLink] should be matched in methods that
     * take an options mask
     */
    const val TRIBE_LINK: Int = 0x40

    /**
     * Bit field indicating that a [Mention] should be matched in methods that
     * take an options mask
     */
    const val MENTION: Int = 0x80


    /**
     * Bit mask indicating that all available patterns should be matched in
     * methods that take an options mask
     *
     * **Note:** [.MAP_ADDRESSES] is deprecated.
     * Use [android.view.textclassifier.TextClassifier.generateLinks]
     * instead and avoid it even when targeting API levels where no alternative is available.
     */
    const val ALL: Int = WEB_URLS or EMAIL_ADDRESSES or PHONE_NUMBERS or LIGHTNING_NODE_PUBLIC_KEY or VIRTUAL_NODE_ADDRESS or BITCOIN_ADDRESS or TRIBE_LINK or MENTION
    const val LINKS_WITH_PREVIEWS: Int = WEB_URLS or LIGHTNING_NODE_PUBLIC_KEY or VIRTUAL_NODE_ADDRESS or TRIBE_LINK

    private val COMPARATOR: Comparator<LinkSpec> = object : Comparator<LinkSpec> {
        override fun compare(a: LinkSpec, b: LinkSpec): Int {
            if (a.start < b.start) {
                return -1
            }
            if (a.start > b.start) {
                return 1
            }
            if (a.end < b.end) {
                return 1
            }
            return if (a.end > b.end) {
                -1
            } else 0
        }
    }

    /**
     * Scans the text of the provided Spannable and turns all occurrences
     * of the link types indicated in the mask into clickable links.
     * If the mask is nonzero, it also removes any existing URLSpans
     * attached to the Spannable, to avoid problems if you call it
     * repeatedly on the same text.
     *
     * @param text Spannable whose text is to be marked-up with links
     * @param mask Mask to define which kinds of links will be searched.
     *
     * @return True if at least one link is found and applied.
     */
    fun gatherLinks(
        text: String,
        @LinkifyMask mask: Int
    ): List<LinkSpec> {
        if (mask == 0) {
            return emptyList()
        }
        val links = ArrayList<LinkSpec>()
        if (mask and LIGHTNING_NODE_PUBLIC_KEY != 0) {
            gatherLinks(
                links, text, SphinxPatterns.LIGHTNING_NODE_PUBLIC_KEY, arrayOf(),
                null
            )
        }
        if (mask and VIRTUAL_NODE_ADDRESS != 0) {
            gatherLinks(
                links, text, SphinxPatterns.VIRTUAL_NODE_ADDRESS, arrayOf(),
                null,
            )
        }
        if (mask and BITCOIN_ADDRESS != 0) {
            gatherLinks(
                links, text, SphinxPatterns.BITCOIN_BIP21_URI, arrayOf("bitcoin:"),
                null,
            )
        }
        if (mask and TRIBE_LINK != 0) {
            gatherLinks(
                links, text, SphinxPatterns.JOIN_TRIBE_LINK, arrayOf(),
                null,
            )
        }
        if (mask and MENTION != 0) {
            gatherLinks(
                links, text, SphinxPatterns.MENTION, arrayOf(),
                null,
            )
        }
        if (mask and WEB_URLS != 0) {
            gatherLinks(
                links,
                text,
                SphinxPatterns.AUTOLINK_WEB_URL,
                arrayOf("http://", "https://", "rtsp://", "sphinx.chat://"),
                sUrlMatchFilter,
            )
        }
        if (mask and EMAIL_ADDRESSES != 0) {
            gatherLinks(
                links, text, PatternsCompat.AUTOLINK_EMAIL_ADDRESS, arrayOf("mailto:"),
                null,
            )
        }
        pruneOverlaps(links)
        return links
    }

    private fun makeUrl(
        url: String, prefixes: Array<String?>,
    ): String {
        var localUrl = url
        var hasPrefix = false
        for (i in prefixes.indices) {
            if (localUrl.regionMatches(0, prefixes[i]!!, 0, prefixes[i]!!.length, ignoreCase = true)) {
                hasPrefix = true

                // Fix capitalization if necessary
                if (!localUrl.regionMatches(
                        0,
                        prefixes[i]!!, 0, prefixes[i]!!.length, ignoreCase = false
                    )
                ) {
                    localUrl = prefixes[i].toString() + localUrl.substring(prefixes[i]!!.length)
                }
                break
            }
        }
        if (!hasPrefix && prefixes.size > 0) {
            localUrl = prefixes[0].toString() + localUrl
        }
        return localUrl
    }

    private fun gatherLinks(
        links: ArrayList<LinkSpec>,
        s: String,
        pattern: Pattern,
        schemes: Array<String?>,
        matchFilter: MatchFilter?
    ) {
        val m = pattern.matcher(s)
        while (m.find()) {
            val start = m.start()
            val end = m.end()
            if (matchFilter == null || matchFilter.acceptMatch(s, start, end)) {
                val url = makeUrl(m.group(0), schemes)

                val spec = LinkSpec(
                    tag = SphinxPatterns.getTag(pattern).name,
                    url = url,
                    start = start,
                    end = end
                )

                links.add(spec)
            }
        }
    }

    private fun pruneOverlaps(links: ArrayList<LinkSpec>) {
        Collections.sort(links, COMPARATOR)
        var len = links.size
        var i = 0
        while (i < len - 1) {
            val a = links[i]
            val b = links[i + 1]
            var remove = -1
            if (a.start <= b.start && a.end > b.start) {
                if (b.end <= a.end) {
                    remove = i + 1
                } else if (a.end - a.start > b.end - b.start) {
                    remove = i + 1
                } else if (a.end - a.start < b.end - b.start) {
                    remove = i
                }
                if (remove != -1) {
                    links.removeAt(remove)
                    len--
                    continue
                }
            }
            i++
        }
    }

    object SphinxPatterns {
        // TODO: Have the regex for a Bitcoin address and a Bitcoin txid
        val AUTOLINK_WEB_URL: Pattern = Pattern.compile(
            "(${PatternsCompat.AUTOLINK_WEB_URL.pattern()})"
        )

        val LIGHTNING_NODE_PUBLIC_KEY: Pattern = Pattern.compile(
            LightningNodePubKey.REGEX
        )

        val VIRTUAL_NODE_ADDRESS: Pattern = Pattern.compile(
            VirtualLightningNodeAddress.REGEX
        )

        val JOIN_TRIBE_LINK: Pattern = Pattern.compile(
            TribeJoinLink.REGEX
        )

        val MENTION: Pattern = Pattern.compile(
            "\\B@[^\\s]+"
        )

        val LINK_PREVIEWS: Pattern = Pattern.compile(
            "(" +
                    "${TribeJoinLink.REGEX}|" +
                    "${PatternsCompat.AUTOLINK_WEB_URL.pattern()}|" +
                    "${LightningNodePubKey.REGEX}|" +
                    "${VirtualLightningNodeAddress.REGEX}|" +
                    "${TribeJoinLink.REGEX})"
        )
            
        val COPYABLE_LINKS: Pattern = Pattern.compile(
            "(${TribeJoinLink.REGEX}|${PatternsCompat.AUTOLINK_WEB_URL.pattern()}|${LightningNodePubKey.REGEX}|${VirtualLightningNodeAddress.REGEX}|${TribeJoinLink.REGEX})"
        )


        private val BITCOIN_ADDRESS: Pattern = Pattern.compile(
            "(bc1|[13])[a-zA-HJ-NP-Z0-9]{25,39}"
        )

        val BITCOIN_BIP21_URI: Pattern = Pattern.compile(
            "${BITCOIN_ADDRESS.toRegex().pattern}|bitcoin:${BITCOIN_ADDRESS.toRegex().pattern}" // TODO: Make this BIP21 fully complaint
        )

        fun getTag(pattern: Pattern): LinkTag {
            return when(pattern) {
                AUTOLINK_WEB_URL -> {
                    LinkTag.WebURL
                }
                LIGHTNING_NODE_PUBLIC_KEY -> {
                    LinkTag.LightningNodePublicKey
                }
                VIRTUAL_NODE_ADDRESS -> {
                    LinkTag.VirtualNodePublicKey
                }
                JOIN_TRIBE_LINK -> {
                    LinkTag.JoinTribeLink
                }
                PatternsCompat.AUTOLINK_EMAIL_ADDRESS -> {
                    LinkTag.Email
                }
                BITCOIN_ADDRESS -> {
                    LinkTag.BitcoinAddress
                }
                MENTION -> {
                    LinkTag.Mention
                }
                else -> LinkTag.WebURL
            }

        }
    }


    /**
     * MatchFilter enables client code to have more control over
     * what is allowed to match and become a link, and what is not.
     *
     * For example:  when matching web URLs you would like things like
     * http://www.example.com to match, as well as just example.com itelf.
     * However, you would not want to match against the domain in
     * support@example.com.  So, when matching against a web URL pattern you
     * might also include a MatchFilter that disallows the match if it is
     * immediately preceded by an at-sign (@).
     */
    interface MatchFilter {
        /**
         * Examines the character span matched by the pattern and determines
         * if the match should be turned into an actionable link.
         *
         * @param s        The body of text against which the pattern
         * was matched
         * @param start    The index of the first character in s that was
         * matched by the pattern - inclusive
         * @param end      The index of the last character in s that was
         * matched - exclusive
         *
         * @return         Whether this match should be turned into a link
         */
        fun acceptMatch(s: CharSequence, start: Int, end: Int): Boolean
    }
}