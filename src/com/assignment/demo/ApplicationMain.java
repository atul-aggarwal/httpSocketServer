package com.assignment.demo;

import com.assignment.models.HttpServerSocket;

/**
 * Main class for Application
 * 
 * @author atul.aggarwal
 *
 */
public class ApplicationMain {
    public static void main(String args[]) {
        int serverPort = Integer.parseInt(args[0]);
        String path = args[1];

        HttpServerSocket httpServerSocket = new HttpServerSocket(serverPort,path);
        httpServerSocket.accept();
    }
}
