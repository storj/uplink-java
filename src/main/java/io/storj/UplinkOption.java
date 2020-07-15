package io.storj;

/**
 * Options for configuring {@link JNAUplink}.
 */
public class UplinkOption {

    private enum Key {
        TEMP_DIR,
        USER_AGENT,
        DIAL_TIMEOUT,
    }

    private Key key;

    private Object value;

    UplinkOption(Key key, Object value) {
        this.key = key;
        this.value = value;
    }

    /**
     * Option for temp directory to be used during upload. If not set, OS temp directory will be
     * used.
     *
     * @param tempDir path to temp directory
     * @return an {@link UplinkOption}
     */
    public static UplinkOption tempDir(String tempDir) {
        return new UplinkOption(Key.TEMP_DIR, tempDir);
    }

    /**
     * Option for user agent string.
     *
     * @param userAgent user agent string
     * @return an {@link UplinkOption}
     */
    public static UplinkOption userAgent(String userAgent) {
        return new UplinkOption(Key.USER_AGENT, userAgent);
    }

    /**
     * Option for dial timeout which defines how long client should wait for establishing
     * connection to peers.
     *
     * @param dialTimeout dial timeout in milliseconds
     * @return an {@link UplinkOption}
     */
    public static UplinkOption dialTimeout(int dialTimeout) {
        return new UplinkOption(Key.DIAL_TIMEOUT, dialTimeout);
    }

    static JNAUplink.Config.ByValue internal(UplinkOption... options) {
        JNAUplink.Config.ByValue config = new JNAUplink.Config.ByValue();

        for (UplinkOption option : options) {
            if (option.key == Key.TEMP_DIR) {
                config.temp_directory = option.value.toString();
            } else if (option.key == Key.USER_AGENT) {
                config.user_agent = option.value.toString();
            } else if (option.key == Key.DIAL_TIMEOUT) {
                config.dial_timeout_milliseconds = (Integer) option.value;
            }
        }
        return config;
    }

}