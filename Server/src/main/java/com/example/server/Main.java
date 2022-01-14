package com.example.server;


/**
 * Main class
 */
public class Main {
    /** Creates new server */
    public static void main(String[] args) {
        try
        {
            //new Server( 4444 );
            new ServerReplay( 4445 );
        }
        
        catch(Exception e )
        {
            System.out.println( e.getMessage() );
        }
    }
}
