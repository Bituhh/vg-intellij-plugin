package uk.org.oliveira.vg;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(
        name = "uk.org.oliveira.vg.VGState",
        storages = {@Storage("VGPlugin.xml")}
)
public class VGState implements PersistentStateComponent<VGState> {
    public String[] typeSuggestions = {"BOOLEAN", "INTEGER", "TEXT", "JSON", "JSONB", "CHARACTER VARYING", "CHARACTER"};
    public String[] rolesSuggestions = {};

    public static VGState getInstance() {
        return ServiceManager.getService(VGState.class);
    }

    @Override
    public @Nullable VGState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull VGState state) {
        XmlSerializerUtil.copyBean(state, this);
    }
}
