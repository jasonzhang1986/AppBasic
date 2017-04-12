package me.jasonzhang.appbase.utils;

import io.reactivex.annotations.NonNull;
import timber.log.Timber;

/**
 * Created by JifengZhang on 2017/4/12.
 */

public class LoggerUtils {
    private static boolean SHOW_LOG = true;
    public static void initialize(boolean show_log) {
        SHOW_LOG = show_log;
        Timber.plant(new Timber.DebugTree());
    }
    public static void i (@NonNull String message, Object... args) {
        if (SHOW_LOG) {
            Timber.i(message, args);
        }
    }
    public static void d (@NonNull String message, Object... args) {
        if (SHOW_LOG) {
            Timber.d(message, args);
        }
    }
    public static void v (@NonNull String message, Object... args) {
        if (SHOW_LOG) {
            Timber.v(message, args);
        }
    }
    public static void e (@NonNull String message, Object... args) {
        Timber.e(message, args);
    }

    public static void i (Throwable t) {
        if (SHOW_LOG) {
            Timber.i(t);
        }
    }

    public static void d (Throwable t) {
        if (SHOW_LOG) {
            Timber.d(t);
        }
    }

    public static void v (Throwable t) {
        if (SHOW_LOG) {
            Timber.v(t);
        }
    }

    public static void e (Throwable t) {
        Timber.e(t);
    }

    public static void i (Throwable t, @NonNull String message, Object... args) {
        if (SHOW_LOG) {
            Timber.i(t, message, args);
        }
    }

    public static void d (Throwable t, @NonNull String message, Object... args) {
        if (SHOW_LOG) {
            Timber.d(t, message, args);
        }
    }

    public static void v (Throwable t, @NonNull String message, Object... args) {
        if (SHOW_LOG) {
            Timber.v(t, message, args);
        }
    }

    public static void e (Throwable t, @NonNull String message, Object... args) {
        Timber.e(t, message, args);
    }

}
