package uk.org.oliveira.vg.store_procedure;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.TextFieldWithAutoCompletion;
import com.intellij.ui.TextFieldWithAutoCompletionListProvider;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.Nullable;
import uk.org.oliveira.vg.VGState;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AuthorizationRoleDialog extends DialogWrapper {

    private final TextFieldWithAutoCompletion<String> role;
    private final Dimension defaultDimension = new Dimension(300, 30);

    protected AuthorizationRoleDialog(@Nullable Project project) {
        super(project, true);

        // Roles
        VGState state = VGState.getInstance();
        TextFieldWithAutoCompletionListProvider<String> provider = new TextFieldWithAutoCompletion.StringsCompletionProvider(Arrays.asList(state.rolesSuggestions), null);
        this.role = new TextFieldWithAutoCompletion<>(project, provider, false, null);

        init();
        setTitle("Role Name");
    }


    @Override
    protected @Nullable ValidationInfo doValidate() {
        if (this.role.getText().isEmpty()) {
            return new ValidationInfo("Role name must not be empty!");
        }
        return null;
    }


    @Override
    protected @Nullable JComponent createCenterPanel() {
        return FormBuilder.createFormBuilder()
                .addLabeledComponent("Role", getRolePanel(), true)
                .getPanel();
    }

    private JPanel getRolePanel() {
        JPanel panel = new JPanel();
        this.role.setPreferredSize(this.defaultDimension);
        panel.add(this.role);
        return panel;
    }

    public String getRole() {
        VGState state = VGState.getInstance();
        List<String> tempList = new ArrayList<>(Arrays.asList(state.rolesSuggestions));
        if (!tempList.contains(this.role.getText())) {
            tempList.add(this.role.getText());
            state.rolesSuggestions = tempList.toArray(String[]::new);
        }

        return this.role.getText();
    }
}
