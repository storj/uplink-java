# uplink-java

## ⚠️ Important Notice: Repository Status

This library is **no longer actively maintained** and may not be compatible with current Storj services. Support for this library is unavailable, and users are advised to use alternative solutions such as the [Storj Go library](https://github.com/storj/uplink).

### Recommendations for Users:
- **Use at your own risk**: This library may contain outdated dependencies and unsupported features.
- **Alternative Options**: For actively maintained libraries, consider using the official [Storj Go library](https://github.com/storj/uplink).

### Current Release:
The latest release of this library is `v1.0.0-rc.1`. No further updates or fixes are planned.

## Overview

Java library to access the Storj Network

### Disclaimer
This repository is provided "as-is" without any guarantees of functionality or compatibility. Please ensure you test thoroughly before using it in production environments.


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

## Release process

_NOTE: This section is for the maintainer of the library, not for the users of the library._

Switch to a fix release:

```
mvn versions:set -DnewVersion=1.0.0
cd example 
mvn versions:set -DnewVersion=1.0.0
```

And change manually the dependency version in `pom.xml`.

Make sure you have the credentials in your `~/.m2/settings.xml`:

```
<settings>
  <servers>
    <server>
      <id>ossrh</id>
      <username>storjling</username>
      <password>PASSWORD</password>
    </server>
  </servers>
</settings>
```

Upload to maven staging:

```
mvn clean deploy -P release
```
