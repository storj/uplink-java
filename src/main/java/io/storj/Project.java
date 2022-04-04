package io.storj;

/**
 * Represents a stateful resource to a project. It allows executing operations
 * like creating, listing, deleting buckets or uploading, downloading, listing, deleting objects.
 *
 * <p>Make sure to always close the Project object after completing work with it. The Project class
 * implements the {@link java.lang.AutoCloseable} interface, so it is best to use Project objects
 * in try-with-resource blocks:</p>
 *
 * <pre>
 * {@code try (Project project = uplink.openProject(access)) {
 *       Bucket bucket = project.createBucket("my-bucket")) {
 *      ...
 *  }
 * }
 * }
 * </pre>
 */
public class Project implements AutoCloseable {

    private JNAUplink.Project.ByReference project;

    Project(JNAUplink.Project.ByReference project) {
        this.project = project;
    }

    /**
     * Returns bucket metadata.
     *
     * @param bucket the bucket name
     * @return the bucket info
     * @throws StorjException if an error occurs during the deletion
     */
    public BucketInfo statBucket(String bucket) throws StorjException {
        JNAUplink.BucketResult.ByValue result = JNAUplink.INSTANCE.uplink_stat_bucket(this.project, bucket);
        ExceptionUtil.handleError(result.error);

        BucketInfo bucketInfo = new BucketInfo(result.bucket);
        JNAUplink.INSTANCE.uplink_free_bucket_result(result);
        return bucketInfo;
    }

    /**
     * Creates a bucket in this project.
     *
     * @param bucket the bucket name
     * @return the bucket info
     * @throws StorjException if an error occurs during the deletion
     */
    public BucketInfo createBucket(String bucket) throws StorjException {
        JNAUplink.BucketResult.ByValue result = null;
        BucketInfo bucketInfo = null;
        try {
            result = JNAUplink.INSTANCE.uplink_create_bucket(this.project, bucket);
            ExceptionUtil.handleError(result.error);
            bucketInfo = new BucketInfo(result.bucket);
        } finally {
            JNAUplink.INSTANCE.uplink_free_bucket_result(result);
        }
        return bucketInfo;
    }

    /**
     * Ensures that a bucket exists in this project.
     *
     * @param bucket the bucket name
     * @return the bucket info
     * @throws StorjException if an error occurs during the deletion
     */
    public BucketInfo ensureBucket(String bucket) throws StorjException {
        JNAUplink.BucketResult.ByValue result = null;
        BucketInfo bucketInfo = null;
        try {
            result = JNAUplink.INSTANCE.uplink_ensure_bucket(this.project, bucket);
            ExceptionUtil.handleError(result.error);
            bucketInfo = new BucketInfo(result.bucket);
        } finally {
            JNAUplink.INSTANCE.uplink_free_bucket_result(result);
        }
        return bucketInfo;
    }

    public BucketIterator listBuckets(BucketListOption... options) {
        return new BucketIterator(this.project, options);
    }

    /**
     * Deletes an bucket from this project.
     *
     * @param bucket the bucket name
     * @return the bucket info
     * @throws StorjException if an error occurs during the deletion
     */
    public BucketInfo deleteBucket(String bucket) throws StorjException {
        JNAUplink.BucketResult.ByValue deleteBucket = null;
        BucketInfo bucketInfo = null;
        try {
            deleteBucket = JNAUplink.INSTANCE.uplink_delete_bucket(this.project, bucket);
            ExceptionUtil.handleError(deleteBucket.error);
            bucketInfo = new BucketInfo(deleteBucket.bucket);
        } finally {
            JNAUplink.INSTANCE.uplink_free_bucket_result(deleteBucket);
        }
        return bucketInfo;
    }

    /**
     * Returns object metadata.
     *
     * @param bucket the bucket name
     * @param key    the key of the object to delete
     * @return the object info
     * @throws StorjException if an error occurs during the deletion
     */
    public ObjectInfo statObject(String bucket, String key) throws StorjException {
        JNAUplink.ObjectResult.ByValue statObject = null;
        ObjectInfo objectInfo = null;
        try {
            statObject = JNAUplink.INSTANCE.uplink_stat_object(this.project, bucket, key);
            ExceptionUtil.handleError(statObject.error);
            objectInfo = new ObjectInfo(statObject.object);
        } finally {
            JNAUplink.INSTANCE.uplink_free_object_result(statObject);
        }
        return objectInfo;
    }

    /**
     * Deletes an object from this bucket.
     *
     * @param bucket the bucket name
     * @param key    the key of the object to delete
     * @return the object info
     * @throws StorjException if an error occurs during the deletion
     */
    public ObjectInfo deleteObject(String bucket, String key) throws StorjException {
        ObjectInfo objectInfo = null;
        JNAUplink.ObjectResult.ByValue statObject = null;
        try {
            statObject = JNAUplink.INSTANCE.uplink_delete_object(this.project, bucket, key);
            ExceptionUtil.handleError(statObject.error);
            if (statObject.object == null) {
                return null;
            }
            objectInfo = new ObjectInfo(statObject.object);
        } finally {
            JNAUplink.INSTANCE.uplink_free_object_result(statObject);

        }

        return objectInfo;
    }

    /**
     * Uploads the content to a new object in this bucket.
     *
     * @param bucket  the bucket name
     * @param key     an object key
     * @param options options to apply on the new object
     * @return output stream which can be used for upload
     * @throws StorjException if an error occurs during the upload
     */
    public ObjectOutputStream uploadObject(String bucket, String key, ObjectUploadOption... options) throws StorjException {
        JNAUplink.UploadResult.ByValue uploadResult = JNAUplink.INSTANCE.uplink_upload_object(this.project, bucket, key,
                ObjectUploadOption.internal(options));
        ExceptionUtil.handleError(uploadResult.error);

        return new ObjectOutputStream(uploadResult.upload);
    }

    /**
     * Starts a download from the specific key.
     *
     * @param bucket  the bucket name
     * @param key     an object key
     * @param options options to apply while downloading
     * @return input stream which can be used to download object
     * @throws StorjException if an error occurs during the download
     */
    public ObjectInputStream downloadObject(String bucket, String key, ObjectDownloadOption... options) throws StorjException {
        JNAUplink.DownloadResult.ByValue downloadResult = JNAUplink.INSTANCE.uplink_download_object(this.project, bucket, key,
                ObjectDownloadOption.internal(options));
        ExceptionUtil.handleError(downloadResult.error);

        return new ObjectInputStream(downloadResult.download);
    }

    /**
     * Lists the objects in this bucket.
     *
     * @param bucket  the bucket name
     * @param options an optional list of {@link ObjectListOption}
     * @return an {@link Iterable}&lt;{@link ObjectInfo}&gt;
     */
    public ObjectIterator listObjects(String bucket, ObjectListOption... options) {
        return new ObjectIterator(this.project, bucket, options);
    }

    @Override
    public void close() throws StorjException {
        JNAUplink.Error.ByReference result = JNAUplink.INSTANCE.uplink_close_project(this.project);
        ExceptionUtil.handleError(result);
    }
}
