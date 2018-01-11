package com.assignment.models;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Manager has the following responsibilities.
 * <p>
 * 1. Create and manage thread pool
 * <p>
 * 2. Decide if further requests can be accepted from server or not.
 * <p>
 * 3. Make sure, workers get the work as long as waitingQueue has requests
 * pending.
 * 
 * @author atul.aggarwal
 *
 */
public class Manager {
    // Maximum number of working threads
    private static final int WORKER_LIMIT = 5;

    // Max size of Waiting queue
    private static final int MAX_QUEUE_SIZE = 10;

    // Maximum size of Job list
    private static final int MAX_JOB_LIST_SIZE = 10;

    private Worker[] workers = new Worker[WORKER_LIMIT];

    private Queue<ClientModel> jobList = new LinkedList<>();
    private Queue<ClientModel> waitingQueue = new LinkedList<>();

    public Manager() {

        initializeWorkers();

        // Start a seperate thread for Manager, as it needs to continuosly check
        // for waiting queue and job list.
        Thread managerThread = new Thread(() -> assignJobs(), "ManagerThread");
        managerThread.start();
    }

    /**
     * Every worker will be using one Job queue shared by Manager.
     * Manager will put items in the JobQueue and Workers will take jobs from it
     * based on their availability.
     * @author atul.aggarwal
     */
    private void initializeWorkers() {
        for (int i = 0; i < workers.length; i++) {
            workers[i] = new Worker(jobList, i);
            Thread worker = new Thread(workers[i],workers[i].getWorkerId());
            
            worker.start();
        }
    }

    /**
     * Continuously loop through waiting queue and job queue and assign jobs
     * 
     * This method will run in seperate thread and will be managed by Manager
     * itself
     * 
     * @author atul.aggarwal.
     */
    private void assignJobs() {
        while (true) {
            try {
                synchronized (waitingQueue) {
                    while (waitingQueue.isEmpty()) { // No new jobs to process
                        waitingQueue.wait();
                    }
                    synchronized (jobList) {
                        while (jobList.size() < MAX_JOB_LIST_SIZE
                                && !waitingQueue.isEmpty()) {
                            jobList.add(waitingQueue.poll());
                            jobList.notifyAll();
                        }
                        jobList.wait();
                        waitingQueue.notifyAll();
                    }
                }
            } catch (InterruptedException e) {
                System.err.println("assignJobs Interrupted");
            }
        }
    }

    /**
     * Manager will keep accepting request and adding it to the waiting queue.
     * Once queue reach to its limit, manager will stop catering more requests.
     * 
     * @param clientJob
     * @return
     * @author atul.aggarwal
     */
    @SuppressWarnings("finally")
    public boolean acceptRequests(ClientModel clientJob) {

        synchronized (waitingQueue) {
            if (waitingQueue.size() == MAX_QUEUE_SIZE) {
                try {
                    waitingQueue.wait();
                } catch (InterruptedException e) {
                    System.err.println("Accept Requests Interrupted");
                } finally {
                    return false;
                }
            }

            waitingQueue.add(clientJob);
            System.out.println("waiting Queue size :" + waitingQueue.size());
            waitingQueue.notifyAll();
            return true;
        }

    }
}
