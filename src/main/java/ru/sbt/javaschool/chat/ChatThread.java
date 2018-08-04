package ru.sbt.javaschool.chat;

import java.io.*;
import java.net.Socket;
import java.util.Queue;

import static java.lang.System.out;

public class ChatThread extends Thread {
    public static final String JOINED_NEW_USER = "joined new user";
    public static final String USER_LEFT_US = "user left us";
    public static final String SYSTEM = "SYSTEM";
    private final Socket socket;
    private final Queue<Message> messages;
    private final Queue<Message> notifyQueue;

    public ChatThread( Socket client, Queue<Message> messages, Queue<Message> notifyQueue ) {
        this.socket = client;
        this.messages = messages;
        this.notifyQueue = notifyQueue;
    }

    @Override
    public void run() {
        final long id = getId();
        try {
            out.println( id + " Connected client " + socket.getInetAddress().toString() + ":" + socket.getPort() );

            BufferedReader reader = new BufferedReader( new InputStreamReader( socket.getInputStream() ) );
            PrintWriter writer = new PrintWriter( socket.getOutputStream() );

            String login = reader.readLine();
            out.println( id + " login: " + login );
            notifyQueue.add( new Message( SYSTEM, login, JOINED_NEW_USER ) );
            writer.println( "Hello, " + login + "!" );
            writer.flush();

            do {
                ObjectInputStream ois = new ObjectInputStream( socket.getInputStream() );

                Message message = (Message) ois.readObject();
                out.println( id + " Client says " + message );

                if ( "exit".equalsIgnoreCase( message.getText() ) ) break;
                else writer.println( "message received!" );

                messages.add( message );
            } while ( true );

            writer.println( "Good bye!" );
            writer.flush();
            notifyQueue.add( new Message( SYSTEM, login, USER_LEFT_US ) );

        } catch ( IOException e ) {
            e.printStackTrace();
        } catch ( ClassNotFoundException e ) {
            e.printStackTrace();
        } finally {
            out.println( id + " Closing client connection..." );
            if ( socket != null ) {
                try {
                    socket.shutdownOutput();
                    socket.shutdownInput();
                    socket.close();
                    out.println( id + " Closed" );
                } catch ( IOException ioe ) {
                    ioe.printStackTrace();
                }
            }
        }
    }
}
