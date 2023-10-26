package com.kay.filestorage.persistence;


import org.springframework.context.ApplicationContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.concurrent.Callable;
import java.util.function.Consumer;

public class TxHelper {

    private static TxHelper txHelper;

    protected TxHelper() {
    }

    public static TxHelper getInstance(ApplicationContext ctx, String beanName) {
        synchronized (TxHelper.class) {
            if (txHelper != null) {
                return txHelper;
            }
            Object instance = ctx.getAutowireCapableBeanFactory().applyBeanPostProcessorsAfterInitialization(new TxHelper(), beanName);
            txHelper = (TxHelper) instance;
            return txHelper;
        }
    }

    public static <T> T inTransaction(Callable<T> callable) {
        return txHelper.callInTransaction(callable);
    }

    public static <T> T inNewTransaction(Callable<T> callable) {
        return txHelper.callInNewTransaction(callable);
    }

    public static void beforeCommit(Consumer<Boolean> readOnlyConsumer) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void beforeCommit(boolean readOnly) {
                readOnlyConsumer.accept(readOnly);
            }
        });
    }


    public static void afterCommit(Runnable runnable) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                runnable.run();
            }
        });
    }

    public static void beforeCompletion(Runnable runnable) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void beforeCompletion() {
                runnable.run();
            }
        });
    }

    public static void afterCompletion(Consumer<Integer> statusConsumer) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCompletion(int status) {
                statusConsumer.accept(status);
            }
        });
    }

    @Transactional
    public <T> T callInTransaction(Callable<T> callable) {
        return doCall(callable);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public <T> T callInNewTransaction(Callable<T> callable) {
        return doCall(callable);
    }

    private <T> T doCall(Callable<T> callable) {
        try {
            return callable.call();
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

}
