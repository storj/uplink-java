package io.storj;

public class EncryptionKey {

    private String passphrase;
    private byte[] salt;

    public EncryptionKey(String passphrase, byte[] salt) {
        this.passphrase = passphrase;
        this.salt = salt;
    }

    String getPassphrase() {
        return passphrase;
    }

    byte[] getSalt() {
        return salt;
    }
}
