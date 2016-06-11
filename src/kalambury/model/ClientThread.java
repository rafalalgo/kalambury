package kalambury.model;

import javafx.application.Platform;
import kalambury.server.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.regex.Pattern;

/**
 * Created by rafalbyczek on 28.05.16.
 */
public class ClientThread implements Runnable {
    private Socket clientSocket;
    private Server baseServer;
    private BufferedReader incomingMessageReader;
    private PrintWriter outgoingMessageWriter;
    private String clientName;

    public ClientThread(Socket clientSocket, Server baseServer) {
        this.setClientSocket(clientSocket);
        this.baseServer = baseServer;
        try {

            incomingMessageReader = new BufferedReader(new InputStreamReader(
                    clientSocket.getInputStream()));
            outgoingMessageWriter = new PrintWriter(
                    clientSocket.getOutputStream(), true);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public String toString() {
        return "ClientThread{" +
                "clientSocket=" + clientSocket +
                ", baseServer=" + baseServer +
                ", incomingMessageReader=" + incomingMessageReader +
                ", outgoingMessageWriter=" + outgoingMessageWriter +
                ", clientName='" + clientName + '\'' +
                '}';
    }


    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClientThread that = (ClientThread) o;

        if (clientSocket != null ? !clientSocket.equals(that.clientSocket) : that.clientSocket != null) return false;
        if (baseServer != null ? !baseServer.equals(that.baseServer) : that.baseServer != null) return false;
        if (incomingMessageReader != null ? !incomingMessageReader.equals(that.incomingMessageReader) : that.incomingMessageReader != null)
            return false;
        if (outgoingMessageWriter != null ? !outgoingMessageWriter.equals(that.outgoingMessageWriter) : that.outgoingMessageWriter != null)
            return false;
        return clientName != null ? clientName.equals(that.clientName) : that.clientName == null;

    }


    public int hashCode() {
        int result = clientSocket != null ? clientSocket.hashCode() : 0;
        result = 31 * result + (baseServer != null ? baseServer.hashCode() : 0);
        result = 31 * result + (incomingMessageReader != null ? incomingMessageReader.hashCode() : 0);
        result = 31 * result + (outgoingMessageWriter != null ? outgoingMessageWriter.hashCode() : 0);
        result = 31 * result + (clientName != null ? clientName.hashCode() : 0);
        return result;
    }


    public Server getBaseServer() {
        return baseServer;
    }


    public void setBaseServer(Server baseServer) {
        this.baseServer = baseServer;
    }


    public BufferedReader getIncomingMessageReader() {
        return incomingMessageReader;
    }


    public void setIncomingMessageReader(BufferedReader incomingMessageReader) {
        this.incomingMessageReader = incomingMessageReader;
    }


    public PrintWriter getOutgoingMessageWriter() {
        return outgoingMessageWriter;
    }


    public void setOutgoingMessageWriter(PrintWriter outgoingMessageWriter) {
        this.outgoingMessageWriter = outgoingMessageWriter;
    }

    public void run() {
        try {
            this.clientName = getClientNameFromNetwork();
            Platform.runLater(() -> {
                baseServer.clientNames.add(clientName + " - "
                        + clientSocket.getRemoteSocketAddress());
            });
            String inputToServer;
            while (true) {
                inputToServer = incomingMessageReader.readLine();
                baseServer.writeToAllSockets(inputToServer);
            }
        } catch (SocketException e) {
            baseServer.clientDisconnected(this);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void writeToServer(String input) {
        if (input != null && input != "null" && input.length() >= 2) {
            if (Pattern.matches(".*NOWEHASLO.*", input)) {
                baseServer.setWord(input.substring(input.indexOf(' ') + 1));
            } else {
                outgoingMessageWriter.println(input);
            }
        }
    }


    public String getClientNameFromNetwork() throws IOException {
        return incomingMessageReader.readLine();
    }


    public String getClientName() {
        return this.clientName;
    }


    public void setClientName(String clientName) {
        this.clientName = clientName;
    }


    public Socket getClientSocket() {
        return clientSocket;
    }


    public void setClientSocket(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }
}
