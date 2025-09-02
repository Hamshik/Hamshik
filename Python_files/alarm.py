import sys
from PyQt5.QtWidgets import QApplication, QWidget, QLineEdit, QPushButton, QLabel
from PyQt5.QtCore import QTimer, QTime
from PyQt5.QtGui import QPixmap, QIcon
from datetime import datetime
import pygame

PATH = "C:\\Users\\hp\\Music\\Best Morning Alarm.mp3"
IconPATH = "C:/Users/hp/OneDrive/Pictures/alram.png"
format = "%d-%m-%y %H:%M:%S"

class AlarmClock(QWidget):
    def __init__(self):
        super().__init__()
        self.setWindowTitle("Alarm Clock")
        self.setFixedSize(350, 100)
        pixmap = QPixmap(IconPATH)
        pic = QIcon(pixmap)
        self.setWindowIcon(pic)


        self.input = QLineEdit(self)
        self.input.setGeometry(10, 10, 200, 30)
        self.input.setPlaceholderText("Enter time (dd-mm-yy HH:MM:SS)")
        self.input.setObjectName("input")

        self.button = QPushButton("Enter", self)
        self.button.setGeometry(220, 10, 60, 30)
        self.button.clicked.connect(self.submit)

        self.label = QLabel(self)
        self.label.setGeometry(10, 45, 400, 30)
        self.label.setStyleSheet("font-size:20px; font-family: Algerian;")

        self.inCorrect = QLabel(self)
        self.inCorrect.setGeometry(10, 70, 400, 30)
        self.inCorrect.setStyleSheet("""font-size:20px; font-family: Algerian; color:red;""")

        self.alarm = QLabel(self)
        self.alarm.setGeometry(10, 70, 400, 30)
        self.alarm.setStyleSheet("font-size:19px; font-family: Algerian; color:black")

        self.alarm_time = None
        self.timer = QTimer(self)
        self.timer.timeout.connect(self.check_alarm)
        self.timer.start(1000)  # Start timer immediately to update time

    def submit(self):
        text = self.input.text()
        try:
            self.alarm_time = datetime.strptime(text, format)
            self.alarm.setText(f"Alarm set for {self.alarm_time}")
            self.button.setDisabled(True)
            self.input.setStyleSheet("border-bottom: 2px solid blue;")
            self.inCorrect.clear()  # Clear error message
            self.alarm.clear()
        except ValueError:
            self.input.setStyleSheet("border: 2px solid red;")
            self.inCorrect.setText("Invalid time format!")

    def check_alarm(self):
        current_time = datetime.now().strftime(format)
        self.label.setText(f"Current time: {current_time}")

        if self.alarm_time and datetime.now() >= self.alarm_time:
            try:
                self.label.setText("Alarm triggered!")
                pygame.mixer.init()
                pygame.mixer.music.load(PATH)
                pygame.mixer.music.play()
                self.timer.stop()
            except IOError as e:
                print("Something went wrong:", e)

def main():
    app = QApplication(sys.argv)
    window = AlarmClock()
    window.show()
    sys.exit(app.exec_())

if __name__ == '__main__':
    main()
