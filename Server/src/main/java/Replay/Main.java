package Replay;


import com.example.server.Server;

/**
 * Main class
 */
public class Main {
    /** Creates new server */
    public static void main(String[] args) {
        try
        {
            new ServerReplay( 4445 );
        }
        
        catch(Exception e )
        {
            System.out.println( e.getMessage() );
        }
    }
}
