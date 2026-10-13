import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

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

                String formattedAlarmTime = alarmDateTime.format(formatter);
                System.out.println("Alarm is set for: " + formattedAlarmTime);
                isCorrectlyFormatted = true;
            } catch (DateTimeParseException e) {
                System.out.printf("Invalid format. Please use (%s %s)\n", formatDate, formatTime);
            }
        }
        
        //Starting new Thread
        RunTime run = new RunTime(alarmDateTime, input);
        Thread thread = new Thread(run);
        thread.start();

        thread.setDaemon(true);
        thread.join();

        // we are not closing the Scanner resources since we pass input to the runTime class
    }
}

class RunTime implements Runnable {
    //Declaring the variables
    private String PATH = "C:\\Users\\hp\\Music\\sunflower-street-drumloop-85bpm-163900.wav";
    private LocalDateTime alarmDateTime;
    private Clip clip;
    private Scanner input;

    // Reciving the argu. from main class
    public RunTime(LocalDateTime alarmDateTime, Scanner input) {
        this.alarmDateTime = alarmDateTime;
        this.input = input;
    }
    
    //Overriding the run meothod to custamize the bahaviur of Thread
    @Override
    public void run() {
        while (!LocalDateTime.now().isAfter(alarmDateTime)) {
            try {
                System.out.print("\r" + LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println("Thread was interrupted!");
            }
        }

        //Stoping the audio if user ppress enter
        playAudio_On();
        System.out.println("Press *Enter* [To stop]");
        input.nextLine();
        playAudio_Off();
    }

    //delclaring the methods
    private void playAudio_On() {
        try {
            File file = new File(PATH);
            AudioInputStream audio = AudioSystem.getAudioInputStream(file);
            clip = AudioSystem.getClip();
            clip.open(audio);
            //TO repeact until user press *enter*
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            System.out.println("Audio playback started.");
        } catch (UnsupportedAudioFileException e) {
            System.out.println("Audio file is not supported.");
        } catch (IOException e) {
            System.out.println("Error reading the audio file.");
        } catch (LineUnavailableException e) {
            System.out.println("Audio line is unavailable.");
        }
    }

    private void playAudio_Off() {
        //Seeing whearther Audio is playing or not if yes stoping
        if (clip != null && clip.isRunning()) {
            clip.stop();
            clip.close();
            System.out.println("Audio playback stopped.");
        } else {
            System.out.println("No audio is currently playing.");
        }
    }
}
