package uk.org.oliveira.vg.store_procedure;

import org.jetbrains.annotations.NotNull;
import uk.org.oliveira.vg.NotificationManager;
import uk.org.oliveira.vg.VGUtils;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class StoreProcedure {

    private static final String storeProcedureTemplatePath = "/templates/store_procedure.sql";
    private String lines = "";
    public String name;

    protected StoreProcedure(@NotNull String prefix, @NotNull String name, @NotNull List<String> arguments, @NotNull String returnType, @NotNull String volatility, @NotNull List<String> roles) throws IOException {
        String fullName = prefix.concat("_").concat(name);
        this.name = fullName.concat(".sql");
        this.lines = VGUtils.getFileLinesFromResource(storeProcedureTemplatePath);
        if (this.lines != null) {
            this.lines = this.lines.replace("{{FUNCTION_NAME}}", fullName);

            String argumentsList = "";
            for (String argument : arguments) {
                argumentsList = argumentsList.concat(", ".concat(argument));
            }
            this.lines = this.lines.replace("{{ARGUMENTS}}", argumentsList);

            this.lines = this.lines.replace("{{RETURN_TYPE}}", returnType);
            this.lines = this.lines.replace("{{VOLATILITY}}", volatility);
            this.lines = this.lines.replace("{{PREFIX}}", prefix);

            String rolesList = "NULL";
            if (roles.size() > 0) {
                rolesList = "'{";
                for (int i = 0; i < roles.size(); i++) {
                    if (roles.size() - 1 == i) {
                        rolesList = rolesList.concat("\"").concat(roles.get(i)).concat("\"");
                    } else {
                        rolesList = rolesList.concat("\"").concat(roles.get(i)).concat("\",");
                    }
                }

                rolesList = rolesList.concat("}'");
            }

            this.lines = this.lines.replace("{{ROLES}}", rolesList);
        } else {
            NotificationManager.notifyError("Unable to open template store procedure file!");
        }
    }

    public String writeFile(String targetPath) throws IOException {
        String target = Path.of(targetPath, this.name).toString();
        FileWriter writer = new FileWriter(target);
        writer.write(this.lines);
        writer.close();
        return target;
    }
}
