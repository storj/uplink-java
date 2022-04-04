package io.storj.uplink.example;

import io.storj.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class Example {

    public static void main(String[] args) {

        String filesDir = System.getProperty("java.io.tmpdir");
        UplinkOption[] uplinkOptions = new UplinkOption[]{
                UplinkOption.tempDir(filesDir),
        };

        Uplink uplink = new Uplink(uplinkOptions);

        Access accessGrant = Access.parse(System.getenv("UPLINK_ACCESS"));

        try (Project project = uplink.openProject(accessGrant)) {
            project.ensureBucket("bucket1");

            String keyName = "key" + new Date().getTime();

            //create new object
            try (ObjectOutputStream upload = project.uploadObject("bucket1", keyName)) {
                byte[] data = "Hello world".getBytes(StandardCharsets.UTF_8);
                upload.write(data, 0, data.length);
                upload.commit();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

            //download object
            try (ObjectInputStream download = project.downloadObject("bucket1", keyName)) {
                byte[] buffer = new byte[1024];
                int n = download.read(buffer);
                System.out.println(n);
                if (new String(buffer).equals("Hello world")) {
                    throw new RuntimeException();
                }
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

            //list objects
            try (ObjectIterator it = project.listObjects("bucket1")) {
                while (it.hasNext()) {
                    System.out.println(it.next().getKey());
                }
            }

        }

    }

}