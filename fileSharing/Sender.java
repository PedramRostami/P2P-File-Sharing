package fileSharing;

import sun.misc.IOUtils;
import sun.nio.ch.IOUtil;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Random;

public class Sender {
    private static ArrayList<File> files;
    private static Reader reader;
    private static int port;

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("Sender Created!!");
        files = new ArrayList<>();
        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                files.add(Reader.commandReader(args[i]));
                System.out.println("File Added");
            }
        }
        reader = new Reader(files);
        reader.start();


        while (true) {
            String fileRequest = getFileName();
//            String fileRequest = getFileName1();
            boolean isExist = false;
            for (int i = 0; i < files.size(); i++)
                if (files.get(i).getName().equals(fileRequest)) {
                    isExist = true;
                    System.out.println("File Founded!");
                    Thread.sleep(2000);
                    Config config = findRequest(files.get(i));
                    String sendMessage = receiveSendRequest(config);
                    if ("yes".equals(sendMessage)) {
                        Thread.sleep(2000);
                        System.out.println("start sending...");
                        sender(files.get(i), config);
                        System.out.println("finish sending!");
                    }
                }
            if (!isExist) {
                System.out.println("File Not Founded!");
            }
        }
    }

    public static String getFileName() throws IOException {
        port = 4446;
        byte[] receive = new byte[256];
        MulticastSocket ms = new MulticastSocket(port);
        InetAddress group = InetAddress.getByName("224.0.0.1");
        ms.joinGroup(group);
        DatagramPacket packet = null;
        packet = new DatagramPacket(receive, receive.length);
        ms.receive(packet);
        System.out.println("Sender IP : " + ms.getLocalAddress().getHostName());
        ms.close();
        String fileRequest = convertToString(receive).toString();
        return fileRequest;
    }

    public static String getFileName1() throws IOException {
        port = 4446;
        byte[] receive = new byte[256];
        DatagramSocket datagramSocket = new DatagramSocket(port);
        DatagramPacket packet = null;
        packet = new DatagramPacket(receive, receive.length);
        System.out.println("Socket listening on port " + port);
        System.out.println("Got here");
        datagramSocket.receive(packet);
        System.out.println("Sender IP : " + datagramSocket.getLocalAddress().getHostName());
        datagramSocket.close();
        String fileRequest = convertToString(receive).toString();
        return fileRequest;
    }

    public static String receiveSendRequest(Config config) throws IOException {
        Timeout timeout = new Timeout();
        DatagramSocket ds = new DatagramSocket(config.getPort());
        timeout.setDs(ds);
        byte[] message = new byte[4096];
        DatagramPacket packet = new DatagramPacket(message, message.length);
        try {
            timeout.start();
            ds.receive(packet);
            timeout.terminate();
            String res = convertToString(message).toString();
            ds.close();
            return res;
        } catch (SocketException se) {
            return "no";
        }
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

    private static Config findRequest(File file) throws IOException {
        InetAddress local = InetAddress.getByName("255.255.255.255");
        int port = 1234;
        DatagramSocket ds = new DatagramSocket();
        Config conf = new Config();
        conf.setIp("255.255.255.255");
        Random rand = new Random();
        int confPort = rand.nextInt(9000) + 1000;
        conf.setPort(confPort);
        conf.setFilename(file.getName());
        java.io.File f = new java.io.File(file.getAddress());
        FileInputStream fis = new FileInputStream(f);
        byte[] fileBytes = new byte[(int) f.length()];
        fis.read(fileBytes);
        if (fileBytes.length / Constants.MIN_SIZE < 50) {
            conf.setPacketSize(Constants.MIN_SIZE);
            if (fileBytes.length % Constants.MIN_SIZE == 0)
                conf.setPacketCount(fileBytes.length / Constants.MIN_SIZE);
            else
                conf.setPacketCount((fileBytes.length / Constants.MIN_SIZE) + 1);
        } else if (fileBytes.length / Constants.MID_SIZE < 50) {
            conf.setPacketSize(Constants.MID_SIZE);
            if (fileBytes.length % Constants.MID_SIZE == 0)
                conf.setPacketCount(fileBytes.length / Constants.MID_SIZE);
            else
                conf.setPacketCount((fileBytes.length / Constants.MID_SIZE) + 1);
        } else {
            conf.setPacketSize(Constants.BIG_SIZE);
            if (fileBytes.length % Constants.BIG_SIZE == 0)
                conf.setPacketCount(fileBytes.length / Constants.BIG_SIZE);
            else
                conf.setPacketCount((fileBytes.length / Constants.BIG_SIZE) + 1);
        }
        conf.setLastPacketSize((int) f.length() % conf.getPacketSize());
        byte[] sendPacketByteArrays = new byte[4096];
        sendPacketByteArrays = conf.toString().getBytes();
        DatagramPacket packet = new DatagramPacket(sendPacketByteArrays, sendPacketByteArrays.length, local, port);
        ds.send(packet);
        ds.close();
        return conf;
    }

    private static void sender(File file, Config config) throws IOException, InterruptedException {
        InetAddress address = InetAddress.getByName(config.getIp());
        DatagramSocket ds = new DatagramSocket();
        byte[] part = null;
        java.io.File f = new java.io.File(file.getAddress());
        FileInputStream fis = new FileInputStream(f);
        byte[] fileBytes = new byte[(int) f.length()];
        fis.read(fileBytes);
        for (int i = 0; i < config.getPacketCount(); i++) {
            if (i < config.getPacketCount() - 1) {
                part = new byte[config.getPacketSize() + Constants.OFFSET_SIZE];
                byte[] offset = ByteBuffer.allocate(Constants.OFFSET_SIZE).putInt(i + 1).array();
                for (int j = 0; j < Constants.OFFSET_SIZE; j++)
                    part[j] = offset[j];
                for (int j = Constants.OFFSET_SIZE; j < Constants.OFFSET_SIZE + config.getPacketSize(); j++) {
                    part[j] = fileBytes[j + i * config.getPacketSize() - Constants.OFFSET_SIZE];
                }
                DatagramPacket packet = new DatagramPacket(part, part.length, address, config.getPort());
                Thread.sleep(50);
                ds.send(packet);
                System.out.println(i+"/"+config.getPacketCount());
            } else if (i == config.getPacketCount() - 1) {
                part = new byte[config.getLastPacketSize() + Constants.OFFSET_SIZE];
                byte[] offset = ByteBuffer.allocate(Constants.OFFSET_SIZE).putInt(i + 1).array();
                for (int j = 0; j < Constants.OFFSET_SIZE; j++)
                    part[j] = offset[j];
                for (int j = Constants.OFFSET_SIZE; j < Constants.OFFSET_SIZE + config.getLastPacketSize(); j++) {
                    part[j] = fileBytes[j + i * config.getPacketSize() - Constants.OFFSET_SIZE];
                }
                DatagramPacket packet = new DatagramPacket(part, part.length, address, config.getPort());
                ds.send(packet);
            }
        }
        ds.close();
    }
}
