package chartographer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@SpringBootApplication
public class Main {


    public static String pathToFiles;

    public static void main(String[] args) throws IOException {
        if (args.length > 0) {
            pathToFiles = args[0].endsWith("\\") ? args[0] : args[0] + "\\";
        } else {
            pathToFiles = "/automaticFolder/";
        }
        Path contentPathAsPathType = Path.of(pathToFiles);

        if (!Files.exists(contentPathAsPathType)) {
            Files.createDirectories(contentPathAsPathType);
        }
        SpringApplication.run(Main.class, args);
    }
}
