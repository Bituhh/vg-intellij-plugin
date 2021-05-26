package uk.org.oliveira.vg.store_procedure;

import com.intellij.openapi.actionSystem.ActionToolbarPosition;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBRadioButton;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import org.jdesktop.swingx.HorizontalLayout;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class NewStoreProcedureSetupDialog extends DialogWrapper {

    private final JBTextField prefix = new JBTextField();
    private final JBTextField name = new JBTextField();

    private final JBRadioButton volatilityStableRadioButton = new JBRadioButton("Stable");
    private final JBRadioButton volatilityVolatileRadioButton = new JBRadioButton("Volatile");
    private final JBRadioButton volatilityImmutableRadioButton = new JBRadioButton("Immutable");

    private final JBTextField returnType = new JBTextField("INTEGER");

    private final List<String> userAuthorizationRoles = new ArrayList<>();
    private final JBList<String> userAuthorizationRolesList = new JBList<>();
    private final ToolbarDecorator userAuthorizationRolesDecorator = ToolbarDecorator.createDecorator(this.userAuthorizationRolesList);

    protected NewStoreProcedureSetupDialog(@Nullable Project project) {
        super(project, true);
        init();
        setTitle("New Store Procedure Setup");
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return FormBuilder.createFormBuilder()
                .addLabeledComponent("File name", getFileNameInputPanel(), true)
                .addLabeledComponent("Return type", getReturnTypePanel(), true)
                .addLabeledComponent("Volatility", getVolatilityPanel(), true)
                .addSeparator()
                .addLabeledComponent("User authorization roles", getUserAuthorizationRolesPanel(), true)
                .getPanel();
    }

    @Override
    protected @Nullable ValidationInfo doValidate() {
        if (this.prefix.getText().isEmpty()) {
            return new ValidationInfo("Procedure prefix must not be empty!");
        }
        if (!this.prefix.getText().endsWith("f")) {
            return new ValidationInfo("Procedure prefix must end with 'f'!");
        }
        if (this.name.getText().isEmpty()) {
            return new ValidationInfo("Procedure name must not be empty!");
        }
        if (!this.name.getText().matches("[a-z_]+")) {
            return new ValidationInfo("Procedure name must match regex '[a-z_]+'!");
        }
        return null;
    }

    @Override
    public @Nullable JComponent getPreferredFocusedComponent() {
        return this.prefix;
    }

    private JPanel getFileNameInputPanel() {
        JPanel panel = new JPanel(new HorizontalLayout());
        this.prefix.requestFocus();
        panel.add(this.prefix);

        JBLabel underscore = new JBLabel("_");
        panel.add(underscore);

        this.name.setColumns(25);
        panel.add(this.name);

        return panel;
    }

    private JPanel getReturnTypePanel() {
        JPanel panel = new JPanel(new HorizontalLayout());
        this.returnType.setColumns(30);
        panel.add(this.returnType);
        return panel;
    }

    private JPanel getVolatilityPanel() {
        JPanel panel = new JPanel(new HorizontalLayout());

        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(volatilityStableRadioButton);
        buttonGroup.add(volatilityVolatileRadioButton);
        buttonGroup.add(volatilityImmutableRadioButton);
        volatilityStableRadioButton.setSelected(true);

        panel.add(volatilityStableRadioButton);
        panel.add(volatilityVolatileRadioButton);
        panel.add(volatilityImmutableRadioButton);

        return panel;
    }

    private JPanel getUserAuthorizationRolesPanel() {
        this.userAuthorizationRolesDecorator.setToolbarPosition(ActionToolbarPosition.RIGHT);
        this.userAuthorizationRolesDecorator.setAddAction(a -> {
            Messages.InputDialog inputDialog = new Messages.InputDialog("Please provide a role name?", "Role Name", null, null, null);
            if (inputDialog.showAndGet()) {
                this.userAuthorizationRoles.add(inputDialog.getInputString());
                this.userAuthorizationRolesList.setListData(this.userAuthorizationRoles.toArray(String[]::new));
            }
        }).setRemoveAction(a -> {
            int selectedIndex = this.userAuthorizationRolesList.getSelectedIndex();
            this.userAuthorizationRoles.remove(selectedIndex);
            this.userAuthorizationRolesList.setListData(this.userAuthorizationRoles.toArray(String[]::new));
        }).disableUpDownActions();

        return this.userAuthorizationRolesDecorator.createPanel();
    }

    public String getStoreProcedureName() {
        return this.prefix.getText().concat("_").concat(this.name.getText()).toLowerCase();
    }

    public String getReturnType() {
        return returnType.getText().toUpperCase();
    }

    public String getVolatility() {
        if (volatilityImmutableRadioButton.isSelected()) {
            return volatilityImmutableRadioButton.getText().toUpperCase();
        }
        if (volatilityVolatileRadioButton.isSelected()) {
            return volatilityVolatileRadioButton.getText().toUpperCase();
        }
        return volatilityStableRadioButton.getText().toUpperCase();
    }

    public List<String> getUserAuthorizationRoles() {
        return this.userAuthorizationRoles;
    }
}
