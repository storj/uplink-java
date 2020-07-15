package io.storj;

/**
 * Represents a share based on a bucket and a prefix.
 *
 * @see Access#share(Permission, SharePrefix...)
 */
public class SharePrefix {

    private String bucket;
    private String prefix;

    public SharePrefix(String bucket) {
        this.bucket = bucket;
    }

    public SharePrefix(String bucket, String prefix) {
        this.bucket = bucket;
        this.prefix = prefix;
    }

    String getBucket() {
        return bucket;
    }

    String getPrefix() {
        return prefix;
    }
}
