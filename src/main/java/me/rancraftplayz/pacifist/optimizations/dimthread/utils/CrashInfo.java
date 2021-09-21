package me.rancraftplayz.pacifist.optimizations.dimthread.utils;

import net.minecraft.CrashReport;
import net.minecraft.ReportedException;
import net.minecraft.server.level.ServerLevel;

/**
 * Original code from DimensionalThreading (https://github.com/WearBlackAllDay/DimensionalThreading)
 * Licensed under the MIT License
 */

public record CrashInfo(ServerLevel world, Throwable throwable) {
    public void crash(String title) {
        CrashReport report = CrashReport.forThrowable(this.throwable, title);
        this.world.fillReportDetails(report);
        throw new ReportedException(report);
    }
}
