package com.assignment.models;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Optional;
import java.util.Queue;

/**
 * Workers are responsible to execute the client request and once done
 * session will be closed.
 * @author atul.aggarwal
 *
 */
public class Worker implements Runnable {
    private Queue<ClientModel> jobs;
    private String workerId;

    Worker(Queue<ClientModel> jobList, int id) {
        this.jobs = jobList;
        this.workerId = "worker-" + id;
    }

    public String getWorkerId() {
        return workerId;
    }

    public void run() {

        // Run indefinitely
        while (true) {

            synchronized (jobs) {

                // Wait till job arrives in queue
                while (jobs.size() == 0) {
                    try {
                        jobs.wait();
                    } catch (InterruptedException e) {
                        return;
                    }
                }

                processJob(jobs.poll());
                jobs.notifyAll();

            }
        }

    }

    /**
     * 
     * @param client
     * @author atul.aggarwal
     */
    private void processJob(ClientModel client) {

        try (BufferedReader dis = client.getReader();
                BufferedWriter writer = client.getWriter();
                Socket clientSoc = client.getClient()) 
        {
            System.out.println("Worker :" + workerId + "; Uploading file for Client :" + client.getClientId());
            
            Thread.sleep(200); // To give ample opportunity to all workers to get work
            
            uploadFile(dis, client);
            String message = "File Uploaded Successfully";
            String error = "200 OK";
            createResponse(writer,error,message);
        } catch (IOException e) {
            try {
                String message = "Error Occured While Uploading file";
                String error = "400 Failed";
                createResponse(client.getWriter(),error,message);
                
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected void createResponse(BufferedWriter writer,String error, String message) throws IOException {
        writer.write("HTTP/1.1 "+error+"\r\n");
        writer.write("content-length: " + message.length() + "\r\n");
        writer.write("Content-Type: application/x-www-form-urlencoded\r\n");
        writer.write("\r\n");
        writer.write(message);
        writer.flush();
    }

    /**
     * Uploads/Save the post data into file
     * @param dis
     * @param client
     */
    private void uploadFile(BufferedReader dis, ClientModel client) {
        Optional<String> postData = Optional.ofNullable(readPostData(dis));
        
        if (postData.isPresent()) {
            File file = new File(client.getClientId() + "-" + System.currentTimeMillis() + ".txt");
            FileWriter writer = null;
            try {
                writer = new FileWriter(file);
                writer.write(postData.get());
                writer.flush();
            } catch (IOException e) {
                try {
                    client.getWriter()
                            .write("Error Occured While Uploading file");
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }finally{
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * Reads post request and retrieves post data from it
     * @param dis
     * @return
     * @author atul.aggarwal
     */
    protected String readPostData(BufferedReader dis) {
        String line;
        try {

            if (dis.readLine().startsWith("POST")) {
                int index;
                int postDatalength = 0;
                String postData = null;
                while ((line = dis.readLine()) != null && line.length() != 0) {
                    if ((index = line.indexOf("Content-Length: ")) > -1) {
                        postDatalength = Integer.valueOf(
                                line.substring(index + 16, line.length()));

                    }
                }
                if (postDatalength > 0) {
                    char[] postDataArray = new char[postDatalength];
                    dis.read(postDataArray, 0, postDatalength);
                    postData = new String(postDataArray);
                }

                return postData;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
