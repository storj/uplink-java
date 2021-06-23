package io.storj;

import com.sun.jna.Memory;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;

/**
 * Represents all credentials you need to access data on the Storj network:
 * <ul>
 *     <li>Satellite address in the form of "host:port"</li>
 *     <li>ApiKey to access the satellite</li>
 *     <li>Encryption access for accessing the encrypted content</li>
 * </ul>
 */
public class Access {

    protected String serializedAccess;

    Access(String serializedAccess) {
        this.serializedAccess = serializedAccess;
    }

    /**
     * Serializes this {@link Access} to base58-encoded {@link String}.
     *
     * @return a {@link String} with serialized Access Grant
     * @throws StorjException in case of error
     */
    public String serialize() throws StorjException {
        return this.serializedAccess;
    }

    /**
     * Parses a base58-encoded {@link String} to an {@link Access}.
     *
     * @param serialized a base58-encoded {@link String}
     * @return the parsed {@link Access} Grant
     * @throws StorjException in case of error
     */
    public static Access parse(String serialized) throws StorjException {
        JNAUplink.AccessResult.ByValue result = JNAUplink.INSTANCE.uplink_parse_access(serialized);
        ExceptionUtil.handleError(result.error);
        JNAUplink.INSTANCE.uplink_free_access_result(result);
        return new Access(serialized);
    }

    /**
     * Creates a new access grant with specific permissions.
     * <p>
     * Access grants can only have their existing permissions restricted,
     * and the resulting access grant will only allow for the intersection of all previous
     * Share calls in the access grant construction chain.
     * <p>
     * Prefixes, if provided, restrict the access grant (and internal encryption information)
     * to only contain enough information to allow access to just those prefixes.
     *
     * @param permission the permission
     * @param prefixes   list of prefixes to restrict access
     * @return shared access grant
     * @throws StorjException if an error occurs during the sharing
     */
    public Access share(Permission permission, SharePrefix... prefixes) throws StorjException {
        JNAUplink.Permission.ByValue cPermission = permission.internal();
        JNAUplink.AccessResult.ByValue accessResult = null;
        JNAUplink.AccessResult.ByValue shareResult = null;
        JNAUplink.StringResult.ByValue stringResult = null;

        JNAUplink.SharePrefix.ByReference firstPrefix = new JNAUplink.SharePrefix.ByReference();
        if (prefixes.length > 0) {
            JNAUplink.SharePrefix.ByReference[] cPrefixes = (JNAUplink.SharePrefix.ByReference[]) firstPrefix.toArray(prefixes.length);
            for (int i = 0; i < prefixes.length; i++) {
                cPrefixes[i].bucket = prefixes[i].getBucket();
                cPrefixes[i].prefix = prefixes[i].getPrefix();
            }
            firstPrefix = cPrefixes[0];
        }

        try {
            accessResult = JNAUplink.INSTANCE.uplink_parse_access(this.serializedAccess);
            ExceptionUtil.handleError(accessResult.error);

            shareResult = JNAUplink.INSTANCE.uplink_access_share(accessResult.access, cPermission, firstPrefix, prefixes.length);
            ExceptionUtil.handleError(shareResult.error);

            stringResult = JNAUplink.INSTANCE.uplink_access_serialize(shareResult.access);
            ExceptionUtil.handleError(stringResult.error);

            return new Access(stringResult.string);
        } finally {
            if (accessResult != null) {
                JNAUplink.INSTANCE.uplink_free_access_result(accessResult);
            }
            if (shareResult != null) {
                JNAUplink.INSTANCE.uplink_free_access_result(shareResult);
            }
            if (stringResult != null) {
                JNAUplink.INSTANCE.uplink_free_string_result(stringResult);
            }
        }
    }

    /**
     * Overrides the root encryption key for the prefix in
     * bucket with encryptionKey.
     * <p>
     * This function is useful for overriding the encryption key in user-specific
     * access grants when implementing multitenancy in a single app bucket.
     *
     * @param bucket        the bucket name
     * @param prefix        the prefix
     * @param encryptionKey new encryption key
     * @throws StorjException in case of error
     */
    public void overrideEncryptionKey(String bucket, String prefix, EncryptionKey encryptionKey) throws StorjException {
        JNAUplink.AccessResult.ByValue accessResult = null;
        JNAUplink.EncryptionKeyResult.ByValue encKeyResult = null;
        try {
            accessResult = JNAUplink.INSTANCE.uplink_parse_access(this.serializedAccess);
            ExceptionUtil.handleError(accessResult.error);

            Pointer salt = new Memory(encryptionKey.getSalt().length);
            salt.write(0, encryptionKey.getSalt(), 0, encryptionKey.getSalt().length);
            encKeyResult = JNAUplink.INSTANCE.uplink_derive_encryption_key(encryptionKey.getPassphrase(), salt, new NativeLong(encryptionKey.getSalt().length));
            ExceptionUtil.handleError(encKeyResult.error);

            JNAUplink.Error.ByReference error = JNAUplink.INSTANCE.uplink_access_override_encryption_key(accessResult.access, bucket, prefix, encKeyResult.encryption_key);
            ExceptionUtil.handleError(error);
        } finally {
            if (accessResult != null) {
                JNAUplink.INSTANCE.uplink_free_access_result(accessResult);
            }
            if (encKeyResult != null) {
                JNAUplink.INSTANCE.uplink_free_encryption_key_result(encKeyResult);
            }
        }
    }
}
