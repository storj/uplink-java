package io.storj;

class ExceptionUtil {

    static void handleError(JNAUplink.Error.ByReference error) throws StorjException {
        if (error != null) {
            String message = "" + error.code;
            if (error.message != null) {
                message = String.valueOf(error.message);
            }
            throw new StorjException(message);
        }
    }

}
