package io.storj;

/**
 * Options for downloading object.
 *
 * @see Project#downloadObject(String, String, ObjectDownloadOption...)
 */
public class ObjectDownloadOption {

    private enum Key {
        OFFSET,
        LENGTH,
    }

    private Key key;

    private Object value;

    ObjectDownloadOption(Key key, Object value) {
        this.key = key;
        this.value = value;
    }

    /**
     * Option for the starting offset of download in bytes.
     *
     * @param offset the offset
     * @return a {@link ObjectUploadOption}
     */
    public static ObjectDownloadOption offset(long offset) {
        return new ObjectDownloadOption(Key.OFFSET, offset);
    }

    /**
     * Option for the total bytes to download in bytes.
     *
     * @param length the length
     * @return a {@link ObjectUploadOption}
     */
    public static ObjectDownloadOption length(long length) {
        return new ObjectDownloadOption(Key.LENGTH, length);
    }

    static JNAUplink.DownloadOptions internal(ObjectDownloadOption... options) {
        if (options.length == 0) {
            return null;
        }

        JNAUplink.DownloadOptions downloadOptions = new JNAUplink.DownloadOptions();
        for (ObjectDownloadOption option : options) {
            if (option.key == Key.OFFSET) {
                downloadOptions.offset = (long) option.value;
            } else if (option.key == Key.LENGTH) {
                downloadOptions.length = (long) option.value;
            }
        }

        return downloadOptions;
    }
}
