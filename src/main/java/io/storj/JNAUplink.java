package io.storj;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;

interface JNAUplink extends Library {

    public static final JNAUplink INSTANCE = Native.load("uplink", JNAUplink.class);

    public static final int EOF = -1;
    public static final int ERROR_UPLOAD_DONE = (int) 0x22;
    public static final int ERROR_INVALID_HANDLE = (int) 0x04;
    public static final int ERROR_OBJECT_NOT_FOUND = (int) 0x21;
    public static final int ERROR_CANCELED = (int) 0x03;
    public static final int ERROR_BUCKET_ALREADY_EXISTS = (int) 0x11;
    public static final int ERROR_BANDWIDTH_LIMIT_EXCEEDED = (int) 0x06;
    public static final int ERROR_BUCKET_NOT_FOUND = (int) 0x13;
    public static final int ERROR_OBJECT_KEY_INVALID = (int) 0x20;
    public static final int ERROR_TOO_MANY_REQUESTS = (int) 0x05;
    public static final int ERROR_BUCKET_NAME_INVALID = (int) 0x10;
    public static final int ERROR_BUCKET_NOT_EMPTY = (int) 0x12;
    public static final int ERROR_INTERNAL = (int) 0x02;

    @Structure.FieldOrder({"_handle"})
    public static class Handle extends Structure {
        public NativeLong _handle;

        public Handle() {
            super();
        }

        public Handle(NativeLong _handle) {
            super();
            this._handle = _handle;
        }

        public static class ByReference extends Handle implements Structure.ByReference {
        }

        public static class ByValue extends Handle implements Structure.ByValue {
        }
    }

    public static class Access extends Handle {
        public static class ByReference extends Access implements Structure.ByReference {
        }

        public static class ByValue extends Access implements Structure.ByValue {
        }
    }

    public static class Project extends Handle {
        public static class ByReference extends Project implements Structure.ByReference {
        }

        public static class ByValue extends Project implements Structure.ByValue {
        }
    }

    public static class EncryptionKey extends Handle {
        public static class ByReference extends EncryptionKey implements Structure.ByReference {
        }

        public static class ByValue extends EncryptionKey implements Structure.ByValue {
        }
    }

    @Structure.FieldOrder({"user_agent", "dial_timeout_milliseconds", "temp_directory"})
    public static class Config extends Structure {
        public String user_agent;
        public int dial_timeout_milliseconds;
        /**
         * temp_directory specifies where to save data during downloads to use less memory.
         */
        public String temp_directory;

        public Config() {
            super();
        }

        public Config(String user_agent, int dial_timeout_milliseconds, String temp_directory) {
            super();
            this.user_agent = user_agent;
            this.dial_timeout_milliseconds = dial_timeout_milliseconds;
            this.temp_directory = temp_directory;
        }

        public static class ByReference extends Config implements Structure.ByReference {
        }

        public static class ByValue extends Config implements Structure.ByValue {
        }
    }

    @Structure.FieldOrder({"name", "created"})
    public static class Bucket extends Structure {
        public String name;
        public long created;

        public Bucket() {
            super();
        }

        public Bucket(String name, long created) {
            super();
            this.name = name;
            this.created = created;
        }

        public static class ByReference extends Bucket implements Structure.ByReference {
        }

        public static class ByValue extends Bucket implements Structure.ByValue {
        }
    }

    @Structure.FieldOrder({"code", "message"})
    public static class Error extends Structure {
        public int code;
        public String message;

        public Error() {
            super();
        }

        public Error(int code, String message) {
            super();
            this.code = code;
            this.message = message;
        }

        public static class ByReference extends Error implements Structure.ByReference {
        }

        public static class ByValue extends Error implements Structure.ByValue {
        }
    }

    @Structure.FieldOrder({"access", "error"})
    public static class AccessResult extends Structure {
        public JNAUplink.Access.ByReference access;
        public Error.ByReference error;

        public AccessResult() {
            super();
        }

        public AccessResult(JNAUplink.Access.ByReference access, Error.ByReference error) {
            super();
            this.access = access;
            this.error = error;
        }

        public static class ByReference extends AccessResult implements Structure.ByReference {
        }

        public static class ByValue extends AccessResult implements Structure.ByValue {
        }
    }

    @Structure.FieldOrder({"project", "error"})
    public static class ProjectResult extends Structure {
        public JNAUplink.Project.ByReference project;
        public Error.ByReference error;

        public ProjectResult() {
            super();
        }

        public ProjectResult(JNAUplink.Project.ByReference project, Error.ByReference error) {
            super();
            this.project = project;
            this.error = error;
        }

        public static class ByReference extends ProjectResult implements Structure.ByReference {
        }

        public static class ByValue extends ProjectResult implements Structure.ByValue {
        }
    }

    @Structure.FieldOrder({"bucket", "error"})
    public static class BucketResult extends Structure {
        public Bucket.ByReference bucket;
        public Error.ByReference error;

        public BucketResult() {
            super();
        }

        public BucketResult(Bucket.ByReference bucket, Error.ByReference error) {
            super();
            this.bucket = bucket;
            this.error = error;
        }

        public static class ByReference extends BucketResult implements Structure.ByReference {
        }

        public static class ByValue extends BucketResult implements Structure.ByValue {
        }
    }

    @Structure.FieldOrder({"string", "error"})
    public static class StringResult extends Structure {
        public String string;
        public Error.ByReference error;

        public StringResult() {
            super();
        }

        public StringResult(String string, Error.ByReference error) {
            super();
            this.string = string;
            this.error = error;
        }

        public static class ByReference extends StringResult implements Structure.ByReference {
        }

        public static class ByValue extends StringResult implements Structure.ByValue {
        }
    }

    @Structure.FieldOrder({"allow_download", "allow_upload", "allow_list", "allow_delete", "not_before", "not_after"})
    public static class Permission extends Structure {
        public byte allow_download;
        public byte allow_upload;
        public byte allow_list;
        public byte allow_delete;
        /**
         * disabled when 0.
         */
        public long not_before;
        /**
         * disabled when 0.
         */
        public long not_after;

        public Permission() {
            super();
        }

        public Permission(byte allow_download, byte allow_upload, byte allow_list, byte allow_delete, long not_before, long not_after) {
            super();
            this.allow_download = allow_download;
            this.allow_upload = allow_upload;
            this.allow_list = allow_list;
            this.allow_delete = allow_delete;
            this.not_before = not_before;
            this.not_after = not_after;
        }

        public static class ByReference extends Permission implements Structure.ByReference {
        }

        public static class ByValue extends Permission implements Structure.ByValue {
        }
    }

    @Structure.FieldOrder({"bucket", "prefix"})
    public static class SharePrefix extends Structure {
        public String bucket;
        /**
         * prefix is the prefix of the shared object keys.
         */
        public String prefix;

        public SharePrefix() {
            super();
        }

        public SharePrefix(String bucket, String prefix) {
            super();
            this.bucket = bucket;
            this.prefix = prefix;
        }

        public static class ByReference extends SharePrefix implements Structure.ByReference {
        }

        public static class ByValue extends SharePrefix implements Structure.ByValue {
        }
    }

    @Structure.FieldOrder({"cursor"})
    public static class ListBucketsOptions extends Structure {
        public String cursor;

        public ListBucketsOptions() {
            super();
        }

        public ListBucketsOptions(String cursor) {
            super();
            this.cursor = cursor;
        }

        public static class ByReference extends ListBucketsOptions implements Structure.ByReference {
        }

        public static class ByValue extends ListBucketsOptions implements Structure.ByValue {
        }
    }

    public static class BucketIterator extends Handle {
        public static class ByReference extends BucketIterator implements Structure.ByReference {
        }

        public static class ByValue extends BucketIterator implements Structure.ByValue {
        }
    }

    public static class Download extends Handle {
        public static class ByReference extends Download implements Structure.ByReference {
        }

        public static class ByValue extends Download implements Structure.ByValue {
        }
    }

    public static class ObjectIterator extends Handle {
        public static class ByReference extends ObjectIterator implements Structure.ByReference {
        }

        public static class ByValue extends ObjectIterator implements Structure.ByValue {
        }
    }

    public static class Upload extends Handle {
        public static class ByReference extends Upload implements Structure.ByReference {
        }

        public static class ByValue extends Upload implements Structure.ByValue {
        }
    }

    @Structure.FieldOrder({"key", "is_prefix", "system", "custom"})
    public static class Object extends Structure {
        public String key;
        public byte is_prefix;
        public JNAUplink.SystemMetadata.ByValue system;
        public JNAUplink.CustomMetadata.ByValue custom;

        public Object() {
            super();
        }

        public Object(String key, byte is_prefix, JNAUplink.SystemMetadata.ByValue system, JNAUplink.CustomMetadata.ByValue custom) {
            super();
            this.key = key;
            this.is_prefix = is_prefix;
            this.system = system;
            this.custom = custom;
        }

        public static class ByReference extends Object implements Structure.ByReference {
        }

        public static class ByValue extends Object implements Structure.ByValue {
        }
    }

    @Structure.FieldOrder({"created", "expires", "content_length"})
    public static class SystemMetadata extends Structure {
        public long created;
        public long expires;
        public long content_length;

        public SystemMetadata() {
            super();
        }

        public SystemMetadata(long created, long expires, long content_length) {
            super();
            this.created = created;
            this.expires = expires;
            this.content_length = content_length;
        }

        public static class ByReference extends SystemMetadata implements Structure.ByReference {
        }

        public static class ByValue extends SystemMetadata implements Structure.ByValue {
        }
    }

    @Structure.FieldOrder({"key", "key_length", "value", "value_length"})
    public static class CustomMetadataEntry extends Structure {
        public Pointer key;
        public NativeLong key_length;
        public Pointer value;
        public NativeLong value_length;

        public CustomMetadataEntry() {
            super();
        }

        public CustomMetadataEntry(Pointer key, NativeLong key_length, Pointer value, NativeLong value_length) {
            super();
            this.key = key;
            this.key_length = key_length;
            this.value = value;
            this.value_length = value_length;
        }

        public static class ByReference extends CustomMetadataEntry implements Structure.ByReference {
        }

        public static class ByValue extends CustomMetadataEntry implements Structure.ByValue {
        }
    }

    @Structure.FieldOrder({"entries", "count"})
    public static class CustomMetadata extends Structure {
        public JNAUplink.CustomMetadataEntry.ByReference entries;
        public NativeLong count;

        public CustomMetadata() {
            super();
        }

        public CustomMetadata(JNAUplink.CustomMetadataEntry.ByReference entries, NativeLong count) {
            super();
            this.entries = entries;
            this.count = count;
        }

        public static class ByReference extends CustomMetadata implements Structure.ByReference {
        }

        public static class ByValue extends CustomMetadata implements Structure.ByValue {
        }
    }

    @Structure.FieldOrder({"expires"})
    public static class UploadOptions extends Structure {
        /**
         * When expires is 0 or negative, it means no expiration.
         */
        public long expires;

        public UploadOptions() {
            super();
        }

        public UploadOptions(long expires) {
            super();
            this.expires = expires;
        }

        public static class ByReference extends UploadOptions implements Structure.ByReference {
        }

        public static class ByValue extends UploadOptions implements Structure.ByValue {
        }
    }

    @Structure.FieldOrder({"offset", "length"})
    public static class DownloadOptions extends Structure {
        public long offset;
        /**
         * When length is negative, it will read until the end of the blob.
         */
        public long length;

        public DownloadOptions() {
            super();
        }

        public DownloadOptions(long offset, long length) {
            super();
            this.offset = offset;
            this.length = length;
        }

        public static class ByReference extends DownloadOptions implements Structure.ByReference {
        }

        public static class ByValue extends DownloadOptions implements Structure.ByValue {
        }
    }

    @Structure.FieldOrder({"prefix", "cursor", "recursive", "system", "custom"})
    public static class ListObjectsOptions extends Structure {
        public String prefix;
        public String cursor;
        public byte recursive;
        public byte system;
        public byte custom;

        public ListObjectsOptions() {
            super();
        }

        public ListObjectsOptions(String prefix, String cursor, byte recursive, byte system, byte custom) {
            super();
            this.prefix = prefix;
            this.cursor = cursor;
            this.recursive = recursive;
            this.system = system;
            this.custom = custom;
        }

        public static class ByReference extends ListObjectsOptions implements Structure.ByReference {
        }

        public static class ByValue extends ListObjectsOptions implements Structure.ByValue {
        }
    }

    @Structure.FieldOrder({"object", "error"})
    public static class ObjectResult extends Structure {
        public JNAUplink.Object.ByReference object;
        public Error.ByReference error;

        public ObjectResult() {
            super();
        }

        public ObjectResult(JNAUplink.Object.ByReference object, Error.ByReference error) {
            super();
            this.object = object;
            this.error = error;
        }

        public static class ByReference extends ObjectResult implements Structure.ByReference {
        }

        public static class ByValue extends ObjectResult implements Structure.ByValue {
        }
    }

    @Structure.FieldOrder({"upload", "error"})
    public static class UploadResult extends Structure {
        public JNAUplink.Upload.ByReference upload;
        public Error.ByReference error;

        public UploadResult() {
            super();
        }

        public UploadResult(JNAUplink.Upload.ByReference upload, Error.ByReference error) {
            super();
            this.upload = upload;
            this.error = error;
        }

        public static class ByReference extends UploadResult implements Structure.ByReference {
        }

        public static class ByValue extends UploadResult implements Structure.ByValue {
        }
    }

    @Structure.FieldOrder({"download", "error"})
    public static class DownloadResult extends Structure {
        public JNAUplink.Download.ByReference download;
        public Error.ByReference error;

        public DownloadResult() {
            super();
        }

        public DownloadResult(JNAUplink.Download.ByReference download, Error.ByReference error) {
            super();
            this.download = download;
            this.error = error;
        }

        public static class ByReference extends DownloadResult implements Structure.ByReference {
        }

        public static class ByValue extends DownloadResult implements Structure.ByValue {
        }
    }

    @Structure.FieldOrder({"bytes_written", "error"})
    public static class WriteResult extends Structure {
        public NativeLong bytes_written;
        public Error.ByReference error;

        public WriteResult() {
            super();
        }

        public WriteResult(NativeLong bytes_written, Error.ByReference error) {
            super();
            this.bytes_written = bytes_written;
            this.error = error;
        }

        public static class ByReference extends WriteResult implements Structure.ByReference {
        }

        public static class ByValue extends WriteResult implements Structure.ByValue {
        }
    }

    @Structure.FieldOrder({"bytes_read", "error"})
    public static class ReadResult extends Structure {
        public NativeLong bytes_read;
        public Error.ByReference error;

        public ReadResult() {
            super();
        }

        public ReadResult(NativeLong bytes_read, Error.ByReference error) {
            super();
            this.bytes_read = bytes_read;
            this.error = error;
        }

        public static class ByReference extends ReadResult implements Structure.ByReference {
        }

        public static class ByValue extends ReadResult implements Structure.ByValue {
        }
    }

    @Structure.FieldOrder({"encryption_key", "error"})
    public static class EncryptionKeyResult extends Structure {
        public JNAUplink.EncryptionKey.ByReference encryption_key;
        public Error.ByReference error;

        public EncryptionKeyResult() {
            super();
        }

        public EncryptionKeyResult(JNAUplink.EncryptionKey.ByReference encryption_key, Error.ByReference error) {
            super();
            this.encryption_key = encryption_key;
            this.error = error;
        }

        public static class ByReference extends EncryptionKeyResult implements Structure.ByReference {
        }

        public static class ByValue extends EncryptionKeyResult implements Structure.ByValue {
        }
    }

    // access
    AccessResult.ByValue uplink_parse_access(String access);

    AccessResult.ByValue uplink_request_access_with_passphrase(String address, String apiKey, String passphrase);

    AccessResult.ByValue uplink_config_request_access_with_passphrase(Config.ByValue config, String address, String apiKey, String passphrase);

    StringResult.ByValue uplink_access_serialize(Access.ByReference access);

    Error.ByReference uplink_access_override_encryption_key(Access.ByReference access, String bucket, String prefix, EncryptionKey.ByReference encryptionKey);

    void uplink_free_encryption_key_result(EncryptionKeyResult.ByValue result);

    EncryptionKeyResult.ByValue uplink_derive_encryption_key(String passphrase, Pointer salt, NativeLong length);

    // long is used directly because in uplink-c its long long
    AccessResult.ByValue uplink_access_share(Access.ByReference access, Permission.ByValue permission, SharePrefix.ByReference prefixes, long size);

    void uplink_free_access_result(AccessResult.ByValue result);

    // bucket
    BucketResult.ByValue uplink_stat_bucket(Project.ByReference project, String bucket);

    BucketResult.ByValue uplink_create_bucket(Project.ByReference project, String bucket);

    BucketResult.ByValue uplink_ensure_bucket(Project.ByReference project, String bucket);

    BucketResult.ByValue uplink_delete_bucket(Project.ByReference project, String bucket);

    void uplink_free_bucket_result(BucketResult.ByValue p0);

    void uplink_free_bucket(Bucket.ByReference bucket);

    BucketIterator.ByReference uplink_list_buckets(Project.ByReference project, ListBucketsOptions.ByReference options);

    boolean uplink_bucket_iterator_next(BucketIterator.ByReference iterator);

    Error.ByReference uplink_bucket_iterator_err(BucketIterator.ByReference iterator);

    Bucket.ByReference uplink_bucket_iterator_item(BucketIterator.ByReference iterator);

    void uplink_free_bucket_iterator(BucketIterator.ByReference iterator);

    // project
    ProjectResult.ByValue uplink_config_open_project(Config.ByValue config, Access.ByReference access);

    ProjectResult.ByValue uplink_open_project(Access.ByReference access);

    Error.ByReference uplink_close_project(Project.ByReference project);

    void uplink_free_project_result(ProjectResult.ByValue result);

    // object
    ObjectResult.ByValue uplink_stat_object(Project.ByReference project, String bucket, String key);

    ObjectResult.ByValue uplink_delete_object(Project.ByReference project, String bucket, String key);

    void uplink_free_object_result(ObjectResult.ByValue result);

    void uplink_free_object(Object.ByReference object);

    ObjectIterator.ByReference uplink_list_objects(Project.ByReference project, String bucket, ListObjectsOptions.ByReference options);

    boolean uplink_object_iterator_next(ObjectIterator.ByReference iterator);

    Error.ByReference uplink_object_iterator_err(ObjectIterator.ByReference iterator);

    Object.ByReference uplink_object_iterator_item(ObjectIterator.ByReference iterator);

    void uplink_free_object_iterator(ObjectIterator.ByReference iterator);

    // upload
    UploadResult.ByValue uplink_upload_object(Project.ByReference project, String bucket, String key, UploadOptions.ByReference options);

    WriteResult.ByValue uplink_upload_write(Upload.ByReference upload, Pointer bytes, NativeLong size);

    Error.ByReference uplink_upload_commit(Upload.ByReference upload);

    Error.ByReference uplink_upload_abort(Upload.ByReference upload);

    ObjectResult.ByValue uplink_upload_info(Upload.ByReference upload);

    Error.ByReference uplink_upload_set_custom_metadata(Upload.ByReference upload, CustomMetadata.ByValue metadata);

    void uplink_free_write_result(WriteResult.ByValue result);

    void uplink_free_upload_result(UploadResult.ByValue result);

    // download
    DownloadResult.ByValue uplink_download_object(Project.ByReference project, String bucket, String key, DownloadOptions options);

    ReadResult.ByValue uplink_download_read(Download.ByReference download, byte[] bytes, NativeLong size);

    ObjectResult.ByValue uplink_download_info(Download.ByReference download);

    void uplink_free_read_result(ReadResult.ByValue result);

    Error.ByReference uplink_close_download(Download.ByReference download);

    void uplink_free_download_result(DownloadResult.ByValue result);

    // other
    void uplink_free_string_result(StringResult.ByValue result);

    void uplink_free_error(Error.ByReference error);
}
