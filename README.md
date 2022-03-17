# uplink-java

Java library to access the Storj Network

## Example

First, you need to install to library locally:

```
mvn clean install -DskipTests
```

After that you can add the Java dependency:

```
        <dependency>
            <groupId>io.storj</groupId>
            <artifactId>uplink-java</artifactId>
            <version>1.1.0-SNAPSHOT</version>
        </dependency>
```

You need the [uplink-c](https://github.com/storj/uplink-c) binary, which can be built with `./scripts/build-uplink.sh`. 

After the build the library can be found in .build which should be added to the `LD_LIBRARY_PATH` environment variable on linux:


```
export LD_LIBRARY_PATH=`pwd`/.build:$LD_LIBRARY_PATH
```

Now the library can be used. The only requirement is a Storj access grant which can be generated on satellite API.

Example for basic operations.

```java


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

```