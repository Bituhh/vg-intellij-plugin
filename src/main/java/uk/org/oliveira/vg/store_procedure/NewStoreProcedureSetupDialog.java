package uk.org.oliveira.vg.store_procedure;

import com.intellij.openapi.actionSystem.ActionToolbarPosition;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.TextFieldWithAutoCompletion;
import com.intellij.ui.TextFieldWithAutoCompletionListProvider;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBRadioButton;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import org.jdesktop.swingx.HorizontalLayout;
import org.jetbrains.annotations.Nullable;
import uk.org.oliveira.vg.VGState;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NewStoreProcedureSetupDialog extends DialogWrapper {
    private final Project project;

    private final JBTextField prefix = new JBTextField();
    private final JBTextField name = new JBTextField();

    private final JBRadioButton volatilityStableRadioButton = new JBRadioButton("Stable");
    private final JBRadioButton volatilityVolatileRadioButton = new JBRadioButton("Volatile");
    private final JBRadioButton volatilityImmutableRadioButton = new JBRadioButton("Immutable");

    private final TextFieldWithAutoCompletion<String> returnType;

    private final List<String> userAuthorizationRoles = new ArrayList<>();
    private final JBList<String> userAuthorizationRolesList = new JBList<>();
    private final ToolbarDecorator userAuthorizationRolesDecorator = ToolbarDecorator.createDecorator(this.userAuthorizationRolesList);

    private final List<String> arguments = new ArrayList<>();
    private final JBList<String> argumentsList = new JBList<>();
    private final ToolbarDecorator argumentsDecorator = ToolbarDecorator.createDecorator(this.argumentsList);

    private final Dimension defaultDimension = new Dimension(335, 30);

    protected NewStoreProcedureSetupDialog(@Nullable Project project) {
        super(project, true);
        this.project = project;

        // Type
        VGState state = VGState.getInstance();
        TextFieldWithAutoCompletionListProvider<String> provider = new TextFieldWithAutoCompletion.StringsCompletionProvider(Arrays.asList(state.typeSuggestions), null);
        this.returnType = new TextFieldWithAutoCompletion<>(project, provider, false, null);

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
                .addLabeledComponent("Arguments", getArgumentsPanel(), true)
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
            return new ValidationInfo("Procedure prefix must end with an 'f'!");
        }
        if (this.name.getText().isEmpty()) {
            return new ValidationInfo("Procedure name must not be empty!");
        }
        if (!this.name.getText().matches("[a-z0-9_]+")) {
            return new ValidationInfo("Procedure name must match regex '[a-z0-9_]+'!");
        }
        return null;
    }

    @Override
    public @Nullable JComponent getPreferredFocusedComponent() {
        return this.prefix;
    }

    private JPanel getFileNameInputPanel() {
        JPanel panel = new JPanel(new HorizontalLayout());
        panel.add(this.prefix);

        JBLabel underscore = new JBLabel("_");
        panel.add(underscore);

        this.name.setColumns(25);
        panel.add(this.name);

        return panel;
    }

    private JPanel getReturnTypePanel() {
        JPanel panel = new JPanel(new HorizontalLayout());
        this.returnType.setPreferredSize(this.defaultDimension);
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
            AuthorizationRoleDialog dialog = new AuthorizationRoleDialog(project);
            if (dialog.showAndGet()) {
                this.userAuthorizationRoles.add(dialog.getRole());
                this.userAuthorizationRolesList.setListData(this.userAuthorizationRoles.toArray(String[]::new));
            }
        }).setRemoveAction(a -> {
            int selectedIndex = this.userAuthorizationRolesList.getSelectedIndex();
            this.userAuthorizationRoles.remove(selectedIndex);
            this.userAuthorizationRolesList.setListData(this.userAuthorizationRoles.toArray(String[]::new));
        }).disableUpDownActions();

        return this.userAuthorizationRolesDecorator.createPanel();
    }

    private JPanel getArgumentsPanel() {
        this.argumentsDecorator.setToolbarPosition(ActionToolbarPosition.RIGHT);
        this.argumentsDecorator.setAddAction(a -> {
            ArgumentsDialog argumentsDialog = new ArgumentsDialog(this.project);

            if (argumentsDialog.showAndGet()) {
                this.arguments.add(argumentsDialog.getName().concat(" ").concat(argumentsDialog.getType()));
                this.argumentsList.setListData(this.arguments.toArray(String[]::new));
            }
        }).setRemoveAction(a -> {
            int selectedIndex = this.argumentsList.getSelectedIndex();
            this.arguments.remove(selectedIndex);
            this.argumentsList.setListData(this.arguments.toArray(String[]::new));
        }).setMoveUpAction(a -> move(Direction.UP)).setMoveDownAction(a -> move(Direction.DOWN));

        return this.argumentsDecorator.createPanel();
    }

    public List<String> getArguments() {
        return this.arguments;
    }

    private enum Direction {
        UP, DOWN
    }

    private void move(Direction direction) {
        int i = this.argumentsList.getSelectedIndex() - 1;
        if (direction == Direction.DOWN) {
            i = this.argumentsList.getSelectedIndex() + 1;
        }
        int j = this.argumentsList.getSelectedIndex();
        String s = this.arguments.get(i);
        this.arguments.set(i, this.arguments.get(j));
        this.arguments.set(j, s);
        this.argumentsList.setListData(this.arguments.toArray(String[]::new));
        this.argumentsList.setSelectedIndex(i);
    }

    public String getStoreProcedurePrefix() {
        return this.prefix.getText();
    }

    public String getStoreProcedureName() {
        return this.name.getText();
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
