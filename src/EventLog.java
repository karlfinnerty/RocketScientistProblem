import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;   
import java.io.IOException;
import java.util.Scanner;
import java.time.Instant;

public class EventLog extends Thread{
    EventLog(){
        try {
            File output = new File("output.dat");
            if (output.createNewFile()) {
              System.out.println("Output file created: " + output.getName());
            } else {
              System.out.println("Delete output.dat and start again to avoid overlapping missions");
            }
          } catch (IOException e) {
            System.out.println("File error occurred.");
            e.printStackTrace();
          }
    }

    // Synchronised to prevent interleaving theads read and writing to file
    public synchronized void writeFile(String str) {
        try {
            FileWriter output = new FileWriter("output.dat", true);
            // Time will probably be replaced with our own version 

            Instant time = Instant.now();
            output.write(time + ": " + str + "\n");
            output.close();
          } catch (IOException e) {
            System.out.println("File error occurred.");
            e.printStackTrace();
          }
    }

    public synchronized void readFile(){
        try {
            System.out.println("\n******** PRINTING EVENT LOG **********\n");
            File output = new File("output.dat");
            Scanner scanner = new Scanner(output);
            while (scanner.hasNextLine()) {
              String line = scanner.nextLine();
              System.out.println(line);
            }
            scanner.close();
            System.out.println("\n******* EVENT LOG PRINTED SUCCESSFULLY ********\n");
          } catch (FileNotFoundException e) {
            System.out.println("File error occurred.");
            e.printStackTrace();
          }
    }
}
