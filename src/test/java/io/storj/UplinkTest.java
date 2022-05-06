package io.storj;

import com.google.common.io.ByteStreams;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static io.storj.ObjectDownloadOption.length;
import static io.storj.ObjectDownloadOption.offset;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class UplinkTest {

    private static Access access;

    private UplinkOption[] uplinkOptions;

    @Before
    public void setUp() throws StorjException {
        String accessGrant = getAccessGrant();
        access = Access.parse(accessGrant);
        String filesDir = System.getProperty("java.io.tmpdir");
        uplinkOptions = new UplinkOption[]{
                UplinkOption.tempDir(filesDir),
        };
    }

    private static String getAccessGrant() {
        String accessGrant = System.getenv("GATEWAY_0_ACCESS");
        if (accessGrant == null || accessGrant.length() == 0) {
            accessGrant = System.getenv("UPLINK_ACCESS");
        }
        return accessGrant;
    }

    @Test
    public void testBuckets() throws Exception {
        Uplink uplink = new Uplink(uplinkOptions);
        String postfix = "-" + new Date().getTime();
        try (Project project = uplink.openProject(access)) {

            String[] expectedBuckets = new String[]{"test-bucket1" + postfix, "another2" + postfix, "foo3" + postfix, "bar4" + postfix};

            for (String name : expectedBuckets) {
                BucketInfo createBucketInfo = project.createBucket(name);
                Assert.assertEquals(name, createBucketInfo.getName());

                BucketInfo statBucket = project.statBucket(name);
                Assert.assertEquals(name, statBucket.getName());
                Assert.assertEquals(createBucketInfo, statBucket);

                BucketInfo ensureBucketInfo = project.ensureBucket(name);
                Assert.assertEquals(name, ensureBucketInfo.getName());

            }

            String[] toEnsure = new String[]{"gogo", "for", "itit"};
            for (String name : toEnsure) {
                BucketInfo ensureBucketInfo = project.ensureBucket(name);
                Assert.assertEquals(name, ensureBucketInfo.getName());

                BucketInfo statBucket = project.statBucket(name);
                Assert.assertEquals(name, statBucket.getName());
                Assert.assertEquals(ensureBucketInfo, statBucket);
            }

            List<String> combine = new ArrayList<>();
            combine.addAll(Arrays.asList(expectedBuckets));
            combine.addAll(Arrays.asList(toEnsure));
            expectedBuckets = combine.toArray(new String[combine.size()]);

            List<BucketInfo> buckets = new ArrayList<>();
            try (BucketIterator bucketsIterator = project.listBuckets()) {
                for (BucketInfo bucketInfo : bucketsIterator) {
                    buckets.add(bucketInfo);
                }
            }

            for (String b : expectedBuckets) {
                boolean found = false;
                for (BucketInfo bucket : buckets) {
                    if (bucket.getName().equals(b)) {
                        found = true;
                        break;
                    }
                }
                Assert.assertTrue("Bucket " + b + " is not returned", found);
            }

            for (String b : expectedBuckets) {
                BucketInfo deleteBucketInfo = project.deleteBucket(b);
                Assert.assertEquals(b, deleteBucketInfo.getName());
            }

            for (BucketInfo bucket : buckets) {
                try {
                    project.statBucket(bucket.getName());
                } catch (StorjException e) {
                    Assert.assertTrue("Unexpected error: " + e.getMessage(), e.getMessage().contains("bucket not found"));
                }
            }
        }
    }

    @Test
    public void testObjects() throws Exception {
        Uplink uplink = new Uplink(uplinkOptions);
        String postfix = "-" + new Date().getTime();
        try (Project project = uplink.openProject(access)) {
            BucketInfo createBucketInfo = project.createBucket("test-objects-test-bucket" + postfix);

            byte[] expectedData = new byte[2 * 1024 * 1024];
            Random random = new Random();
            random.nextBytes(expectedData);

            String key = "test-file-cążęć-学中文";
            Map<String, String> metadata = new HashMap<>();
            metadata.put("custom-meta-1", "foo");
            metadata.put("custom-meta-2", "boo");
            final Calendar date = Calendar.getInstance();
            date.setTime(new Date());
            date.add(Calendar.MONTH, 1);
            Date expires = date.getTime();
            try (ObjectOutputStream os = project.uploadObject(createBucketInfo.getName(), key,
                    ObjectUploadOption.expires(expires))) {
                os.setCustomMetadata(metadata);
                os.write(expectedData);
                os.commit();
            }

            ObjectInfo objectInfo = project.statObject(createBucketInfo.getName(), key);
            Assert.assertEquals(key, objectInfo.getKey());
            Assert.assertEquals(expectedData.length, objectInfo.getSystemMetadata().getContentLength());
            Assert.assertEquals(metadata, objectInfo.getCustomMetadata());

            byte[] data;
            try (ObjectInputStream is = project.downloadObject(createBucketInfo.getName(), key)) {
                data = ByteStreams.toByteArray(is);
                Assert.assertEquals(objectInfo, is.info());
            }
            Assert.assertArrayEquals(expectedData, data);

            try (InputStream is = project.downloadObject(createBucketInfo.getName(), key,
                    offset(200), length(300))) {
                data = ByteStreams.toByteArray(is);
            }
            Assert.assertArrayEquals(Arrays.copyOfRange(expectedData, 200, 500), data);

            try (ObjectOutputStream os = project.uploadObject(createBucketInfo.getName(), "test-file-2")) {
                os.write(expectedData);
            }

            try {
                project.statObject(createBucketInfo.getName(), "test-file-2");
                fail("Exception not thrown");
            } catch (StorjException e) {
            }

            ObjectInfo deleteObjectInfo = project.deleteObject(createBucketInfo.getName(), key);
            Assert.assertEquals(objectInfo, deleteObjectInfo);

            project.deleteBucket(createBucketInfo.getName());
        }
    }

    @Test
    public void testObjectsListing() throws Exception {
        Uplink uplink = new Uplink(uplinkOptions);
        try (Project project = uplink.openProject(access)) {
            BucketInfo createBucketInfo = project.ensureBucket("test-objects-listing");

            byte[] expectedData = new byte[2 * 1024 * 1024];
            Random random = new Random();
            random.nextBytes(expectedData);

            Map<String, String> metadata = Collections.singletonMap("custom-meta-ążęć", "foo-学中文");

            final Map<String, ObjectInfo> uploadedObjects = new HashMap<>();
            for (int i = 0; i < 10; i++) {
                try (ObjectOutputStream os = project.uploadObject(createBucketInfo.getName(), "test-file" + i)) {
                    os.setCustomMetadata(metadata);
                    os.write(expectedData);
                    os.commit();
                    uploadedObjects.put("test-file" + i, os.info());
                }
            }

            // double check metadata keys
            for (String key : uploadedObjects.keySet()) {
                Assert.assertEquals(metadata, uploadedObjects.get(key).getCustomMetadata());
            }

            final Map<String, ObjectInfo> listedObjects = new HashMap<>();
            try (ObjectIterator objectIterator = project.listObjects(createBucketInfo.getName(),
                    ObjectListOption.system(), ObjectListOption.custom())) {
                for (ObjectInfo objectInfo : objectIterator) {
                    listedObjects.put(objectInfo.getKey(), objectInfo);
                }
            }
            Assert.assertEquals(listedObjects, uploadedObjects);

            for (String key : uploadedObjects.keySet()) {
                project.deleteObject(createBucketInfo.getName(), key);
            }
            project.deleteBucket(createBucketInfo.getName());
        }
    }

    @Test
    public void testUploadDownloadParallel() throws Exception {
        ExecutorService executor
                = Executors.newFixedThreadPool(4);

        Uplink uplink = new Uplink(uplinkOptions);
        try (final Project project = uplink.openProject(access)) {
            BucketInfo createBucketInfo = project.ensureBucket("test-parallel");

            final byte[] expectedData = new byte[2 * 1024 * 1024];
            Random random = new Random();
            random.nextBytes(expectedData);
            int numberOfObjects = 10;

            List<Future<ObjectInfo>> uploadInfo = new ArrayList<>();
            for (int i = 0; i < numberOfObjects; i++) {
                final int index = i;
                uploadInfo.add(executor.submit(new Callable<ObjectInfo>() {
                    @Override
                    public ObjectInfo call() throws Exception {
                        InputStream is = new ByteArrayInputStream(expectedData);
                        try (ObjectOutputStream os = project.uploadObject("test-parallel", "object" + index)) {
                            ByteStreams.copy(is, os);
                            os.commit();
                            return os.info();
                        }
                    }
                }));
            }
            for (Future<ObjectInfo> info : uploadInfo) {
                info.get();
            }

            List<Future<ObjectInfo>> downloadInfo = new ArrayList<>();
            for (int i = 0; i < numberOfObjects; i++) {
                final int index = i;
                downloadInfo.add(executor.submit(new Callable<ObjectInfo>() {
                    @Override
                    public ObjectInfo call() throws Exception {
                        try (ObjectInputStream is = project.downloadObject("test-parallel", "object" + index)) {
                            byte[] data = ByteStreams.toByteArray(is);
                            Assert.assertArrayEquals(expectedData, data);
                            return is.info();
                        }
                    }
                }));
            }
            for (Future<ObjectInfo> info : downloadInfo) {
                info.get();
            }

            List<Future<ObjectInfo>> deleteInfo = new ArrayList<>();
            for (int i = 0; i < numberOfObjects; i++) {
                final int index = i;
                deleteInfo.add(executor.submit(new Callable<ObjectInfo>() {
                    @Override
                    public ObjectInfo call() throws Exception {
                        return project.deleteObject("test-parallel", "object" + index);
                    }
                }));
            }
            for (Future<ObjectInfo> info : deleteInfo) {
                info.get();
            }

            project.deleteBucket("test-parallel");
        }
    }

    @Test
    public void testPermissions() throws Exception {
        String testData = "test-data";
        Uplink uplink = new Uplink(uplinkOptions);
        try (Project project = uplink.openProject(access)) {
            project.ensureBucket("test-upload-permission");

            try (ObjectOutputStream os = project.uploadObject("test-upload-permission", "test-key")) {
                os.write(testData.getBytes());
                os.commit();
            }
        }

        Permission permission = new Permission.Builder().allowDownload().allowList().build();
        Access sharedAccess = access.share(permission, new SharePrefix("test-upload-permission"));
        try (Project project = uplink.openProject(sharedAccess)) {
            byte[] data = null;
            try (ObjectInputStream is = project.downloadObject("test-upload-permission", "test-key")) {
                data = ByteStreams.toByteArray(is);
            }
            Assert.assertArrayEquals(testData.getBytes(), data);

            try {
                project.deleteObject("test-upload-permission", "test-key");
                fail();
            } catch (StorjException e) {
                // exception expected
            }
        }

        sharedAccess = access.share(permission, new SharePrefix("not-existing-bucket"));
        try (Project project = uplink.openProject(sharedAccess)) {
            try {
                project.statObject("test-upload-permission", "test-key");
                fail();
            } catch (StorjException e) {
                // exception expected
            }
        }

        try (Project project = uplink.openProject(access)) {
            project.deleteObject("test-upload-permission", "test-key");
            project.deleteBucket("test-upload-permission");
        }
    }

    @Test
    public void testAccess() throws Exception {
        String serializedAccess = getAccessGrant();
        Access access = Access.parse(serializedAccess);
        String newSerializedAccess = access.serialize();
        assertEquals(serializedAccess, newSerializedAccess);

        Permission permission = new Permission.Builder().allowDownload().allowList().build();
        Access sharedAccess = access.share(permission);
        sharedAccess.serialize();

        permission = new Permission.Builder().allowDownload().allowList().allowUpload().allowDelete().build();
        sharedAccess = access.share(permission,
                new SharePrefix("bucket1"), new SharePrefix("bucket2", "my-prefix"));
        sharedAccess.serialize();

        sharedAccess.overrideEncryptionKey("bucket2", "my-prefix/", new EncryptionKey("strong-pass", "salt".getBytes()));
    }

    @Test
    public void testErrors() throws Exception {
        try {
            Access access = Access.parse("INVALID_ACCESS");
            fail();
        } catch (StorjException e) {
            // error expected
        }

        Uplink uplink = new Uplink(uplinkOptions);
        try {
            uplink.requestAccessWithPassphrase("1.1.1.1:7777", "invalid api key", "pass");
            fail();
        } catch (StorjException e) {
            // error expected
        }
        try {
            uplink.requestAccessWithPassphrase(null, null, null);
            fail();
        } catch (StorjException e) {
            // error expected
        }
        try {
            Project project = uplink.openProject(null);
            fail();
        } catch (StorjException e) {
            // error expected
        }

        try (final Project project = uplink.openProject(access)) {
            try {
                project.statBucket("not-existing");
                fail();
            } catch (StorjException e) {
                // error expected
            }
            try {
                project.statBucket(null);
                fail();
            } catch (StorjException e) {
                // error expected
            }
            try {
                project.deleteBucket("not-existing");
                fail();
            } catch (StorjException e) {
                // error expected
            }
            try {
                project.deleteBucket(null);
                fail();
            } catch (StorjException e) {
                // error expected
            }


            String bucketName = "existing-bucket-" + new Date().getTime();
            project.createBucket(bucketName);

            try {
                project.createBucket(bucketName);
                fail();
            } catch (StorjException e) {
                // error expected
            }

            try {
                project.createBucket(null);
                fail();
            } catch (StorjException e) {
                // error expected
            }

            try {
                project.statObject(bucketName, "not-existing-object");
                fail();
            } catch (StorjException e) {
                // error expected
            }
            try {
                project.statObject(bucketName, null);
                fail();
            } catch (StorjException e) {
                // error expected
            }

            try {
                project.deleteObject(bucketName, "not-existing-object");
            } catch (StorjException e) {
                fail();
            }

            try {
                project.deleteObject(bucketName, null);
                fail();
            } catch (StorjException e) {
                // error expected
            }

            try {
                project.downloadObject(bucketName, "not-existing-object");
                fail();
            } catch (StorjException e) {
                // error expected
            }
            try {
                project.downloadObject(bucketName, null);
                fail();
            } catch (StorjException e) {
                // error expected
            }
        }
    }

    @Test
    public void testMove() throws Exception {
        Random random = new Random();

        Uplink uplink = new Uplink(uplinkOptions);
        String prefix = "" + Math.abs(random.nextInt());
        try (Project project = uplink.openProject(access)) {
            BucketInfo sourceBucket = project.ensureBucket("test-move-from-" + prefix);
            BucketInfo destinationBucket = project.ensureBucket("test-move-to-" + prefix);
            int length = 2 * 1024 * 1024;

            byte[] expectedData = new byte[length];

            random.nextBytes(expectedData);


            try (ObjectOutputStream os = project.uploadObject(sourceBucket.getName(), "test-file")) {
                os.write(expectedData);
                os.commit();
            }

            try (ObjectOutputStream os = project.uploadObject(sourceBucket.getName(), "test-file2")) {
                os.write(expectedData);
                os.commit();
            }

            project.moveObject(sourceBucket.getName(), "test-file", destinationBucket.getName(), "test-dest");

            try (ObjectInputStream is = project.downloadObject(destinationBucket.getName(), "test-dest")) {
                ByteBuffer byteBuffer = ByteBuffer.allocate(length);
                Assert.assertArrayEquals(expectedData, ByteStreams.toByteArray(is));
            }

        }
    }

    @Test(expected = StorjException.class)
    public void testMoveFailure() throws Exception {
        Random random = new Random();

        Uplink uplink = new Uplink(uplinkOptions);
        String prefix = "" + Math.abs(random.nextInt());
        try (Project project = uplink.openProject(access)) {
            BucketInfo sourceBucket = project.ensureBucket("test-move-from-" + prefix);
            BucketInfo destinationBucket = project.ensureBucket("test-move-to-" + prefix);


            project.moveObject(sourceBucket.getName(), "test-file", destinationBucket.getName(), "test-dest");
            // exception is expected, not sigfailure
        }
    }
}
