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