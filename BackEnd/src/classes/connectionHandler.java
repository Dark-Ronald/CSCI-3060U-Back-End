package classes;

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

    public static void shutdown() {
        cHI.shutdown();
        try {
            cHT.join();
        }
        catch (java.lang.InterruptedException e) {
            //do nothing
        }
    }

    public static void signalFrontEnds() {
        cHI.signalFrontEnds();
    }
}

/*
run method cannot be static, but there is no need for a connectionHandler instance to be
created in any of the other classes, so this class is just used for an instance so that
interaction between other classes can be done through connectionHandler
 */
class connectionHandlerInst implements Runnable {
    private static AtomicBoolean shutdown = new AtomicBoolean();
    private static ArrayList<clientConnectionHandler> connections = new ArrayList<>();

    connectionHandlerInst() {
        shutdown.set(false);
    }

    public void run() {
        while (!shutdown.get()) {
            try {
                ServerSocket serverSocket = new ServerSocket(8080);
                serverSocket.setSoTimeout(1000);
                while (!shutdown.get()) {
                    try {
                        Socket clientSocket = serverSocket.accept();
                        connections.add(new clientConnectionHandler(clientSocket));

                    } catch (java.net.SocketTimeoutException e) {
                        continue;
                    }
                }
            } catch (java.io.IOException e) {
                //do nothing
            }
        }
    }

    public void shutdown() {
        shutdown.set(true);
        for (clientConnectionHandler connection : connections) {
            connection.shutdown();
        }
    }

    public void signalFrontEnds() {
        for (clientConnectionHandler connection : connections) {
            connection.sendSignal();
        }
    }
}

class clientConnectionHandler {
    Thread readerT;
    Thread writerT;
    connectionReader reader;
    connectionWriter writer;

    clientConnectionHandler(Socket socket) {
        reader = new connectionReader(socket);
        readerT = new Thread(reader);
        readerT.start();

        writer = new connectionWriter(socket);
        writerT = new Thread(writer);
        writerT.start();
    }

    public void shutdown() {
        reader.shutdown();
        writer.shutdown();
        try {
            readerT.join();
            writerT.join();
        }
        catch (java.lang.InterruptedException e) {
            //do nothing
        }
    }

    public void sendSignal() {
        writer.sendSignal();
    }
}

class connectionReader implements Runnable {
    private Socket clientSocket;
    private AtomicBoolean shutdown = new AtomicBoolean();

    connectionReader(Socket socket) {
        clientSocket = socket;
        shutdown.set(false);
    }

    public void run() {
        try {
            InputStreamReader reader = new InputStreamReader(clientSocket.getInputStream());
            boolean update = false;
            while (!shutdown.get()) {
                /*
                get the most recent request for an update, and ignore the rest
                 */
                while (reader.ready()) {
                    if (shutdown.get()) {
                        break;
                    }
                    /*
                    data is being used as a signal, so it doesnt matter what the data is
                     */
                    reader.read();
                    update = true;
                }
                if (update) {
                    update = false;
                    /*
                    wakeup is only processed when the program is idle, so if it is currently
                    running then it will finish its current execution
                     */
                    synchronized (main.wakeup) {
                        main.wakeup.set(true);
                        main.wakeup.notify();
                    }
                }
            }
        }
        catch (IOException e) {
            //will end up here if connection is closed, so do nothing
        }
    }

    public void shutdown() {
        shutdown.set(true);
    }
}

class connectionWriter implements Runnable {
    Socket clientSocket;
    private AtomicBoolean shutdown = new AtomicBoolean();
    private AtomicBoolean shutdownOrSignal = new AtomicBoolean(); //only used for signals, value never set, exists for clarity

    connectionWriter(Socket socket) {
        clientSocket = socket;
        shutdown.set(false);
    }

    public void run() {
        try {
            OutputStreamWriter writer = new OutputStreamWriter(clientSocket.getOutputStream());

            while (!shutdown.get()) {
                try {
                    shutdownOrSignal.wait();
                    if (!shutdown.get()) {
                        /*
                        data is used as a signal, so what is written doesnt matter
                         */
                        writer.write(49);
                    }
                }
                catch (java.lang.InterruptedException e) {
                    //do nothing
                }
            }
        }
        catch (IOException e) {

        }
    }

    public void shutdown() {
        shutdown.set(true);
        shutdownOrSignal.notify();
    }

    public void sendSignal() {
        shutdownOrSignal.notify();
    }
}
