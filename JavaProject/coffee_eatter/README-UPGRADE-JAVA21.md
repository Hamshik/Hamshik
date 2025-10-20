# Upgrade to Java 21 (LTS)

This project is configured to target Java 21.

What I changed for you:

- Fixed javafx-maven-plugin mainClass to `in.hamshik.App`.
- Added `maven-enforcer-plugin` to require Java 21 during the build.
- Added `maven-toolchains-plugin` configuration to allow selecting JDK 21 via Maven toolchains.

How to set up your environment (two options):

Option A — System JDK (recommended if you only need one JDK):

1. Download and install a JDK 21 distribution (Oracle, Eclipse Temurin/Adoptium, Azul, Liberica, etc.).
2. Set `JAVA_HOME` to the JDK 21 installation directory and ensure `java` and `javac` on `PATH` point to that JDK.
   - On Windows (cmd.exe):
     - setx JAVA_HOME "C:\\Program Files\\Java\\jdk-21"
     - setx PATH "%JAVA_HOME%\\bin;%%PATH%%"
3. Restart your terminal/IDE and run `java -version` and `mvn -v` to confirm they report Java 21.

Option B — Maven Toolchains (keep multiple JDKs installed):

1. Install JDK 21 somewhere on your machine.
2. Create the file `%USERPROFILE%\\.m2\\toolchains.xml` with contents similar to:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<toolchains>
  <toolchain>
    <type>jdk</type>
    <provides>
      <version>21</version>
    </provides>
    <configuration>
      <jdkHome>C:\\Program Files\\Java\\jdk-21</jdkHome>
    </configuration>
  </toolchain>
</toolchains>
```

3. Run `mvn -v` to see Maven detect the toolchain. The `maven-toolchains-plugin` will pick up JDK 21 for the build.

Notes:
- The project `pom.xml` already targets Java 21 (compiler `release` set to 21).
- If you installed a newer JDK (for example JDK 25), Maven will still be able to compile for `--release 21` in most cases, but runtime enforcement via the enforcer plugin will fail unless you provide a JDK 21 toolchain or set JAVA_HOME to a JDK 21.

If you'd like, I can:
- Add a sample `%USERPROFILE%\\.m2\\toolchains.xml` into the repo (not recommended for secrets or personal paths).
- Automatically download and install a matching JDK 21 for you (requires permission).
- Update CI configs to use JDK 21.

Next steps I can take now: verify project builds with the current setup, or try to install JDK 21 locally and run a full `mvn package`.
