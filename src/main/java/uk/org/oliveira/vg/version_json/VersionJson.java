package uk.org.oliveira.vg.version_json;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.jetbrains.annotations.NotNull;
import uk.org.oliveira.vg.NotificationManager;
import uk.org.oliveira.vg.VGUtils;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class VersionJson {

    public static void create(String currentFolderPath, List<String> files) throws IOException {
        String projectRootPath = VGUtils.getProjectRootPathFrom(currentFolderPath);
        if (projectRootPath != null) {
            String file = addFiles(projectRootPath, files);
            writeToFile(projectRootPath, file);
        } else {
            NotificationManager.notifyError("Unable to get project root folder");
        }
    }

    public static void addFile(String filePath) throws IOException, ParseException {
        String projectRootPath = VGUtils.getProjectRootPathFrom(filePath);
        if (projectRootPath != null) {
            List<String> files = getFileList(projectRootPath);
            files.add(filePath);
            String file = addFiles(projectRootPath, files);
            writeToFile(projectRootPath, file);
        } else {
            NotificationManager.notifyError("Unable to get project root folder");
        }
    }

    public static void removeFile(String filePath) throws IOException, ParseException {
        String projectRootPath = VGUtils.getProjectRootPathFrom(filePath);
        if (projectRootPath != null) {
            List<String> files = getFileList(projectRootPath);
            files.remove(filePath);
            String file = addFiles(projectRootPath, files);
            writeToFile(projectRootPath, file);
        } else {
            NotificationManager.notifyError("Unable to get project root folder");
        }
    }

    private static String addFiles(@NotNull String projectRootPath, List<String> files) {
        String filesPaths = files.size() > 0 ? "\"" : "";
        for (int i = 0; i < files.size(); i++) {
            String filePath = files.get(i).replace(projectRootPath, "../");
            filesPaths = filesPaths.concat(filePath).concat("\"");
            if (i != files.size() - 1) {
                filesPaths = filesPaths.concat(",\n            \"");
            }
        }

        return ""
                .concat("[\n")
                .concat("    {\n")
                .concat("        \"userToUser\": \"root\",\n")
                .concat("        \"dependencies\": [],\n")
                .concat("        \"fileList\": [\n")
                .concat("            ").concat(filesPaths).concat("\n")
                .concat("        ]\n")
                .concat("    }\n")
                .concat("]");
    }

    private static List<String> getFileList(String projectRootPath) throws IOException, ParseException {
        Path versionJsonPath = Path.of(projectRootPath, "postgres/release/current/version.json");
        String file = String.join("\n", Files.readAllLines(versionJsonPath));
        JSONParser parser = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE);
        JSONArray versionJsonArray = (JSONArray) parser.parse(file);
        JSONObject versionJsonObject = (JSONObject) versionJsonArray.get(0);
        JSONArray fileList = (JSONArray) versionJsonObject.get("fileList");

        List<String> filesList = new ArrayList<>();
        for (int i = 0; i < fileList.size(); i++) {
            String path = (String) fileList.get(i);
            filesList.add(path.replace("../", projectRootPath));
        }
        return filesList;
    }

    private static void writeToFile(String projectRootPath, String file) throws IOException {
        String versionJsonPath = Path.of(projectRootPath, "postgres/release/current/version.json").toString();
        FileWriter writer = new FileWriter(versionJsonPath);
        writer.write(file);
        writer.close();
    }
}
