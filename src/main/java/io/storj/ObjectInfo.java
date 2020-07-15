package io.storj;

import com.sun.jna.Structure;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Represents object metadata.
 */
public class ObjectInfo implements Serializable, Comparable<ObjectInfo> {

    private String key;
    private boolean isPrefix;
    private SystemMetadata system;
    private Map<String, String> custom = new HashMap<>();

    ObjectInfo(JNAUplink.Object object) {
        this.key = object.key;
        this.isPrefix = object.is_prefix == 1;
        this.system = new SystemMetadata(object.system);

        if (object.custom.count.longValue() > 0) {
            Structure[] entries = object.custom.entries.toArray(object.custom.count.intValue());
            for (int i = 0; i < entries.length; i++) {
                JNAUplink.CustomMetadataEntry entry = (JNAUplink.CustomMetadataEntry) entries[i];
                byte[] keyBytes = entry.key.getByteArray(0, entry.key_length.intValue());
                byte[] valueBytes = entry.value.getByteArray(0, entry.value_length.intValue());

                custom.put(new String(keyBytes, StandardCharsets.UTF_8), new String(valueBytes, StandardCharsets.UTF_8));
            }
        }
    }

    /**
     * Returns the key to the object relative to the bucket.
     *
     * @return the object key
     */
    public String getKey() {
        return key;
    }

    /**
     * Returns if this is a prefix instead of an object.
     *
     * <p>This is useful in non-recursive object listings.</p>
     *
     * @return <code>true</code> if this represents a prefix, or <code>false</code> if this
     * represents an object
     */
    public boolean isPrefix() {
        return isPrefix;
    }

    /**
     * Returns objects system metadata e.g. content length.
     *
     * @return the system metadata
     */
    public SystemMetadata getSystemMetadata() {
        return system;
    }

    /**
     * Returns objects custom metadata.
     *
     * @return the custom metadata as a map
     */
    public Map<String, String> getCustomMetadata() {
        return custom;
    }

    /**
     * Two {@link ObjectInfo} objects are equal if their names are equal.
     *
     * @return <code>true</code> if this object is the same as the specified object;
     * <code>false</code> otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ObjectInfo that = (ObjectInfo) o;
        return Objects.equals(key, that.key)
                && isPrefix == that.isPrefix
                && Objects.equals(system, that.system)
                && Objects.equals(custom, that.custom);
    }

    /**
     * The hash code value of {@link ObjectInfo} is the hash code value of its name.
     *
     * @return a hash code value for this object
     */
    @Override
    public int hashCode() {
        return Objects.hash(key);
    }

    /**
     * Two {@link ObjectInfo} objects are compared to each other by their prefix flag, bucket,
     * path and version, in this order.
     *
     * @return a negative integer, zero, or a positive integer as this object is less than,
     * equal to, or greater than the specified object.
     */
    @Override
    public int compareTo(ObjectInfo other) {
        int result = Boolean.compare(isPrefix(), other.isPrefix());
        if (result != 0) {
            return result;
        }

        result = getKey().compareTo(other.getKey());
        if (result != 0) {
            return result;
        }


        result = getSystemMetadata().compareTo(other.getSystemMetadata());
        if (result != 0) {
            return result;
        }

        if (!getCustomMetadata().equals(other.getCustomMetadata())) {
            return -1;
        }
        return 0;
    }

    @Override
    public String toString() {
        return "ObjectInfo{" +
                "key='" + key + '\'' +
                ", isPrefix=" + isPrefix +
                ", system=" + system +
                ", custom=" + custom +
                '}';
    }
}
