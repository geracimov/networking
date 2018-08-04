package ru.sbt.javaschool.chat;

import ru.sbt.javaschool.ChatBase;

import java.io.*;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.Socket;

import static java.lang.System.console;
import static java.lang.System.out;

public class Client extends ChatBase {

    public static void main( String args[] ) {
        port = ChatBase.getPort( args );

        Socket socket = new Socket();
        try {
            InetSocketAddress target = new InetSocketAddress( Inet4Address.getLocalHost(), port );
            out.println( "Connecting to" + target.toString() );
            socket.connect( target );
            out.println( "Connected. " + socket.toString() );

            PrintWriter writer = new PrintWriter( socket.getOutputStream() );
            BufferedReader reader = new BufferedReader( new InputStreamReader( socket.getInputStream() ) );

            out.println( "enter login: " );
            String login = console().readLine();
            out.println( "login " + login );
            writer.println( login );
            writer.flush();

            String input = reader.readLine();
            out.println( "System: " + input + " Введите сообщение:" );
            do {
                out.println( "Кому:" );
                String user = console().readLine();
                out.println( "Введите сообщение:" );
                String text = console().readLine();
                Message message = Message.builder()
                        .source( login )
                        .target( user )
                        .text( text )
                        .build();

                out.println( "вы ввели " + message );

                ObjectOutputStream oos = new ObjectOutputStream( socket.getOutputStream() );
                oos.writeObject( message );

                //writer.println( message );
                writer.flush();

                if ( "exit".equalsIgnoreCase( text ) )
                    break;
            }
            while ( true );
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }
}
