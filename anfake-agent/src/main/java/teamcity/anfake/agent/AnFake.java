package teamcity.anfake.agent;

import jetbrains.buildServer.RunBuildException;
import jetbrains.buildServer.agent.BuildRunnerContext;
import jetbrains.buildServer.util.FileUtil;
import jetbrains.buildServer.util.StringUtil;

import java.io.File;
import java.io.IOException;

public final class AnFake {
    private AnFake() {}

    public static File getWrapper(BuildRunnerContext ctx) throws RunBuildException {
        String anfCmd = ctx.getRunnerParameters().get("Wrapper");

        if (StringUtil.isEmptyOrSpaces(anfCmd))
            throw new RunBuildException("Wrapper batch file not specified.");

        File anfFile = new File(ctx.getWorkingDirectory(), anfCmd);
        if (!anfFile.exists()) {
            throw new RunBuildException(
                String.format("Wrapper batch file not found: '%s'", anfFile.getAbsolutePath()));
        }

        return anfFile;
    }

    public static File getExe(File wrapper) throws RunBuildException {
        String anFakePath;
        try {
            anFakePath = FileUtil.readText(wrapper);
            int start = anFakePath.indexOf("\"%~dp0\\");
            int end = anFakePath.indexOf("\\AnFake.exe\"");
            if (start < 0 || end <= start + 7) {
                throw new RunBuildException("Unable to extract AnFake path from anf.cmd");
            }
            anFakePath = FileUtil.normalizeSeparator(anFakePath.substring(start + 7, end + 11));
        } catch (IOException e) {
            throw new RunBuildException("Unable to read anf.cmd", e);
        }

        return new File(wrapper.getParent(), anFakePath);
    }

    public static File getExe(BuildRunnerContext ctx) throws RunBuildException {
        return getExe(getWrapper(ctx));
    }
}
