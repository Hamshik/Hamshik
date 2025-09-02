import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class AlarmClock {
    public static void main(String[] args) throws InterruptedException {

        //declaring the variable
        Scanner input = new Scanner(System.in);

        String formatDate = "dd-MM-yyyy";
        String formatTime = "HH:mm:ss";
        String format = "dd-MM-yyyy HH:mm:ss";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern(formatDate);
        DateTimeFormatter formatterTime = DateTimeFormatter.ofPattern(formatTime);

        LocalDateTime currentDateTime = LocalDateTime.now();
        String currentFormatted = currentDateTime.format(formatter);
        LocalDateTime DateTime = null;

        LocalDateTime alarmDateTime = null;
        boolean isCorrectlyFormatted = false;

        //checking Wheather The user putted the alram set properly
        while (!isCorrectlyFormatted) {
            System.out.print("Enter the Alarm Time (" + formatTime + "): ");
            String choiceTime = input.nextLine();

            System.out.print("Enter the Alarm Date (" + formatDate + "): ");
            String choiceDate = input.nextLine();

            try {
                LocalDate date = LocalDate.parse(choiceDate, formatterDate);
                LocalTime time = LocalTime.parse(choiceTime, formatterTime);
                alarmDateTime = LocalDateTime.of(date, time);

                DateTime = LocalDateTime.parse(currentFormatted, formatter);

                String formattedAlarmTime = alarmDateTime.format(formatter);
                System.out.println("Alarm is set for: " + formattedAlarmTime);
                isCorrectlyFormatted = true;
            } catch (DateTimeParseException e) {
                System.out.printf("Invalid format. Please use (%s %s)\n", formatDate, formatTime);
            }
        }
        
        //Starting new Thread
        runTime run = new runTime(alarmDateTime, input);
        Thread thread = new Thread(run);
        thread.start();

        // we are not closing the Scanner resources since we pass input to the runTime class
    }
}
