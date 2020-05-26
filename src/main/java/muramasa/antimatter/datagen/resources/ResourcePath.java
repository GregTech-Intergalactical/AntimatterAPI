package muramasa.antimatter.datagen.resources;

import muramasa.antimatter.Ref;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ResourcePath {

    public static final Path RESOURCE_DEFAULT = Paths.get(String.join("", "resourcepacks", File.separator, Ref.ID, "_resources"));
    public static final Path RESOURCE_GENERATED = Paths.get(String.join("", "resourcepacks", File.separator, Ref.ID, "_generated"));

    public static void writeMcMeta(Path path, String description) {
        File metaFile = Paths.get(path.toString(), "pack.mcmeta").toFile();
        if (!metaFile.exists()) {
            try {
                FileWriter fileWriter = new FileWriter(metaFile);
                PrintWriter writer = new PrintWriter(fileWriter);
                writer.println("{");
                writer.println("\t\"pack\": {");
                writer.println("\t\t\"description\": \" insert something here \",");
                writer.println("\t\t\"pack_format\": 5");
                writer.println("\t}");
                writer.println("}");
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
