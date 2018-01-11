package com.assignment.models;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Client class reprsenting {@link Socket} connected to our server.
 * @author atul.aggarwal
 *
 */
public class ClientModel {

    private Socket client;
    private String clientId;
    private BufferedWriter writer;
    private BufferedReader reader;
    
    /**
     * 
     * @param client
     * @param clientNum
     */
    public ClientModel(Socket client, Long clientNum ){
        this.client = client;
        this.clientId = "CLIENT-"+clientNum;
         
        try {
            this.writer = new BufferedWriter(new PrintWriter(client.getOutputStream()));            
            InputStream is = client.getInputStream();
            this.reader = new BufferedReader(new InputStreamReader(is));
            
        } catch (IOException e) {
            System.err.println("Error occurred while reading data");
            e.printStackTrace();
            try {
                client.close();
            } catch (IOException e1) {
                System.err.println("Error occurred while closing resource");
                e1.printStackTrace();
            }
        }
    }

    public Socket getClient() {
        return client;
    }

    public String getClientId() {
        return clientId;
    }

    public BufferedWriter getWriter() {
        return writer;
    }

    public BufferedReader getReader() {
        return reader;
    }  

}
