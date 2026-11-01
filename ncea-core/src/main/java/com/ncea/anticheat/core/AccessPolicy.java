package com.ncea.anticheat.core;

/** Command permission decisions shared by the Paper adapter and unit tests. */
public final class AccessPolicy {
    private AccessPolicy() {}
    public static boolean canExecute(String subcommand, boolean admin, boolean alerts) {
        return admin || ("alerts".equalsIgnoreCase(subcommand) && alerts);
    }
}
