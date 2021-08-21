package me.rancraftplayz.pacifist.optimizations.common.config;

import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.NonNull;
import space.vectrix.ignite.api.Blackboard;
import space.vectrix.ignite.api.config.Configuration;

import java.nio.file.Path;

public class PacifistInfo {
    private static final @MonotonicNonNull Path CONFIGS_PATH = Blackboard.getProperty(Blackboard.CONFIG_DIRECTORY_PATH);

    private static @MonotonicNonNull Path PACIFIST_PATH;
    private static Configuration.@MonotonicNonNull Key<PacifistConfig> PACIFIST_CONFIG;

    public static @MonotonicNonNull Path getPacifistPath() {
        if (PacifistInfo.PACIFIST_PATH != null) return PacifistInfo.PACIFIST_PATH;
        if (PacifistInfo.CONFIGS_PATH == null) return null;

        return PacifistInfo.PACIFIST_PATH = PacifistInfo.CONFIGS_PATH.resolve("pacifist-optimizations");
    }

    public static Configuration.@NonNull Key<PacifistConfig> getPacifistConfig() {
        if (PacifistInfo.PACIFIST_CONFIG != null) return PacifistInfo.PACIFIST_CONFIG;

        final Path pacifistPath = PacifistInfo.getPacifistPath();
        if (pacifistPath == null) throw new IllegalStateException("Unable to locate path.");

        return PacifistInfo.PACIFIST_CONFIG = Configuration.key(PacifistConfig.class, pacifistPath.resolve("pacifist-optimizations.conf"));
    }
}
