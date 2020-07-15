package io.storj;

import java.util.Date;

/**
 * Represents a set of permission to apply to {@link Access}.
 */
public class Permission {

    private JNAUplink.Permission.ByValue cPermission;

    Permission(Builder builder) {
        this.cPermission = new JNAUplink.Permission.ByValue();
        this.cPermission.allow_upload = (byte) (builder.allowUpload ? 1 : 0);
        this.cPermission.allow_download = (byte) (builder.allowDownload ? 1 : 0);
        this.cPermission.allow_list = (byte) (builder.allowList ? 1 : 0);
        this.cPermission.allow_delete = (byte) (builder.allowDelete ? 1 : 0);
        if (builder.notAfter != null) {
            this.cPermission.not_after = builder.notAfter.getTime() / 1000;
        }
        if (builder.notBefore != null) {
            this.cPermission.not_before = builder.notBefore.getTime() / 1000;
        }
    }

    JNAUplink.Permission.ByValue internal() {
        return this.cPermission;
    }

    /**
     * Builder for {@link Permission} objects.
     */
    public static class Builder {

        private boolean allowDownload;
        private boolean allowUpload;
        private boolean allowList;
        private boolean allowDelete;
        private Date notAfter;
        private Date notBefore;

        /**
         * AllowDownload gives permission to download the object's content. It
         * allows getting object metadata, but it does not allow listing buckets.
         *
         * @return a reference to this object
         */
        public Builder allowDownload() {
            this.allowDownload = true;
            return this;
        }

        /**
         * AllowUpload gives permission to create buckets and upload new objects.
         * It does not allow overwriting existing objects unless AllowDelete is
         * granted too.
         *
         * @return a reference to this object
         */
        public Builder allowUpload() {
            this.allowUpload = true;
            return this;
        }

        /**
         * AllowList gives permission to list buckets. It allows getting object
         * metadata, but it does not allow downloading the object's content.
         *
         * @return a reference to this object
         */
        public Builder allowList() {
            this.allowList = true;
            return this;
        }

        /**
         * AllowDelete gives permission to delete buckets and objects. Unless
         * either AllowDownload or AllowList is granted too, no object metadata and
         * no error info will be returned for deleted objects.
         *
         * @return a reference to this object
         */
        public Builder allowDelete() {
            this.allowDelete = true;
            return this;
        }

        /**
         * NotAfter restricts when the resulting access grant is valid for.
         * If set, the resulting access grant will not work if the Satellite
         * believes the time is after NotAfter.
         * If set, this value should always be after NotBefore.
         *
         * @param notAfter a {@link Date}
         * @return a reference to this object
         */
        public Builder notAfter(Date notAfter) {
            this.notAfter = notAfter;
            return this;
        }

        /**
         * NotBefore restricts when the resulting access grant is valid for.
         * If set, the resulting access grant will not work if the Satellite
         * believes the time is before NotBefore.
         * If set, this value should always be before NotAfter.
         *
         * @param notBefore a {@link Date}
         * @return a reference to this object
         */
        public Builder notBefore(Date notBefore) {
            this.notBefore = notBefore;
            return this;
        }


        /**
         * Creates the new {@link Permission} object from this builder.
         *
         * @return a {@link Permission}
         */
        public Permission build() {
            return new Permission(this);
        }
    }
}
