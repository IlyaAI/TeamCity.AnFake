package teamcity.anfake.agent;

import jetbrains.buildServer.RunBuildException;
import jetbrains.buildServer.agent.runner.BuildServiceAdapter;
import jetbrains.buildServer.agent.runner.ProgramCommandLine;
import jetbrains.buildServer.util.FileUtil;
import jetbrains.buildServer.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Executes 'AnFake &lt;target> &lt;properties>' command.
 *
 * @author IlyaAI
 */
public final class AnFakeService extends BuildServiceAdapter {
    //private static final Logger Log = Logger.getInstance(AnFakeService.class.getName());

    @NotNull
    @Override
    public ProgramCommandLine makeProgramCommandLine() throws RunBuildException {
        String executable;
        List<String> args = new ArrayList<>();

        if ("true".equals(getRunnerParameters().get("Mono"))) {
            executable = getMonoJit();
            args.add(getAnFakeExe());
        } else {
            executable = getAnFakeExe();
        }

        args.add(getScript());
        args.addAll(getTargets());
        args.addAll(getProperties());

        return createProgramCommandline(executable, args);
    }

    private String getMonoJit() throws RunBuildException {
        String monoJit = getConfigParameters().get("Mono");
        if (monoJit == null) {
            throw new RunBuildException("Unable to find config parameter Mono.");
        }
        return monoJit;
    }

    private String getAnFakeExe() throws RunBuildException {
        File baseDir = new File(getWorkingDirectory(), getScript()).getParentFile();
        File anfCmd = new File(baseDir, "anf.cmd");

        if (!anfCmd.exists()) {
            throw new RunBuildException(
                String.format("anf.cmd not found: '%s'", anfCmd.getAbsolutePath()));
        }

        String anFakePath;
        try {
            anFakePath = FileUtil.readText(anfCmd);
            int start = anFakePath.indexOf("\"%~dp0\\");
            int end = anFakePath.indexOf("\\AnFake.exe\"");
            if (start < 0 || end <= start + 7) {
                throw new RunBuildException("Unable to extract AnFake path from anf.cmd");
            }
            anFakePath = anFakePath.substring(start + 7, end + 11);
        } catch (IOException e) {
            throw new RunBuildException("Unable to read anf.cmd", e);
        }

        return new File(baseDir, anFakePath).getAbsolutePath();
    }

    private String getScript() throws RunBuildException {
        String script = getRunnerParameters().get("Script");
        if (StringUtil.isEmptyOrSpaces(script)) {
            throw new RunBuildException("Build script not specified.");
        }
        return script;
    }

    private Collection<String> getTargets() throws RunBuildException {
        String targets = getRunnerParameters().get("Targets");
        if (StringUtil.isEmptyOrSpaces(targets)) {
            throw new RunBuildException("Target not specified.");
        }
        return StringUtil.splitHonorQuotes(targets);
    }

    private Collection<String> getProperties() throws RunBuildException {
        String props = getRunnerParameters().get("Properties");
        if (StringUtil.isEmptyOrSpaces(props)) {
            return Collections.emptyList();
        }
        return StringUtil.splitHonorQuotes(props);
    }
}
