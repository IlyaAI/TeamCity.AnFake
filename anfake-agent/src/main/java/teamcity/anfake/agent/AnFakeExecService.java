package teamcity.anfake.agent;

import jetbrains.buildServer.RunBuildException;
import jetbrains.buildServer.agent.BuildRunnerContext;
import jetbrains.buildServer.agent.runner.BuildServiceAdapter;
import jetbrains.buildServer.agent.runner.ProgramCommandLine;
import jetbrains.buildServer.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Executes 'AnFake &lt;target> &lt;properties>' command.
 *
 * @author IlyaAI
 */
public final class AnFakeExecService extends BuildServiceAdapter {
    //private static final Logger Log = Logger.getInstance(AnFakeService.class.getName());

    @NotNull
    @Override
    public ProgramCommandLine makeProgramCommandLine() throws RunBuildException {
        String executable;
        List<String> args = new ArrayList<>();

        BuildRunnerContext ctx = getRunnerContext();
        if (Mono.isEnabled(ctx)) {
            executable = Mono.getJit(ctx);
            args.add(AnFake.getExe(ctx));
        } else {
            executable = AnFake.getExe(ctx);
        }

        args.add(getScript());
        args.addAll(getTargets());
        args.addAll(getProperties());

        return createProgramCommandline(executable, args);
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
