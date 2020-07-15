package io.storj;

import java.util.Iterator;

// TODO add javadoc
public class BucketIterator implements AutoCloseable, Iterator<BucketInfo>, Iterable<BucketInfo> {

    private JNAUplink.BucketIterator.ByReference cIterator;

    private BucketInfo currentItem;
    private Boolean hasNext = null;

    BucketIterator(JNAUplink.Project.ByReference project, BucketListOption... options) {
        this.cIterator = JNAUplink.INSTANCE.list_buckets(project, BucketListOption.internal(options));
    }

    @Override
    public boolean hasNext() {
        if (this.hasNext == null) {
            this.hasNext = JNAUplink.INSTANCE.bucket_iterator_next(this.cIterator);
            if (this.hasNext) {
                JNAUplink.Bucket.ByReference bucket = JNAUplink.INSTANCE.bucket_iterator_item(this.cIterator);
                this.currentItem = new BucketInfo(bucket);
                JNAUplink.INSTANCE.free_bucket(bucket);
            } else {
                JNAUplink.Error.ByReference error = JNAUplink.INSTANCE.bucket_iterator_err(this.cIterator);
                try {
                    ExceptionUtil.handleError(error);
                } catch (StorjException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return this.hasNext;
    }

    @Override
    public BucketInfo next() {
        if (currentItem != null) {
            this.hasNext = JNAUplink.INSTANCE.bucket_iterator_next(this.cIterator);
            BucketInfo result = this.currentItem;
            if (hasNext) {
                JNAUplink.Bucket.ByReference bucket = JNAUplink.INSTANCE.bucket_iterator_item(this.cIterator);
                this.currentItem = new BucketInfo(bucket);
                JNAUplink.INSTANCE.free_bucket(bucket);
            } else {
                JNAUplink.Error.ByReference error = JNAUplink.INSTANCE.bucket_iterator_err(this.cIterator);
                try {
                    ExceptionUtil.handleError(error);
                } catch (StorjException e) {
                    throw new RuntimeException(e);
                }
            }
            return result;
        } else {
            this.hasNext = JNAUplink.INSTANCE.bucket_iterator_next(this.cIterator);
            if (this.hasNext) {
                JNAUplink.Bucket.ByReference bucket = JNAUplink.INSTANCE.bucket_iterator_item(this.cIterator);
                BucketInfo result = new BucketInfo(bucket);
                JNAUplink.INSTANCE.free_bucket(bucket);
                return result;
            } else {
                JNAUplink.Error.ByReference error = JNAUplink.INSTANCE.bucket_iterator_err(this.cIterator);
                try {
                    ExceptionUtil.handleError(error);
                } catch (StorjException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return null;
    }

    @Override
    public Iterator<BucketInfo> iterator() {
        return this;
    }

    @Override
    public void close() throws StorjException {
        try {
            JNAUplink.Error.ByReference error = JNAUplink.INSTANCE.bucket_iterator_err(cIterator);
            ExceptionUtil.handleError(error);
        } finally {
            JNAUplink.INSTANCE.free_bucket_iterator(cIterator);
        }
    }

}