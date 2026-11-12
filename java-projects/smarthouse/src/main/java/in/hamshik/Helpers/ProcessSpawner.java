package in.hamshik.Helpers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public final class ProcessSpawner {
    public static String createProcess(Consumer<String> out, String path, String... opts) {
        try {
            List<String> command = new ArrayList<>();
            command.add(path);
            command.addAll(Arrays.asList(opts));

            Process process = new ProcessBuilder(command)
                    .redirectOutput(ProcessBuilder.Redirect.DISCARD)
                    .start();

            // Read only stderr
            String errors;
            try (var stream = process.getErrorStream()) {
                errors = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) 
                throw new IOException("Process exited with code " + exitCode + ": " + errors);

            return errors;
        } catch (IOException e) {
            out.accept(e.getMessage());
            return null;
        } catch (InterruptedException _) {
            return null;
        }
    }
}