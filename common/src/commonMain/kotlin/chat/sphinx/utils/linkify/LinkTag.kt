package chat.sphinx.utils.linkify

enum class LinkTag {
    WebURL,
    LightningNodePublicKey,
    VirtualNodePublicKey,
    JoinTribeLink,
    Email,
    BitcoinAddress,
    LinkPreview,
    CopyableLink,
    Mention,
    // TODO: Bitcoin address/txid
}