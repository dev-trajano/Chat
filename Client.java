import java.io.*;
import java.net.*;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.*;

public class Client {
  public static void main(String[] args) {
    try (Socket socket = new Socket("localhost", 12345)) {
      // Generate RSA key pair
      KeyPair keyPair = RSACipher.generateKeyPair();
      PublicKey publicKey = keyPair.getPublic();
      PrivateKey privateKey = keyPair.getPrivate();

      PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
      BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
      BufferedReader serverReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

      System.out.println("Conectado ao servidor. Digite sua mensagem:");

      Thread serverReaderThread = new Thread(() -> {
        try {
          String encryptedMessage;
          while ((encryptedMessage = serverReader.readLine()) != null) {
            String decryptedMessage = RSACipher.decryptWithPrivateKey(encryptedMessage, privateKey);
            System.out.println("Recebido do servidor: " + decryptedMessage);
          }
        } catch (IOException e) {
          e.printStackTrace();
        } catch (Exception e) {
          e.printStackTrace();
        }
      });
      serverReaderThread.start();

      String userInput;
      while ((userInput = consoleReader.readLine()) != null) {
        if (userInput.equalsIgnoreCase("exit") || userInput.equalsIgnoreCase("sair")) {
          System.out.println("Você saiu do chat.");
          break;
        }

        String encryptedMessage = RSACipher.encryptWithPublicKey(userInput, publicKey);
        writer.println(encryptedMessage);
      }
    } catch (IOException e) {
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
