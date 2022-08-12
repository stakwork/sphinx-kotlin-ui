package chat.sphinx.utils.linkify

enum class LinkTag {
    WebURL,
    LightningNodePublicKey,
    VirtualNodePublicKey,
    JoinTribeLink,
    Email,
    BitcoinAddress,
    LinkPreview,
    CopyableLink
    // TODO: Bitcoin address/txid
}