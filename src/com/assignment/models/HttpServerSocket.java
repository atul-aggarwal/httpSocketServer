package com.assignment.models;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Wrapper class on {@link ServerSocket}
 * @author atul.aggarwal
 *
 */
public class HttpServerSocket {

    private int port;
    private String path;
    private ServerSocket serverSocket;

    private Long clientCount= 0L;
    
    
    private Manager manager;

    public HttpServerSocket(int port, String path){
        this.port = port;
        this.path = path;
        
        try {
            this.serverSocket = new ServerSocket(this.port);
            manager = new Manager();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
 
    /**
     * Manages client connected to the server.
     * @author atul.aggarwal
     */
    public void accept(){
        System.out.println("Waiting for Connections . . . ");
        try {
            while(true){  //Keep Accepting requests  
                
                Socket client = this.serverSocket.accept();
                ClientModel clientHandler = new ClientModel(client, clientCount++);

                //All the requests will first put into waiting queue
                boolean accepted = manager.acceptRequests(clientHandler);
                if(!accepted){
                    processServerRefusal(clientHandler.getWriter(),client);
                }
                
               
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * In case waitingQueue is full, server will start refusing connections
     * @param bufferedWriter
     * @param clientToRefuse
     */
    private void processServerRefusal(BufferedWriter bufferedWriter, Socket clientToRefuse) {
        try {
            bufferedWriter.write("HTTP/1.1 500 Failed");
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
