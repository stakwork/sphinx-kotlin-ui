package chat.sphinx.utils.linkify

enum class LinkTag {
    WebURL,
    LightningNodePublicKey,
    VirtualNodePublicKey,
    Email,
    LinkPreview,
    CopyableLink,
    BitcoinAddress
    // TODO: Bitcoin address/txid
}