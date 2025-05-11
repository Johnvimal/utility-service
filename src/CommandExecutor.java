import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CommandExecutor {
    public static void main(String[] args) {
        String commandFile = "./tmp/commands.txt";
        String outputFile = "./tmp/sample-output.txt";
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(10);

        try (BufferedReader reader = new BufferedReader(new FileReader(commandFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("*/")) {
                    String[] parts = line.split(" ");
                    String interval = parts[0].substring(2);
                    String command = line.substring(line.indexOf(" ") + 1);
                    scheduleRecurringTask(executor, Integer.parseInt(interval), command, outputFile);
                } else {
                    String[] parts = line.split(" ");
                    String minute = parts[0];
                    String hour = parts[1];
                    String day = parts[2];
                    String month = parts[3];
                    String year = parts[4];
                    String command = line.substring(line.indexOf(year) + year.length() + 1);
                    scheduleOneTimeTask(executor, minute, hour, day, month, year, command, outputFile);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading command file: " + e.getMessage());
        }
    }

    private static void scheduleOneTimeTask(ScheduledExecutorService executor, String minute, String hour, String day, String month, String year, String command, String outputFile) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy MM dd HH mm");
        LocalDateTime scheduledTime = LocalDateTime.of(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day), Integer.parseInt(hour), Integer.parseInt(minute));
        LocalDateTime currentTime = LocalDateTime.now();
        long delay = java.time.Duration.between(currentTime, scheduledTime).toMillis();

        if (delay > 0) {
            executor.schedule(() -> executeCommand(command, outputFile), delay, TimeUnit.MILLISECONDS);
        } else {
            System.out.println("Scheduled time has passed for one-time task: " + command);
        }
    }

    private static void scheduleRecurringTask(ScheduledExecutorService executor, int interval, String command, String outputFile) {
        executor.scheduleAtFixedRate(() -> executeCommand(command, outputFile), 0, interval, TimeUnit.MINUTES);
    }
    
    private static void executeCommand(String command, String outputFile) {
        try {
            Process process = Runtime.getRuntime().exec(command);
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    StringBuilder output = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        output.append(line).append(System.lineSeparator());
                    }
                    try (FileWriter writer = new FileWriter(outputFile, true)) {
                        writer.write(output.toString());
                    }
                }
                System.out.println("Command executed successfully: " + command);
            } else {
                try (FileWriter writer = new FileWriter(outputFile, true)) {
                    writer.write("Error executing command: " + command + ", Exit Code: " + exitCode + System.lineSeparator());
                }
                System.err.println("Error executing command: " + command + ", Exit Code: " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            try (FileWriter writer = new FileWriter(outputFile, true)) {
                writer.write("Error executing command: " + command + ", Error: " + e.getMessage() + System.lineSeparator());
            } catch (IOException ex) {
                System.err.println("Error writing to output file: " + ex.getMessage());
            }
            System.err.println("Error executing command: " + command + ", Error: " + e.getMessage());
        }
    }

}
