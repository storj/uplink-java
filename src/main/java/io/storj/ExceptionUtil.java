package io.storj;

class ExceptionUtil {

    static void handleError(JNAUplink.Error.ByReference error) throws StorjException {
        if (error != null) {
            String message = "" + error.code;
            if (error.message != null) {
                message = error.message;
                JNAUplink.INSTANCE.uplink_free_error(error);
            }
            throw new StorjException(message);
        }
    }

}
