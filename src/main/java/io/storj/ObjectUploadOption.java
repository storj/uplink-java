package io.storj;

import java.util.Date;

/**
 * Options for uploading object.
 *
 * @see Project#uploadObject(String, String, ObjectUploadOption...)
 */
public class ObjectUploadOption {

    private enum Key {
        EXPIRES,
    }

    private Key key;

    private Object value;

    ObjectUploadOption(Key key, Object value) {
        this.key = key;
        this.value = value;
    }

    /**
     * Option for the expiration date of the new object. If not set, the object will never expire
     * and will persist on the network until deleted explicitly.
     *
     * @param expires the expiration {@link Date}
     * @return a {@link ObjectUploadOption}
     */
    public static ObjectUploadOption expires(Date expires) {
        return new ObjectUploadOption(Key.EXPIRES, expires);
    }

    static JNAUplink.UploadOptions.ByReference internal(ObjectUploadOption... options) {
        if (options.length == 0) {
            return null;
        }

        JNAUplink.UploadOptions.ByReference uploadOptions = new JNAUplink.UploadOptions.ByReference();
        for (ObjectUploadOption option : options) {
            if (option.key == Key.EXPIRES) {
                uploadOptions.expires = ((Date) option.value).getTime() / 1000;
            }
        }

        return uploadOptions;
    }
}
