import java.io.*;
import java.net.*;
import java.security.*;
import java.util.*;

public class Server {
  private static List<PrintWriter> clientWriters = new ArrayList<>();

  public static void main(String[] args) {
    System.out.println("Chat Server iniciado.");

    try (ServerSocket serverSocket = new ServerSocket(12345)) {
      while (true) {
        new ClientHandler(serverSocket.accept()).start();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static class ClientHandler extends Thread {
    private Socket socket;

    public ClientHandler(Socket socket) {
      this.socket = socket;
    }

    public void run() {
      try {
        PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
        clientWriters.add(writer);

        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String encryptedMessage;

        while ((encryptedMessage = reader.readLine()) != null) {
          System.out.println("Recebido: " + encryptedMessage);
          broadcast(encryptedMessage);
        }
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        clientWriters.remove(socket);
        try {
          socket.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }

    private void broadcast(String message) {
      for (PrintWriter writer : clientWriters) {
        writer.println(message);
      }
    }
  }
}
