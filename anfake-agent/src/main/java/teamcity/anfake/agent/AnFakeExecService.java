package teamcity.anfake.agent;

import jetbrains.buildServer.RunBuildException;
import jetbrains.buildServer.agent.BuildFinishedStatus;
import jetbrains.buildServer.agent.BuildRunnerContext;
import jetbrains.buildServer.agent.runner.BuildServiceAdapter;
import jetbrains.buildServer.agent.runner.ProgramCommandLine;
import jetbrains.buildServer.util.StringUtil;
import jetbrains.buildServer.vcs.VcsRootEntry;
import org.jetbrains.annotations.NotNull;

import java.util.*;

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

        List<VcsRootEntry> vcsRoots = getBuild().getVcsRootEntries();
        if (!vcsRoots.isEmpty()) {
            String tfsUri = vcsRoots.get(0).getProperties().get("tfs-url");
            if (!StringUtil.isEmptyOrSpaces(tfsUri)) {
                args.add("Tfs.Uri=" + tfsUri);
            }
        }

        String serverUrl = getConfigParameters().get("teamcity.serverUrl");
        if (!StringUtil.isEmptyOrSpaces(serverUrl)) {
            args.add("TeamCity.Uri=" + serverUrl);
            args.add("TeamCity.BuildId=" + getBuild().getBuildId());
            args.add("TeamCity.BuildTypeId=" + getBuild().getBuildTypeExternalId());
        }

        return createProgramCommandline(executable, args);
    }

    @NotNull
    @Override
    public BuildFinishedStatus getRunResult(int exitCode) {
        switch (exitCode) {
            case 0:
            case 1:
                return BuildFinishedStatus.FINISHED_SUCCESS;
            case 2:
                return BuildFinishedStatus.FINISHED_WITH_PROBLEMS;
            default:
                return super.getRunResult(exitCode);
        }
    }

    private String getScript() throws RunBuildException {
        String script = getRunnerParameters().get("Script");
        if (StringUtil.isEmptyOrSpaces(script)) {
            throw new RunBuildException("Build script not specified.");
        }
        return script;
    }

    private Collection<String> getTargets() throws RunBuildException {
        String targets = getConfigParameters().get("anfake.targets");
        if (StringUtil.isEmptyOrSpaces(targets)) {
            targets = getRunnerParameters().get("Targets");
            if (StringUtil.isEmptyOrSpaces(targets)) {
                throw new RunBuildException("Target not specified.");
            }
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
