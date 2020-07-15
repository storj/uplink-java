package io.storj;

/**
 * Options for listing object.
 *
 * @see Project#listObjects(String, ObjectListOption...)
 */
public class ObjectListOption {

    private enum Key {
        PREFIX,
        CURSOR,
        RECURSIVE,
        SYSTEM,
        CUSTOM,
    }

    private Key key;

    private Object value;

    ObjectListOption(Key key, Object value) {
        this.key = key;
        this.value = value;
    }

    /**
     * Option for the starting cursor in the object listing. The name of the first object in the
     * result listing will be <b>after</b> the cursor.
     *
     * @param cursor a {@link String} with starting cursor
     * @return a {@link ObjectListOption}
     */
    public static ObjectListOption cursor(String cursor) {
        return new ObjectListOption(Key.CURSOR, cursor);
    }

    /**
     * Option for the path prefix to filter the listing results.
     *
     * @param prefix a {@link String} with path prefix
     * @return a {@link ObjectListOption}
     */
    public static ObjectListOption prefix(String prefix) {
        return new ObjectListOption(Key.PREFIX, prefix);
    }

    /**
     * Option for recursive listing.
     *
     * @return a {@link ObjectListOption}
     */
    public static ObjectListOption recursive() {
        return new ObjectListOption(Key.RECURSIVE, true);
    }

    /**
     * Option for returning system metadata with object while listing.
     *
     * @return a {@link ObjectListOption}
     */
    public static ObjectListOption system() {
        return new ObjectListOption(Key.SYSTEM, true);
    }

    /**
     * Option for returning custom metadata with object while listing.
     *
     * @return a {@link ObjectListOption}
     */
    public static ObjectListOption custom() {
        return new ObjectListOption(Key.CUSTOM, true);
    }

    static JNAUplink.ListObjectsOptions.ByReference internal(ObjectListOption... options) {
        if (options.length == 0) {
            return null;
        }

        JNAUplink.ListObjectsOptions.ByReference listOptions = new JNAUplink.ListObjectsOptions.ByReference();
        for (ObjectListOption option : options) {
            if (option.key == Key.CURSOR) {
                listOptions.cursor = option.value.toString();
            } else if (option.key == Key.PREFIX) {
                listOptions.prefix = option.value.toString();
            } else if (option.key == Key.RECURSIVE) {
                listOptions.recursive = (byte) ((boolean) option.value ? 1 : 0);
            } else if (option.key == Key.SYSTEM) {
                listOptions.system = (byte) ((boolean) option.value ? 1 : 0);
            } else if (option.key == Key.CUSTOM) {
                listOptions.custom = (byte) ((boolean) option.value ? 1 : 0);
            }
        }

        return listOptions;
    }

}
