package fileSharing;

import java.util.ArrayList;
import java.util.Scanner;

public class Reader extends Thread{
    private ArrayList<File> files;


    public Reader(ArrayList<File> files) {
        this.files = files;
    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
        String command = "";
        while (true) {
            command = scanner.nextLine();
            if ("exit".equals(command))
                break;
            String[] commandItems = command.split(" ");
            if ("p2p".equals(commandItems[0]) && "-serve".equals(commandItems[1])
                    && "-name".equals(commandItems[2]) && "-path".equals(commandItems[4])) {
                String name = commandItems[3];
                String address = commandItems[5];
                java.io.File file = new java.io.File(address);
                boolean isRepeated = false;
                for (int i = 0; i < files.size(); i++) {
                    if (files.get(i).getName().equals(name) || files.get(i).getAddress().equals(address))
                        isRepeated = true;
                }
                if (isRepeated)
                    System.out.println("Your File added before!");
                else if (file.isFile()) {
                    files.add(new File(name, address));
                    System.out.println("Your File Added Successfully!!");
                } else {
                    System.out.println("Wrong path!");
                }
            } else {
                System.out.println("Wrong syntax!");
            }
        }
        System.exit(1);
    }

    public static File commandReader(String cmd) {
        File file = new File();
        String[] commandItems = cmd.split(" ");
        if ("p2p".equals(commandItems[0]) && "-serve".equals(commandItems[1])
                && "-name".equals(commandItems[2]) && "-path".equals(commandItems[4])) {
            String name = commandItems[3];
            String address = commandItems[5];
            return new File(name, address);
        }
        return null;
    }

    public ArrayList<File> getFiles() {
        return files;
    }

    public void setFiles(ArrayList<File> files) {
        this.files = files;
    }
}
