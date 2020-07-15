package io.storj;

import java.io.Serializable;
import java.util.Date;

/**
 * Represents object system metadata like content length or creation date.
 */
public class SystemMetadata implements Serializable, Comparable<SystemMetadata> {

    private Date created;
    private Date expires;
    private long contentLength;

    SystemMetadata(JNAUplink.SystemMetadata systemMetadata) {
        this.created = new Date(systemMetadata.created * 1000);
        this.expires = new Date(systemMetadata.expires * 1000);
        this.contentLength = systemMetadata.content_length;
    }

    /**
     * Returns object creation date.
     *
     * @return the creation date
     */
    public Date getCreated() {
        return created;
    }

    /**
     * Returns when object will expire.
     *
     * @return the expiration date
     */
    public Date getExpires() {
        return expires;
    }

    /**
     * Returns object content length.
     *
     * @return the content length
     */
    public long getContentLength() {
        return contentLength;
    }


    /**
     * Two {@link SystemMetadata} objects are equal.
     *
     * @return <code>true</code> if this object is the same as the specified object;
     * <code>false</code> otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SystemMetadata that = (SystemMetadata) o;
        // TODO add created when info() after commit will work ok
        return contentLength == that.contentLength
                && java.util.Objects.equals(expires, that.expires);
//                java.util.Objects.equals(created, that.created)
    }


    @Override
    public int compareTo(SystemMetadata other) {
//        int result = getCreated().compareTo(other.getCreated());
//        if (result != 0) {
//            return result;
//        }
        int result = getExpires().compareTo(other.getExpires());
        if (result != 0) {
            return result;
        }

        result = Long.compare(getContentLength(), other.getContentLength());
        if (result != 0) {
            return result;
        }
        return 0;
    }

    @Override
    public String toString() {
        return "SystemMetadata{" +
                "created=" + created +
                ", expires=" + expires +
                ", contentLength=" + contentLength +
                '}';
    }
}