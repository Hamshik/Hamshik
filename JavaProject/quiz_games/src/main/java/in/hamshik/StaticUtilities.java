package in.hamshik;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.lang.reflect.Type;

public class StaticUtilities {

    private static Gson gson;

    public static <T> List<T> load(String path, Type type) throws Exception{
        gson = new Gson();
        InputStreamReader reader = new InputStreamReader(App.class.getResourceAsStream(path));
        return gson.fromJson(reader, type);
    }

    public static void runPy(String pythonPath, String scriptPath, String workingDic) throws Exception {

        // Array of all paths to check
        String[] pathsToCheck = {pythonPath, scriptPath, workingDic};

        // Loop through each path and check if it exists
        for (String p : pathsToCheck) {
            Path path = Paths.get(p);
            if (!Files.exists(path)) throw new Exception("Path does not exist: " + p);
            else System.out.println("Exists");
        }

        // At this point, all paths exist, we can run Python
        ProcessBuilder pb = new ProcessBuilder(pythonPath, scriptPath);
        pb.directory(new File(workingDic));

        Process process = pb.start();

        // Read Python output
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            System.out.println("Python Output:");
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }

        // Wait for Python to finish
        int exitCode = process.waitFor();
        System.out.println("Python process finished with exit code: " + exitCode);
        MControllerVar.isRunning = false;

        if (exitCode != 0) throw new Exception("Python failed to execute.");
        
    }

    public static void writeJson(Type type, String path) throws IOException {
        gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        Writer writer = Files.newBufferedWriter(Path.of(path));
        gson.toJson(type, writer);
        writer.close();
    }
}