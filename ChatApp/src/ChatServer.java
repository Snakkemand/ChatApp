import java.io.*;
import java.net.Socket;
import java.net.ServerSocket;
public class ChatServer {
    private static ServerSocket serverSocket = null;
    private static Socket clientSocket = null;
    private static final int maxClientsCount = 6;
    private static final clientThread[] threads = new clientThread[maxClientsCount];
    public static void main(String args[]) {
        int portNumber = 6969;
        if (args.length < 1) {
            System.out.println("Usage: java MultiThreadChatServerSync <portNumber>\n"
                    + "Now using port number=" + portNumber);
        } else {
            portNumber = Integer.valueOf(args[0]).intValue();
        }
        try {
            serverSocket = new ServerSocket(portNumber);
        } catch (IOException e) {
            System.out.println(e);
        }
        while (true) {
            try {
                clientSocket = serverSocket.accept();
                int i;
                for (i = 0; i < maxClientsCount; i++) {
                    if (threads[i] == null) {
                        (threads[i] = new clientThread(clientSocket, threads)).start();
                        break;
                    }
                }
                if (i == maxClientsCount) {
                    PrintStream outS = new PrintStream(clientSocket.getOutputStream());
                    outS.println("Whoopsie daisy! There are too many people. Try later buddy");
                    outS.close();
                    clientSocket.close();
                }
            } catch (IOException e) {
                System.out.println(e);
            }
        }
    }
}
class clientThread extends Thread {
    private String clientName = null;
    private DataInputStream inS = null;
    private PrintStream outS = null;
    private Socket clientSocket = null;
    private final clientThread[] threads;
    private int maxClientsCount;

    public clientThread(Socket clientSocket, clientThread[] threads) {
        this.clientSocket = clientSocket;
        this.threads = threads;
        maxClientsCount = threads.length;
    }

    public void run() {
        int maxClientsCount = this.maxClientsCount;
        clientThread[] threads = this.threads;
        try {
            inS = new DataInputStream(clientSocket.getInputStream());
            outS = new PrintStream(clientSocket.getOutputStream());
            String name;
            while (true) {
                outS.println("Enter your username.");
                name = inS.readLine().trim();
                if (name.indexOf('@') == -1) {
                    break;
                } else {
                    outS.println("The username should not contain '@' character.");
                }
            }
            outS.println("Welcome " + name
                    + " to the best chat room!.\nTo leave enter /quit in a new line.");
            synchronized (this) {
                for (int i = 0; i < maxClientsCount; i++) {
                    if (threads[i] != null && threads[i] == this) {
                        clientName = "@" + name;
                        break;
                    }
                }
                for (int i = 0; i < maxClientsCount; i++) {
                    if (threads[i] != null && threads[i] != this) {
                        threads[i].outS.println("* A new buddy " + name
                                + " entered the chat room !!! *");
                    }
                }
            }
            while (true) {
                String line = inS.readLine();
                if (line.startsWith("/quit")) {
                    break;
                }
                if (line.startsWith("@")) {
                    String[] words = line.split("\\s", 2);