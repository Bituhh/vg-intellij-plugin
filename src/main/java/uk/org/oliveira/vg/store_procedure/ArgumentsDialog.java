package uk.org.oliveira.vg.store_procedure;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import org.jdesktop.swingx.HorizontalLayout;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class ArgumentsDialog extends DialogWrapper {

    private final JBTextField name = new JBTextField();
    private final JBTextField type = new JBTextField();


    protected ArgumentsDialog(@Nullable Project project) {
        super(project, true);
        init();
        setTitle("Argument Details");
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return FormBuilder.createFormBuilder()
                .addLabeledComponent("Name", getNamePanel(), true)
                .addLabeledComponent("Type", getTypePanel(), true)
                .getPanel();
    }


    @Override
    protected @Nullable ValidationInfo doValidate() {
        if (!this.name.getText().startsWith("i")) {
            return new ValidationInfo("Argument name must start with 'i'!");
        }
        if (this.name.getText().isEmpty()) {
            return new ValidationInfo("Argument name must not be empty!");
        }
        if (this.type.getText().isEmpty()) {
            return new ValidationInfo("Argument type must not be empty!");
        }
        return null;
    }

    @Override
    public @Nullable JComponent getPreferredFocusedComponent() {
        return this.name;
    }

    private JPanel getNamePanel() {
        JPanel panel = new JPanel(new HorizontalLayout());
        this.name.setColumns(25);
        panel.add(this.name);
        return panel;
    }

    private JPanel getTypePanel() {
        JPanel panel = new JPanel(new HorizontalLayout());
        this.type.setColumns(25);
        panel.add(this.type);
        return panel;
    }

    public String getName() {
        return this.name.getText().toLowerCase();
    }

    public String getType() {
        return this.type.getText().toUpperCase();
    }
}
