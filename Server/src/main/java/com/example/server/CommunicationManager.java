package com.example.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

class CommunicationManager
{
    private Socket socket;
    public BufferedReader in;
    public PrintWriter out;

    CommunicationManager(Socket s) throws Exception
    {
        socket = s;
        try
        {
            in = new BufferedReader( new InputStreamReader( socket.getInputStream() ) );
            out = new PrintWriter( socket.getOutputStream(), true );
        }
        catch( Exception e )
        {
            throw new Exception( "Nie można nawiązać połączenia z graczem" );
        }
    }

    /**
     * Wczytuje komunikat z gniazda
     * @throws Exception utracono połączanie z klientem
     */
    String readLine() throws Exception
    {
        return in.readLine();
    }

    /**
     * Wysyła komunikat do podłączonego gniazda
     */
    void writeLine( String line )
    {
        out.println( line );
    }
}
