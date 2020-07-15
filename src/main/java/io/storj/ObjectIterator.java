package io.storj;

import java.util.Iterator;

public class ObjectIterator implements AutoCloseable, Iterator<ObjectInfo>, Iterable<ObjectInfo> {

    private JNAUplink.ObjectIterator.ByReference cIterator;

    private ObjectInfo currentItem;
    private Boolean hasNext = null;

    ObjectIterator(JNAUplink.Project.ByReference project, String bucket, ObjectListOption... options) {
        this.cIterator = JNAUplink.INSTANCE.list_objects(project, bucket, ObjectListOption.internal(options));
    }

    @Override
    public boolean hasNext() {
        if (this.hasNext == null) {
            this.hasNext = JNAUplink.INSTANCE.object_iterator_next(this.cIterator);
            if (this.hasNext) {
                JNAUplink.Object.ByReference obj = JNAUplink.INSTANCE.object_iterator_item(this.cIterator);
                this.currentItem = new ObjectInfo(obj);
                JNAUplink.INSTANCE.free_object(obj);
            } else {
                JNAUplink.Error.ByReference error = JNAUplink.INSTANCE.object_iterator_err(this.cIterator);
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
    public ObjectInfo next() {
        if (currentItem != null) {
            this.hasNext = JNAUplink.INSTANCE.object_iterator_next(this.cIterator);
            ObjectInfo result = this.currentItem;
            if (hasNext) {
                JNAUplink.Object.ByReference obj = JNAUplink.INSTANCE.object_iterator_item(this.cIterator);
                this.currentItem = new ObjectInfo(obj);
                JNAUplink.INSTANCE.free_object(obj);
            } else {
                JNAUplink.Error.ByReference error = JNAUplink.INSTANCE.object_iterator_err(this.cIterator);
                try {
                    ExceptionUtil.handleError(error);
                } catch (StorjException e) {
                    throw new RuntimeException(e);
                }
            }
            return result;
        } else {
            this.hasNext = JNAUplink.INSTANCE.object_iterator_next(this.cIterator);
            if (this.hasNext) {
                JNAUplink.Object.ByReference obj = JNAUplink.INSTANCE.object_iterator_item(this.cIterator);
                ObjectInfo result = new ObjectInfo(obj);
                JNAUplink.INSTANCE.free_object(obj);
                return result;
            } else {
                JNAUplink.Error.ByReference error = JNAUplink.INSTANCE.object_iterator_err(this.cIterator);
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
    public Iterator<ObjectInfo> iterator() {
        return this;
    }

    @Override
    public void close() throws StorjException {
        JNAUplink.INSTANCE.free_object_iterator(this.cIterator);
    }
}
