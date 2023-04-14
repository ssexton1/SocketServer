import java.io.*;
import java.net.*;
import java.util.logging.*;
import java.util.Arrays;

public class TCPClient {
    private static final Logger logger = Logger.getLogger(TCPClient.class.getName());

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: java TCPClient <host> <port> [--loglevel=<level>]");
            System.exit(1);
        }

        String host = args[0];
        int port = Integer.parseInt(args[1]);
        Level logLevel = Level.OFF;
        
        int lastWordParamIndex = args.length;
        
        // Check for command-line flag to set logging level
        if (args.length > 2 && args[args.length - 1].startsWith("--loglevel=")) {
            String levelName = args[args.length - 1].substring(11).toUpperCase();
            logLevel = Level.parse(levelName);
            
            lastWordParamIndex = args.length - 1;
        }

        // Set Logger to print the correct messages
        logger.setUseParentHandlers(false);
        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(Level.ALL);
        logger.addHandler(handler);
        logger.setLevel(logLevel);

        try (Socket socket = new Socket(host, port)) {
            logger.log(Level.INFO, "Connected to server");

            OutputStream out = socket.getOutputStream();
            logger.log(Level.INFO, "Output stream created");

            // Send initial message from command-line arguments
            if ((args.length > 2 && !args[args.length - 1].startsWith("--loglevel=")) || args.length > 3) {
                String msg = String.join(" ", Arrays.copyOfRange(args, 2, lastWordParamIndex));
                out.write((msg + "\n").getBytes());
                logger.log(Level.INFO, "Data sent to server: {0}", msg);
            }

            // Create a new thread for command-line I/O
            Thread thread = new Thread(() -> {
                logger.log(Level.FINE, "Thread started");

                try (BufferedReader in = new BufferedReader(new InputStreamReader(System.in))) {
                    String line;
                    while ((line = in.readLine()) != null) {
                        out.write((line + "\n").getBytes());
                        logger.log(Level.INFO, "Data sent to server: {0}", line);
                    }
                } catch (IOException ex) {
                    logger.log(Level.SEVERE, "Error reading from command-line input", ex);
                } finally {
                    logger.log(Level.FINER, "Command-line thread finished");
                }
            });
            
            thread.start();
          
            InputStream inputStream = socket.getInputStream();
            logger.log(Level.INFO, "Input stream created");

            // Read data from socket until end of stream
            int nextByte = inputStream.read();
            while (nextByte != -1) {
                System.out.write(nextByte);
                nextByte = inputStream.read();
            }
            System.out.println();
            logger.log(Level.INFO, "Server connection closed");
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Error connecting to server", ex);
        } finally {
            logger.log(Level.INFO, "Exiting program");
            System.exit(1);
        }
    }
}
