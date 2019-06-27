package org.arxing.axutils_java;

import com.annimon.stream.Stream;


import java.util.List;

public class MainHelper {

    @SafeVarargs public static void main(PreChecker preChecker,
                                         String defWorkers,
                                         String[] argArray,
                                         Class<? extends IWorker>... workerTypes) throws Exception {
        Args args = Args.of(argArray);
        if (preChecker != null)
            preChecker.preCheckAndSaveEnv(args);
        List<String> methods = args.has("-m") ? args.popValues("-m") : Stream.of(defWorkers.split(" ")).toList();
        for (String method : methods) {
            IWorker worker = searchWorker(method, workerTypes);
            if (worker != null) {
                Logger.println("===> Run worker: %s", worker.name());
                worker.run(args);
            } else {
                Logger.println("===> Worker(%s) not defined!", method);
            }
        }
    }

    public static void main(PreChecker preChecker, String[] argArray, WorkerSearcher customSearcher) throws Exception {
        Args args = Args.of(argArray);
        if (preChecker != null)
            preChecker.preCheckAndSaveEnv(args);
        List<String> methods = args.popValues("-m");
        for (String method : methods) {
            IWorker worker = customSearcher.search(method);
            if (worker != null) {
                Logger.println("===> Run worker: %s", worker.name());
                worker.run(args);
            } else {
                Logger.println("===> Worker(%s) not defined!", method);
            }
        }
    }

    @SafeVarargs private static IWorker searchWorker(String method, Class<? extends IWorker>... workerTypes) throws Exception {
        for (Class<? extends IWorker> workerType : workerTypes) {
            IWorker worker = workerType.newInstance();
            if (worker.name().equals(method))
                return worker;
        }
        return null;
    }

    public interface WorkerSearcher {

        IWorker search(String method) throws Exception;
    }

    public interface PreChecker {

        void preCheckAndSaveEnv(Args args) throws Exception;
    }
}
