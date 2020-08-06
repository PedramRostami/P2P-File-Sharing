package fileSharing;

import sun.rmi.runtime.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.*;

public class Receiver {
    private static int port;

    public static void main(String[] args) throws IOException, InterruptedException {
        Random random = new Random();

        // need broadcasting
        Scanner scanner = new Scanner(System.in);
        String command = "";
        System.out.println("Receiver Created!!");
        while (!"exit".equals(command)) {
            port = 4446;
            System.out.print("Command: ");
            command = scanner.nextLine();
            String[] commandItems = command.split(" ");
            if ("p2p".equals(commandItems[0]) && "-receive".equals(commandItems[1])) {
                String filename = commandItems[2];
                findRequest(filename);
//                findRequest1(filename);
                Config config = findResponse();
                if (config != null) {
                    sendRequest(config);
                    System.out.println("start receiving...");
                    listener(config);
                    System.out.println("finish receiving!");
                }
            }
        }
    }

    private static void findRequest(String filename) throws IOException {
        InetAddress group = InetAddress.getByName("224.0.0.1");
        DatagramSocket firstMessageSocket = new DatagramSocket();
        firstMessageSocket.setBroadcast(true);
        byte[] buffer = filename.getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, port);
        firstMessageSocket.send(packet);
        firstMessageSocket.close();
    }

    private static void findRequest1(String filename) throws IOException {
        InetAddress group = InetAddress.getByName("255.255.255.255");
        DatagramSocket firstMessageSocket = new DatagramSocket();
        firstMessageSocket.setBroadcast(true);
        byte[] buffer = filename.getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, port);
        firstMessageSocket.send(packet);
        System.out.println("FileName Packet sent to port " + Integer.toString(port));
        firstMessageSocket.close();
    }

    private static Config findResponse() throws IOException {
        int port = 1234;
        DatagramSocket ds = new DatagramSocket(port);
        byte[] message = new byte[4096];
        Timeout timeout = new Timeout();
        timeout.setDs(ds);
        DatagramPacket packet = new DatagramPacket(message, message.length);
        try {
            timeout.start();
            ds.receive(packet);
            timeout.terminate();
            Config config = new Config();
            config.convertFromString(convertToString(message).toString());
            ds.close();
            return config;
        } catch (SocketException se) {
            System.out.println("Not Founded!");
            return null;
        }
    }

    private static void sendRequest(Config config) throws IOException, InterruptedException {
        InetAddress inetAddress = InetAddress.getByName(config.getIp());
        DatagramSocket ds = new DatagramSocket();
        byte[] message = "yes".getBytes();
        DatagramPacket packet = new DatagramPacket(message, message.length, inetAddress, config.getPort());
        Thread.sleep(2000);
        ds.send(packet);
    }

    private static void listener(Config config) throws IOException {
        byte[] fileBytes = new byte[((config.getPacketCount() - 1 ) * config.getPacketSize()) + config.getLastPacketSize()];
        InetAddress address = InetAddress.getByName(config.getIp());
        DatagramSocket ds = new DatagramSocket(config.getPort());
        byte[] parts = null;
        for (int i = 0; i < config.getPacketCount(); i++) {
            parts = new byte[config.getPacketSize() + Constants.OFFSET_SIZE];
            DatagramPacket packet = new DatagramPacket(parts, parts.length);
            ds.receive(packet);
            byte[] offsetBytes = new byte[Constants.OFFSET_SIZE];
            for (int j = 0; j < Constants.OFFSET_SIZE; j++)
                offsetBytes[j] = parts[j];
            int offset = ByteBuffer.wrap(offsetBytes).getInt();
            if (offset < config.getPacketCount()) {
                for (int j = 0; j < config.getPacketSize(); j++)
                    fileBytes[(i * config.getPacketSize()) + j] = parts[Constants.OFFSET_SIZE + j];
            } else {
                for (int j = 0; j < config.getLastPacketSize(); j++) {
                    fileBytes[(i * config.getPacketSize()) + j] = parts[Constants.OFFSET_SIZE + j];
                }
            }


        }
        ds.close();
        File f = new File(config.getFilename());
        FileOutputStream fos = new FileOutputStream(f);
        fos.write(fileBytes);
    }

    public static StringBuilder convertToString(byte[] a) {
        if (a == null)
            return null;
        StringBuilder ret = new StringBuilder();
        int i = 0;
        while (a[i] != 0) {
            ret.append((char) a[i]);
            i++;
        }
        return ret;
    }
}
