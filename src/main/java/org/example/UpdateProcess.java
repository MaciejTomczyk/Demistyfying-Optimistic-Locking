package org.example;

import static org.example.App.optimisticLockingAwareUpdate;

public class UpdateProcess implements Runnable{

    private final String threadName;
    private final int idToUpdate;

    public UpdateProcess(int id, String threadName) {
        this.idToUpdate = id;
        this.threadName = threadName;
    }

    @Override
    public void run() {
            try {
                optimisticLockingAwareUpdate(idToUpdate, threadName);
                System.out.println(threadName + " done");
            } catch (OptimisticLockingException e) {
                System.out.println(threadName + "  conflict -> " + e.getMessage());
            } catch (Exception e) {
                System.out.println(threadName + " other error -> " + e.getMessage());
            }
        }
}
