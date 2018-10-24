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
        int portNumber = 6969;
        String ip = "172.20.10.12"; // IP we're connected on, otherwise use "localhost"
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
        catch (UnknownHostException e) {
            System.err.println("Don't know about host " + ip);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to the host "
                    + ip);
        }
        if (clientSocket != null && outS != null && inS != null) {
            try {
                new Thread(new ChatClient()).start();
                while (!closed) {
                    outS.println(inputLine.readLine().trim());
                }
                outS.close();
                inS.close();
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("IOException:  " + e);
            } } }
    public void run() {
        String responseLine;
        try {
            while ((responseLine = inS.readLine()) != null) {
                System.out.println(responseLine);
                if (responseLine.indexOf("* Bye") != -1)
                    break;
            }
            closed = true;
        } catch (IOException e) {
            System.err.println("IOException:  " + e);
          }
        }
    }

