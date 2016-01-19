package teamcity.anfake.agent;

import jetbrains.buildServer.RunBuildException;
import jetbrains.buildServer.agent.BuildRunnerContext;
import jetbrains.buildServer.util.FileUtil;

import java.io.File;
import java.io.IOException;

public final class AnFake {
    private AnFake() {}

    public static String getExe(BuildRunnerContext ctx) throws RunBuildException {
        File baseDir = ctx.getWorkingDirectory();
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
            anFakePath = FileUtil.normalizeSeparator(anFakePath.substring(start + 7, end + 11));
        } catch (IOException e) {
            throw new RunBuildException("Unable to read anf.cmd", e);
        }

        return new File(baseDir, anFakePath).getAbsolutePath();
    }
}
