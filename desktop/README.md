# Sphinx Desktop

## macOS

## Properties

The following values need to be specified in the local.properties

```properties
macOs.bundleID=chat.sphinx.sphinx-desktop
macOs.signing.identity=Apple Developer Account Name
# Optional keychain configuration
macOs.signing.keychain=/path/to/keychain/with/AppleDeveloperAccountName
```

The bundleID has to be created in it's associated `Apple Developer Account Name`.

### Notarization

TODO

### Signing

To distribute the app online we need to have a Developer ID Application certificate with codesigning functionality.

To check if you have this cert on your machine. Run the following command:

```bash
$ security find-identity -p codesigning -v
 1) DXXXXXXXXXXXXXXXXF1 "Developer ID Application: Apple Developer Account Name (XXXXXXXXX)"
     1 valid identities found
```

The `Apple Developer Account Name` will be used as the identity.
#### No CodeSigning Certificate

If you have no code signing ceritifactes (private keys) on your machine. You will see the below output.
```bash
$ security find-identity -p codesigning -v
0 valid identities found
```

##### Create Developer ID Application Certificate

```
/usr/bin/security find-certificate -c "Developer ID Application"
```