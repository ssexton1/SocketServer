import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class QuoteOfTheDayServer {

    private static final int PORT = 17;
    private static final int MAX_PACKET_SIZE = 512;
    private static ExecutorService exec = Executors.newCachedThreadPool();;

    public static void handleTCPRequest(ServerSocket tcpServer) {
         try {
            Socket tcpSocket;
            while ((tcpSocket = tcpServer.accept()) != null) {
               System.out.println("INCOMING TCP CLIENT: " + tcpSocket.getInetAddress().getHostAddress() + ":" + tcpSocket.getPort());
                OutputStream out = tcpSocket.getOutputStream();
                String quote = getRandomQuote();
                byte[] quoteBytes = quote.getBytes();
                out.write(quoteBytes);
                tcpSocket.shutdownOutput();
           }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void handleUDPRequest(DatagramSocket udpServer) {
        try {
            while(true) {
               byte[] buffer = new byte[MAX_PACKET_SIZE];
               DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
               udpServer.receive(packet);
   
               System.out.println("INCOMING UDP CLIENT: " + packet.getAddress().getHostAddress() + ":" + packet.getPort());
   
               String quote = getRandomQuote();
               byte[] quoteBytes = quote.getBytes();
               DatagramPacket quotePacket = new DatagramPacket(quoteBytes, quoteBytes.length, packet.getAddress(), packet.getPort());
               udpServer.send(quotePacket);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public static String getRandomQuote() {

        String[] quotes = {
            "\"If you are what you eat, then I only want to eat the good stuff.\" - Remy",
            "\"[Humans] don't just survive; they discover; they create. ... I mean, just look at what they do with food!\" – Remy",
            "\"Anyone can cook, but only the fearless can be great.\" – Chef Auguste Gusteau",
            "\"All this cooking and reading and TV-watching while we cook and read. It's like you're involving me in crime!\" – Emile",
            "\"I can't believe it! A real gourmet kitchen, and I get to watch!\" – Remy",
            "\"Haute cuisine is an antiquated hierarchy built upon rules written by stupid old men, rules designed to make it impossible for wom­en to enter this world.\" – Sous chef Colette",
            "\"People think haute cuisine is snooooty, so chef must also be snooty. But not so.\" – Sous chef Colette",
            "\"Not everyone can become a great artist, but a great artist can come from anywhere.\" – Food critic Anton Ego",
            "\"The bitter truth we critics must face is that, in the grand scheme of things, the average piece of junk is more meaningful than our criticism designating it so.\" – Anton Ego",
            "\"I just want you to feel you’re doing well. I hate for people to die embarrassed.\" - Fezzik",
            "\"You mean, you put down your rock and I put down my sword, and we try kill each other like civilized people?\" - Westley",
            "\"Please consider me as an alternative to suicide.\" - Prince Humperdinck",
            "\"I have been in the revenge business so long. Now that it’s over, I do not know what to do with the rest of my life.\" - Inigo Montoya",
            "\"A few more steps and we’ll be safe in the Fire Swamp!\" - Westley",
            "\"Good night, Westley. Good work. Sleep well. I’ll most likely kill you in the morning.\"",
            "\"Learn to live with disappointment.\" - Westley",
            "\"Careful. People in masks cannot be trusted.\" - Fezzik", 
            "\"There’s a shortage of perfect breasts in this world. It would be a pity to damage yours.\" - Westly"
        };
        
        String randomQuote = quotes[(int) (Math.random() * quotes.length)];
        return randomQuote + "\n";
   }
   
    public static void main(String[] args) {
    
     try {
        ServerSocket tcpServer = new ServerSocket(PORT);
        DatagramSocket udpServer = new DatagramSocket(PORT);
   
        Runnable tcpTask = () -> handleTCPRequest(tcpServer);
        Runnable udpTask = () -> handleUDPRequest(udpServer);

        System.out.println("Server Starting...");
   
        exec.submit(tcpTask);
        exec.submit(udpTask);

     } catch (IOException ex) {
         ex.printStackTrace();
     }
   }

}
