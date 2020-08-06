package fileSharing;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class FileInfo {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("PATH : ");
            String input = scanner.nextLine();
            File f = new File(input);
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(f);
                byte[] fileBytes = new byte[(int) f.length()];
                fis.read(fileBytes);
                System.out.println("size : " + fileBytes.length);
                for (int i = 0; i < 50; i++) {
                    System.out.print(fileBytes[i] + "@");
                }
                System.out.print("\n");
                for (int i = fileBytes.length - 50; i < fileBytes.length; i++) {
                    System.out.print(fileBytes[i] + "@");
                }
                System.out.print("\n");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
