import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/*
class to handler sockets between front-end instances and the back end, to allow for
communication such as wakeup calls.  Backend acts as server, front ends act as clients
 */
public class connectionHandler {
    private static connectionHandlerInst cHI;
    private static Thread cHT;

    public static void init() {
        cHI = new connectionHandlerInst();
        cHT = new Thread(cHI);
        cHT.start();
    }
}

/*
run method cannot be static, but there is no need for a connectionHandler instance to be
created in any of the other classes, so this class is just used for an instance so that
interaction between other classes can be done through connectionHandler
 */
class connectionHandlerInst implements Runnable {
    private static AtomicBoolean shutdown;

    connectionHandlerInst() {
        shutdown.set(false);
    }

    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(8080);
            serverSocket.setSoTimeout(1000);
            while (!shutdown.get()) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    clientConnectionHandler handler = new clientConnectionHandler(clientSocket);

                }
                catch (java.net.SocketTimeoutException e) {
                    continue;
                }
            }
        }
        catch (java.io.IOException e) {
            System.exit(0);
        }
    }
}

class clientConnectionHandler {
    Thread readerT;
    Thread writerT;
    clientConnectionHandler(Socket socket) {
        connectionReader reader = new connectionReader(socket);
        readerT = new Thread(reader);
        readerT.start();

        connectionWriter writer = new connectionWriter(socket);
        writerT = new Thread(writer);
        writerT.start();
    }
}

class connectionReader implements Runnable {
    private Socket clientSocket;
    private AtomicBoolean shutdown;

    connectionReader(Socket socket) {
        clientSocket = socket;
    }

    public void run() {
        try {
            InputStreamReader reader = new InputStreamReader(clientSocket.getInputStream());

        }
        catch (IOException e) {

        }
    }
}

class connectionWriter implements Runnable {
    Socket clientSocket;

    connectionWriter(Socket socket) {
        clientSocket = socket;
    }

    public void run() {
        try {
            OutputStreamWriter writer = new OutputStreamWriter(clientSocket.getOutputStream());

        }
        catch (IOException e) {

        }
    }
}
