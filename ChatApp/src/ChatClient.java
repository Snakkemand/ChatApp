<<<<<<< HEAD
import java.io.DataInputStream;
import java.io.PrintStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
public class ChatClient implements Runnable {
    private static Socket clientSocket = null;
    private static PrintStream outS = null;
    private static DataInputStream inS = null;
    private static BufferedReader inputLine = null;
    private static boolean closed = false;
    public static void main(String[] args) {
        int portNumber = 4172;
        String ip = "172.20.10.12"; // IP were connected on
        if (args.length < 2) {
            System.out
                    .println(" <host> <portNumber>\n"
                            + "host=" + ip + ", portNumber=" + portNumber);
        } else {
            ip = args[0];
            portNumber = Integer.valueOf(args[1]).intValue();
        }
        try {
            clientSocket = new Socket(ip, portNumber);
            inputLine = new BufferedReader(new InputStreamReader(System.in));
            outS = new PrintStream(clientSocket.getOutputStream());
            inS = new DataInputStream(clientSocket.getInputStream());
        }
    }

