/*
 * Created by Konstantin Tskhovrebov (aka @terrakok)
 */

package ru.terrakok.cicerone;

import android.os.Handler;
import android.os.Looper;

import java.util.LinkedList;
import java.util.Queue;

import ru.terrakok.cicerone.commands.Command;

/**
 * Passes navigation command to an active {@link Navigator}
 * or stores it in the pending commands queue to pass it later.
 */
class CommandBuffer implements NavigatorHolder {
    private Navigator navigator;
    private final Queue<Command[]> pendingCommands = new LinkedList<>();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    public void setNavigator(Navigator navigator) {
        this.navigator = navigator;
        while (!pendingCommands.isEmpty()) {
            if (navigator != null) {
                navigator.applyCommands(pendingCommands.poll());
            } else break;
        }
    }

    @Override
    public void removeNavigator() {
        this.navigator = null;
    }

    /**
     * Passes {@code commands} to the {@link Navigator} if it available.
     * Else puts it to the pending commands queue to pass it later.
     *
     * @param commands navigation command array
     */
    void executeCommands(final Command[] commands) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (navigator != null) {
                    navigator.applyCommands(commands);
                } else {
                    pendingCommands.add(commands);
                }
            }
        });
    }
}
