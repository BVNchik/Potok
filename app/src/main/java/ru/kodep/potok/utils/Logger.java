package ru.kodep.potok.utils;

import ru.kodep.potok.BuildConfig;

/**
 * Created by vlad on 23.03.18
 */

public class Logger {

    @SuppressWarnings("SingleStatementInBlock")
    public static void print(Throwable throwable) {
        if (BuildConfig.DEBUG) {
            throwable.printStackTrace();
        }

    }
}
