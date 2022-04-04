package io.storj;

/**
 * Represents the main entrypoint to the Storj network.
 */
public class Uplink {

    private UplinkOption[] options;

    public Uplink(UplinkOption... options) {
        this.options = options;
    }

    /**
     * Returns a {@link Project} handle for the given {@link Access}.
     *
     * @param access an access grant, see {@link Access}
     * @return a {@link Project} handle
     * @throws StorjException in case of error
     */
    public Project openProject(Access access) throws StorjException {
        String serializedAccess = "";
        if (access != null) {
            serializedAccess = access.serializedAccess;
        }

        JNAUplink.ProjectResult.ByValue result = null;
        JNAUplink.AccessResult.ByValue internalAccess = null;
        try {
            internalAccess = JNAUplink.INSTANCE.uplink_parse_access(serializedAccess);
            ExceptionUtil.handleError(internalAccess.error);

            if (options.length == 0) {
                result = JNAUplink.INSTANCE.uplink_open_project(internalAccess.access);
            } else {
                JNAUplink.Config.ByValue config = UplinkOption.internal(options);
                result = JNAUplink.INSTANCE.uplink_config_open_project(config, internalAccess.access);
            }
            ExceptionUtil.handleError(result.error);
        } finally {
            JNAUplink.INSTANCE.uplink_free_access_result(internalAccess);
        }
        return new Project(result.project);
    }

    /**
     * Generates a new access grant using a passhprase.
     * It must talk to the Satellite provided to get a project-based salt for
     * deterministic key derivation.
     * <p>
     * Note: this is a CPU-heavy function that uses a password-based key derivation function
     * (Argon2). This should be a setup-only step. Most common interactions with the library
     * should be using a serialized access grant through ParseAccess directly.
     *
     * @param satelliteAddress the satellite address
     * @param apiKey           the api key
     * @param passphrase       the passphrase
     * @return the access grant
     * @throws StorjException in case of error
     */
    public Access requestAccessWithPassphrase(String satelliteAddress, String apiKey, String passphrase) throws StorjException {
        JNAUplink.AccessResult.ByValue result = null;

        if (options.length == 0) {
            result = JNAUplink.INSTANCE.uplink_request_access_with_passphrase(satelliteAddress, apiKey, passphrase);
        } else {
            JNAUplink.Config.ByValue config = UplinkOption.internal(options);
            result = JNAUplink.INSTANCE.uplink_config_request_access_with_passphrase(config, satelliteAddress, apiKey, passphrase);
        }
        ExceptionUtil.handleError(result.error);

        JNAUplink.StringResult.ByValue stringResult = JNAUplink.INSTANCE.uplink_access_serialize(result.access);
        try {
            ExceptionUtil.handleError(stringResult.error);

            return new Access(stringResult.string);
        } finally {
            JNAUplink.INSTANCE.uplink_free_access_result(result);
            JNAUplink.INSTANCE.uplink_free_string_result(stringResult);
        }
    }

}