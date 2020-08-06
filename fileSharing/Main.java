package fileSharing;

import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        // for run on localhost please comment line 30 and uncomment line 29 on Receiver.java class
        // for run on localhost please comment line 33 and uncomment line 32 on Sender.java class
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter 1 to run sender\nPress 2 to run receiver\n");
        String cmd = "";
        String[] aaa = new String[]{""};
        cmd = scanner.next();
        if ("1".equals(cmd)) {
            try {
                Sender.main(aaa);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else if ("2".equals(cmd)) {
            try {
                Receiver.main(null);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
