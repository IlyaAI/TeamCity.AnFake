package teamcity.anfake.server;

import jetbrains.buildServer.serverSide.InvalidProperty;
import jetbrains.buildServer.serverSide.PropertiesProcessor;
import jetbrains.buildServer.serverSide.RunType;
import jetbrains.buildServer.serverSide.RunTypeRegistry;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * Provides AnFake Workspace runner meta-info.
 *
 * @author IlyaAI 17.06.2015
 */
public final class AnFakeWorkspaceRunType extends RunType {
    private final PluginDescriptor descriptor;

    public AnFakeWorkspaceRunType(
            @NotNull RunTypeRegistry reg,
            @NotNull PluginDescriptor descriptor) {
        this.descriptor = descriptor;
        reg.registerRunType(this);
    }

    @NotNull
    @Override
    public String getType() {
        return "AnFakeWorkspace";
    }

    @NotNull
    @Override
    public String getDisplayName() {
        return "AnFake TFS Workspacer";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Updates TFS workspace from '.workspace' file and downloads all changes";
    }

    @NotNull
    @Override
    public String describeParameters(@NotNull Map<String, String> parameters) {
        return "AnFake.exe [AnFakeExtras]/teamcity-tf.fsx GetSpecific";
    }

    @Override
    public PropertiesProcessor getRunnerPropertiesProcessor() {
        return new PropertiesProcessor() {
            public Collection<InvalidProperty> process(Map<String, String> properties) {
                return Collections.emptyList();
            }
        };
    }

    @Override
    public Map<String, String> getDefaultRunnerProperties() {
        return Collections.emptyMap();
    }

    @Override
    public String getEditRunnerParamsJspFilePath() {
        return null;
    }

    @Override
    public String getViewRunnerParamsJspFilePath() {
        return null;
    }
}
