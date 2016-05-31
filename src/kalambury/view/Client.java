package kalambury.view;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

/**
 * Created by rafalbyczek on 31.05.16.
 */

public class Client implements Runnable {
    public ObservableList<String> chatLog;
    private Socket clientSocket;
    private BufferedReader serverToClientReader;
    private PrintWriter clientToServerWriter;
    private String name;

    public Client(String hostName, int portNumber, String name) throws IOException {

        clientSocket = new Socket(hostName, portNumber);
        serverToClientReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        clientToServerWriter = new PrintWriter(clientSocket.getOutputStream(), true);
        chatLog = FXCollections.observableArrayList();
        this.name = name;
        clientToServerWriter.println(name);
    }

    public void writeToServer(String input) {
        if (input != null && input != "") {
            clientToServerWriter.println(name + " : " + input);
        }
    }

    public void run() {
        while (true) {
            try {
                final String inputFromServer = serverToClientReader.readLine();
                Platform.runLater(() -> chatLog.add(inputFromServer));

            } catch (SocketException e) {
                Platform.runLater(() -> chatLog.add("Wewnętrzny błąd servera!"));
                break;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
