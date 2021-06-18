package uk.org.oliveira.vg.version_json;

import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;


import net.minidev.json.parser.ParseException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import uk.org.oliveira.vg.VGUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class VersionJsonManager {

    public static void createJsonFile(Path currentFolderPath, List<VirtualFile> files) throws IOException {
        List<String> fileList = new ArrayList<>();
        for (VirtualFile file : files) {
            fileList.add(file.getPath());
        }
        VersionJson.create(currentFolderPath.toString(), fileList);
    }

    public static List<VirtualFile> getVersionFiles(Path currentFolderPath) {
        Path schemaFolderPath = Path.of(currentFolderPath.toString(), "schema");
        Path scriptsFolderPath = Path.of(currentFolderPath.toString(), "scripts");

        List<VirtualFile> filesList = new ArrayList<>();
        if (Files.exists(scriptsFolderPath)) {
            filesList.addAll(getFilesFrom(scriptsFolderPath));
        }

        if (Files.exists(schemaFolderPath)) {
            filesList.addAll(getFilesFrom(schemaFolderPath));
        }

        return filesList;
    }

    private static List<VirtualFile> getFilesFrom(Path path) {
        File directory = new File(path.toString());
        Collection<File> files = FileUtils.listFiles(directory, new RegexFileFilter(".*"), DirectoryFileFilter.DIRECTORY);
        List<VirtualFile> virtualFiles = new ArrayList<>();
        for (File file : files) {
            virtualFiles.add(LocalFileSystem.getInstance().findFileByIoFile(file));
        }
        return virtualFiles;
    }

    public static void addFile(VirtualFile virtualFile) throws ParseException, IOException {
        VersionJson.addFile(virtualFile.getPath());
        VGUtils.refreshProjectRootFrom(virtualFile.getPath());
    }

    public static void removeFile(VirtualFile virtualFile) throws IOException, ParseException {
        VersionJson.removeFile(virtualFile.getPath());
        VGUtils.refreshProjectRootFrom(virtualFile.getPath());
    }
}
