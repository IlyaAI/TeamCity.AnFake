package teamcity.anfake.server;

import jetbrains.buildServer.requirements.Requirement;
import jetbrains.buildServer.requirements.RequirementType;
import jetbrains.buildServer.serverSide.InvalidProperty;
import jetbrains.buildServer.serverSide.PropertiesProcessor;
import jetbrains.buildServer.serverSide.RunType;
import jetbrains.buildServer.serverSide.RunTypeRegistry;
import jetbrains.buildServer.util.StringUtil;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Provides AnFake Restore runner meta-info.
 *
 * @author IlyaAI 14.06.2015
 */
public final class AnFakeRestoreRunType extends RunType {
    private final PluginDescriptor descriptor;

    public AnFakeRestoreRunType(
            @NotNull RunTypeRegistry reg,
            @NotNull PluginDescriptor descriptor) {
        this.descriptor = descriptor;
        reg.registerRunType(this);
    }

    @NotNull
    @Override
    public String getType() {
        return "AnFakeRestore";
    }

    @NotNull
    @Override
    public String getDisplayName() {
        return "AnFake Restore";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Restores AnFake package with NuGet";
    }

    @Override
    public PropertiesProcessor getRunnerPropertiesProcessor() {
        return new PropertiesProcessor() {
            public Collection<InvalidProperty> process(Map<String, String> properties) {
                Collection<InvalidProperty> errors = new ArrayList<InvalidProperty>();

                if (StringUtil.isEmptyOrSpaces(properties.get("PackagesConfig"))) {
                    errors.add(new InvalidProperty("PackagesConfig", "packages.config path isn't specified"));
                }

                return errors;
            }
        };
    }

    @Override
    public String getEditRunnerParamsJspFilePath() {
        return descriptor.getPluginResourcesPath("editAnFakeRestore.jsp");
    }

    @Override
    public String getViewRunnerParamsJspFilePath() {
        return descriptor.getPluginResourcesPath("viewAnFakeRestore.jsp");
    }

    @Override
    public Map<String, String> getDefaultRunnerProperties() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("PackagesConfig", ".nuget/packages.config");
        map.put("SolutionDirectory", "");
        map.put("SourceUrl", "https://www.nuget.org/api/v2/");
        map.put("Options", "");

        return map;
    }

    @NotNull
    @Override
    public String describeParameters(@NotNull Map<String, String> parameters) {
        StringBuilder sb = new StringBuilder();
        if ("true".equals(parameters.get("Mono"))) {
            sb.append("mono ");
        }

        sb.append("NuGet.exe restore ");
        sb.append(parameters.get("PackagesConfig"));

        sb.append(" -SolutionDirectory ");
        String solutionDir = parameters.get("SolutionDirectory");
        if (StringUtil.isEmptyOrSpaces(solutionDir)) {
            sb.append('.');
        } else {
            sb.append(solutionDir);
        }

        String sourceUrl = parameters.get("SourceUrl");
        if (!StringUtil.isEmptyOrSpaces(sourceUrl)) {
            sb.append(" -Source ").append(sourceUrl);
        }

        String options = parameters.get("Options");
        if (!StringUtil.isEmptyOrSpaces(options)) {
            sb.append(' ');
            sb.append(options);
        }

        return sb.toString();
    }

    @NotNull
    @Override
    public List<Requirement> getRunnerSpecificRequirements(@NotNull Map<String, String> parameters) {
        List<Requirement> spec = new ArrayList<Requirement>();

        if ("true".equals(parameters.get("Mono"))) {
            spec.add(new Requirement("Mono", null, RequirementType.EXISTS));
        } else {
            spec.add(new Requirement("DotNetFramework4.0_x86", null, RequirementType.EXISTS));
        }

        return spec;
    }
}
