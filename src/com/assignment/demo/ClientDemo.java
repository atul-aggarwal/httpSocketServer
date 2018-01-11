package com.assignment.demo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URLEncoder;
import java.net.UnknownHostException;

/**
 * Test class representing clients requesting connection to server.
 * 
 * @author atul.aggarwal
 *
 */
public class ClientDemo {

    public static void main(String args[]) throws IOException{
        for(int i = 0 ; i<= 50 ;i++){
            new Thread(()->{  // Create 50 different threads for 50 clients
                try {
                    callServer();
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    protected static void callServer()
            throws UnknownHostException, IOException {
        String data = URLEncoder.encode("key1", "UTF-8") + "=" + URLEncoder.encode("value1", "UTF-8");
        
        InetAddress address = Inet4Address.getLocalHost();        
        Socket s = new Socket(address, 5056);


        
        while(true){
            String path = "/temp";
            BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(s.getOutputStream(), "UTF8"));
            wr.write("POST" + path + " HTTP/1.1\r\n");
            wr.write("Content-Length: " + data.length() + "\r\n");
            wr.write("Content-Type: application/x-www-form-urlencoded\r\n");
            wr.write("\r\n");

            wr.write(data);
            wr.flush();
            
            InputStream is = s.getInputStream(); //Accept request Data
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String toReturn;
            String received;
            
            try{
                // printing date or time as requested by client
                received = reader.readLine(); // Get response from Server
                System.out.println(received);
                
                if(received.equals("HTTP/1.1 200 OK") || received.equals("HTTP/1.1 400 Failed") || received.equals("HTTP/1.1 500 Failed"))
                {
                    System.out.println("Message Received : " + received);
                    System.out.println("Closing Client connection : " + s);
                    s.close();
                    System.out.println("Connection closed");
                    break;
                }
                
            }catch(Exception e){
                
            }
                     
        }
      
    }
    
}
