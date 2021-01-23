package com.vmware.numbergenerator.service;

import com.vmware.numbergenerator.exception.InvalidRequestException;
import com.vmware.numbergenerator.model.NumberGenerator;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.*;

import static com.vmware.numbergenerator.constants.NumberGeneratorConstants.*;

/*
 * A service class introduced to
 * 1. Retrieve status of a UUID.
 * 2. Retrieve number sequence for a valid UUID.
 * 3. Generate a number sequence for a single number.
 * 4. Generate a bulk number sequence for a list of numbers using thread pool.
 */
@Component
public class NumberGeneratorService {

    // A simple cache to store sequences with UUID instead of in-memory Database
    public static final Map<String, List<String>> cache = new HashMap<>();

    private static final int MAX_EXECUTOR_THREADS = 2;

    public String getStatus(String uuid) {
        if (cache.containsKey(uuid) && !CollectionUtils.isEmpty(cache.get(uuid))) {
            return SUCCESS;
        } else if (cache.containsKey(uuid) && CollectionUtils.isEmpty(cache.get(uuid))) {
            return IN_PROGRESS;
        } else {
            return ERROR;
        }
    }

    public List<String> getResult(String uuid) {
        if (!cache.containsKey(uuid)) {
            throw new InvalidRequestException(INVALID_TASK_ID);
        }

        return cache.get(uuid);
    }

    public String generateNumberSequence(NumberGenerator bean) {

        String uuid = UUID.randomUUID().toString();
        cache.put(uuid, Collections.singletonList(computeGeneratedSequence(bean)));

        return uuid;
    }

    public String generateBulkNumberSequence(List<NumberGenerator> beans) {

        String uuid = UUID.randomUUID().toString();
        List<String> bulkResult = new ArrayList<>();
        List<Future> futures = executeTask(beans);

        for (Future future: futures) {
            try {
                bulkResult.add((String)future.get());
            } catch (InterruptedException e) {
                System.out.println("Exception while starting tasks");
            } catch (ExecutionException e) {
                System.out.println("Exception task execution");
            }
        }

        cache.put(uuid, bulkResult);

        return uuid;
    }

    private String computeGeneratedSequence(NumberGenerator bean) {

        // BigInteger could be used if very large numbers are used
        // as Long can hold only 64 bits
        long goal = Long.parseLong(bean.getGoal());
        long step = Long.parseLong(bean.getStep());

        if (goal < step) {
            throw new InvalidRequestException(INVALID_REQUEST_BODY);
        }

        List<Long> result = new ArrayList<>();
        String sequence = "";

        while (goal >= 0) {
            result.add(goal);
            goal-=step;
        }

        for (int index = 0; index < result.size(); index++) {
            sequence += result.get(index).toString();

            if (index < result.size() - 1)
                sequence += ", ";
        }

        return sequence;
    }

    private List<Future> executeTask(List<NumberGenerator> beans) {
        ExecutorService executorService = Executors.newFixedThreadPool(MAX_EXECUTOR_THREADS);
        List<Future> futureList = new ArrayList<>();

        for (NumberGenerator bean: beans) {
            Callable worker = new WorkerThread(bean);
            futureList.add(executorService.submit(worker));
        }

        executorService.shutdown();
        while(!executorService.isTerminated()) {}

        return futureList;
    }

    class WorkerThread implements Callable {

        NumberGenerator numberGenerator;

        WorkerThread(NumberGenerator numberGenerator) {
            this.numberGenerator = numberGenerator;
        }

        @Override
        public Object call() {
            System.out.println(Thread.currentThread().getName() + " Start -- " + numberGenerator.getGoal());
            return computeGeneratedSequence(numberGenerator);
        }
    }

}
