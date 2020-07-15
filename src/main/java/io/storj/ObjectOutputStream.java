package io.storj;

import com.sun.jna.Memory;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * An {@link OutputStream} for writing data to an object stored on the Storj network.
 */
public class ObjectOutputStream extends OutputStream {

    private JNAUplink.Upload.ByReference cUpload;

    /**
     * Used for efficiency by `write(int b)`
     */
    private byte[] buf = new byte[1];
    private boolean committed = false;
    private boolean aborted = false;

    ObjectOutputStream(JNAUplink.Upload.ByReference cUpload) {
        this.cUpload = cUpload;
    }

    /**
     * Writes the specified byte to this output stream. The general
     * contract for <code>write</code> is that one byte is written
     * to the output stream. The byte to be written is the eight
     * low-order bits of the argument <code>b</code>. The 24
     * high-order bits of <code>b</code> are ignored.
     * <p>
     * Subclasses of <code>OutputStream</code> must provide an
     * implementation for this method.
     * <p>
     * If error occur upload is automatically aborted.
     *
     * @param b the <code>byte</code>.
     * @throws IOException if an I/O error occurs. In particular,
     *                     an <code>IOException</code> may be thrown if the
     *                     output stream has been closed.
     */
    @Override
    public void write(int b) throws IOException {
        try {
            buf[0] = (byte) b;

            Pointer byteArray = new Memory(1);
            byteArray.write(0, buf, 0, buf.length);
            JNAUplink.WriteResult.ByValue writeResult = JNAUplink.INSTANCE.upload_write(this.cUpload, byteArray, new NativeLong(1));
            try {
                ExceptionUtil.handleError(writeResult.error);
            } catch (StorjException e) {
                throw new IOException(e);
            }
            JNAUplink.INSTANCE.free_write_result(writeResult);
        } catch (IOException e) {
            this.abort();
            throw e;
        }
    }

    /**
     * Writes <code>len</code> bytes from the specified byte array
     * starting at offset <code>off</code> to this output stream.
     * The general contract for <code>write(b, off, len)</code> is that
     * some of the bytes in the array <code>b</code> are written to the
     * output stream in order; element <code>b[off]</code> is the first
     * byte written and <code>b[off+len-1]</code> is the last byte written
     * by this operation.
     * <p>
     * The <code>write</code> method of <code>OutputStream</code> calls
     * the write method of one argument on each of the bytes to be
     * written out. Subclasses are encouraged to override this method and
     * provide a more efficient implementation.
     * <p>
     * If <code>b</code> is <code>null</code>, a
     * <code>NullPointerException</code> is thrown.
     * <p>
     * If <code>off</code> is negative, or <code>len</code> is negative, or
     * <code>off+len</code> is greater than the length of the array
     * <code>b</code>, then an <tt>IndexOutOfBoundsException</tt> is thrown.
     * <p>
     * If error occur upload is automatically aborted.
     *
     * @param b   the data.
     * @param off the start offset in the data.
     * @param len the number of bytes to write.
     * @throws IOException if an I/O error occurs. In particular,
     *                     an <code>IOException</code> is thrown if the output
     *                     stream is closed.
     */
    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        try {
            if (b == null) {
                throw new NullPointerException();
            } else if ((off < 0) || (off > b.length) || (len < 0) ||
                    ((off + len) > b.length) || ((off + len) < 0)) {
                throw new IndexOutOfBoundsException();
            } else if (len == 0) {
                return;
            }

            Pointer byteArray = new Memory(len);
            byteArray.write(0, b, off, len);
            JNAUplink.WriteResult.ByValue writeResult = JNAUplink.INSTANCE.upload_write(this.cUpload, byteArray, new NativeLong(len));
            try {
                ExceptionUtil.handleError(writeResult.error);
            } catch (StorjException e) {
                throw new IOException(e);
            }
            JNAUplink.INSTANCE.free_write_result(writeResult);
        } catch (IOException e) {
            this.abort();
            throw e;
        }
    }


    /**
     * Commits data to the store.
     *
     * @throws StorjException if an error occurs during committing object
     */
    public void commit() throws StorjException {
        this.committed = true;
        JNAUplink.Error.ByReference error = JNAUplink.INSTANCE.upload_commit(this.cUpload);
        ExceptionUtil.handleError(error);
    }

    /**
     * Returns the last information about the uploaded object.
     *
     * @return the object info
     * @throws StorjException if an error occurs during retrieving info
     */
    public ObjectInfo info() throws StorjException {
        JNAUplink.ObjectResult.ByValue result = JNAUplink.INSTANCE.upload_info(this.cUpload);
        ExceptionUtil.handleError(result.error);
        ObjectInfo info = new ObjectInfo(result.object);
        JNAUplink.INSTANCE.free_object_result(result);
        return info;
    }

    /**
     * Method updates custom metadata to be included with the object.
     *
     * @param metadata the metadata map, if it is null, it won't be modified.
     * @throws StorjException if an error occurs during setting custom metadata
     */
    public void setCustomMetadata(Map<String, String> metadata) throws StorjException {
        if (metadata == null) {
            return;
        }

        JNAUplink.CustomMetadataEntry.ByReference singleEntry = new JNAUplink.CustomMetadataEntry.ByReference();
        Structure[] entries = singleEntry.toArray(metadata.size());
        int i = 0;
        for (Map.Entry<String, String> metadataEntry : metadata.entrySet()) {
            JNAUplink.CustomMetadataEntry.ByReference entry = (JNAUplink.CustomMetadataEntry.ByReference) entries[i];
            byte[] uftString = metadataEntry.getKey().getBytes(StandardCharsets.UTF_8);
            Memory m = new Memory(uftString.length + 1);
            m.write(0, uftString, 0, uftString.length);
            m.setByte(uftString.length, (byte) 0);
            entry.key = m;
            entry.key_length = new NativeLong(uftString.length);

            uftString = metadataEntry.getValue().getBytes(StandardCharsets.UTF_8);
            m = new Memory(uftString.length + 1);
            m.write(0, uftString, 0, uftString.length);
            m.setByte(uftString.length, (byte) 0);
            entry.value = m;
            entry.value_length = new NativeLong(uftString.length);
            i++;
        }

        JNAUplink.CustomMetadata.ByValue customMetadata = new JNAUplink.CustomMetadata.ByValue();
        customMetadata.entries = ((JNAUplink.CustomMetadataEntry.ByReference) entries[0]);
        customMetadata.count = new NativeLong(entries.length);

        JNAUplink.Error.ByReference error = JNAUplink.INSTANCE.upload_set_custom_metadata(this.cUpload, customMetadata);
        ExceptionUtil.handleError(error);
    }

    private void abort() throws IOException {
        if (this.aborted) {
            return;
        }

        JNAUplink.Error.ByReference error = JNAUplink.INSTANCE.upload_abort(this.cUpload);
        try {
            ExceptionUtil.handleError(error);
        } catch (StorjException e) {
            throw new IOException(e);
        }
    }

    /**
     * Closes this input stream and releases any system resources associated
     * with the stream.
     * <p>
     * If stream was not committed then upload is automatically aborted on close.
     *
     * <p> The <code>close</code> method of <code>InputStream</code> does
     * nothing.
     *
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public void close() throws IOException {
        try {
            if (!this.committed) {
                this.abort();
            }
        } finally {
            JNAUplink.UploadResult.ByValue result = new JNAUplink.UploadResult.ByValue();
            result.upload = this.cUpload;
            JNAUplink.INSTANCE.free_upload_result(result);
        }
    }
}
