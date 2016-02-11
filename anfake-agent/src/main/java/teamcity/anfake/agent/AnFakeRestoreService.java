package teamcity.anfake.agent;

import jetbrains.buildServer.BuildProblemData;
import jetbrains.buildServer.BuildProblemTypes;
import jetbrains.buildServer.RunBuildException;
import jetbrains.buildServer.agent.BuildFinishedStatus;
import jetbrains.buildServer.agent.BuildRunnerContext;
import jetbrains.buildServer.agent.runner.BuildServiceAdapter;
import jetbrains.buildServer.agent.runner.ProgramCommandLine;
import jetbrains.buildServer.util.FileUtil;
import jetbrains.buildServer.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Executes '[mono] NuGet restore &lt;packages-config-path> -SolutionDirectory &lt;solution-directory> -Source &lt;source-url>' command.
 *
 * @author IlyaAI
 */
public final class AnFakeRestoreService extends BuildServiceAdapter {
    @NotNull
    @Override
    public ProgramCommandLine makeProgramCommandLine() throws RunBuildException {
        File executable;
        List<String> args = new ArrayList<String>();

        BuildRunnerContext ctx = getRunnerContext();
        if (Mono.isEnabled(ctx)) {
            executable = Mono.getJit(ctx);
            args.add(getNuGetExe().getPath());
        } else {
            executable = getNuGetExe();
        }

        args.add("restore");
        args.add(getPackagesConfig());
        args.add("-SolutionDirectory");
        args.add(getSolutionDirectory());
        args.add("-Source");
        args.add(getSourceUrl());
        args.add("-NonInteractive");
        args.addAll(getCustomArguments());

        return createProgramCommandline(executable.getPath(), args);
    }

    @NotNull
    @Override
    public BuildFinishedStatus getRunResult(int exitCode) {
        switch (exitCode) {
            case 0:
                return BuildFinishedStatus.FINISHED_SUCCESS;
            default:
                BuildProblemData problemData = BuildProblemData.createBuildProblem(
                    getRunnerContext().getRunType(),
                    BuildProblemTypes.TC_EXIT_CODE_TYPE,
                    "AnFake restore FAILED.");

                getBuild()
                    .getBuildLogger()
                    .logBuildProblem(problemData);

                return BuildFinishedStatus.FINISHED_WITH_PROBLEMS;
        }
    }

    private File getNuGetExe() throws RunBuildException {
        File nugetExe = FileUtil.getCanonicalFile(new File("../plugins/anfake-agent/NuGet.exe"));
        if (!nugetExe.exists()) {
            throw new RunBuildException(
                String.format("NuGet not found. Search path: %s", nugetExe.getAbsolutePath()));
        }
        return nugetExe;
    }

    private String getPackagesConfig() throws RunBuildException {
        String packagesConfig = getRunnerParameters().get("PackagesConfig");
        if (StringUtil.isEmptyOrSpaces(packagesConfig)) {
            throw new RunBuildException("packages.config path not specified.");
        }
        return packagesConfig;
    }

    private String getSolutionDirectory() throws RunBuildException {
        String solutionDirectory = getRunnerParameters().get("SolutionDirectory");
        if (StringUtil.isEmptyOrSpaces(solutionDirectory)) {
            return ".";
        }
        return solutionDirectory;
    }

    private String getSourceUrl() throws RunBuildException {
        String sourceUrl = getRunnerParameters().get("SourceUrl");

        if (!StringUtil.isEmptyOrSpaces(sourceUrl)) {
            return sourceUrl;
        }

        File anFakeSettings;
        String solutionDir = getRunnerParameters().get("SolutionDirectory");
        if (!StringUtil.isEmptyOrSpaces(solutionDir)) {
            anFakeSettings = new File(
                new File(getWorkingDirectory(), solutionDir),
                "AnFake.settings.json");
        } else {
            anFakeSettings = new File(getWorkingDirectory(), "AnFake.settings.json");
        }

        if (anFakeSettings.exists()) {
            try {
                JSONObject settings = new JSONObject(FileUtil.readText(anFakeSettings));
                if (settings.has("NuGet.SourceUrl")) {
                    sourceUrl = settings.getString("NuGet.SourceUrl");
                }
            } catch (IOException e) {
                throw new RunBuildException(
                    String.format("Unable to read settings from '%s'.", anFakeSettings.getAbsolutePath()), e);
            }
        }

        if (StringUtil.isEmptyOrSpaces(sourceUrl)) {
            sourceUrl = "https://www.nuget.org/api/v2/";
        }

        return sourceUrl;
    }

    private List<String> getCustomArguments() {
        List<String> result = new ArrayList<String>();
        String options = getRunnerParameters().get("Options");
        if (!StringUtil.isEmptyOrSpaces(options)) {
            for (String line : options.split("[\\r\\n]+")) {
                line = line.trim();
                if (StringUtil.isEmptyOrSpaces(line)) {
                    continue;
                }
                result.addAll(StringUtil.splitHonorQuotes(line));
            }
        }
        return result;
    }
}
