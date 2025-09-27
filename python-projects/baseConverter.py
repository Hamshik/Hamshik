#base converter in GUI
from PyQt5.QtWidgets import QApplication, QWidget, QLineEdit, QPushButton, QLabel
from PyQt5.QtGui import QIcon, QPixmap
from PyQt5.QtCore import Qt
import sys

IconPATH = "C:/Users/hp/OneDrive/Pictures/baseConverter.png"
class BaseConverter(QWidget):
    def __init__(self):
        super().__init__()
        self.setWindowTitle("Base Converter")
        self.setFixedSize(400, 300)
        pixmap = QPixmap(IconPATH)
        pic = QIcon(pixmap)
        self.setWindowIcon(pic)

        self.input_base = QLabel("Enter vaild No.:", self)
        self.input_base.setGeometry(30, 70, 120, 30)
        self.input_base.setStyleSheet("font-size:16px; font-family: Arial; font-weight: bold; color:#222;")
        self.input_base.setAlignment(Qt.AlignRight | Qt.AlingTop)

        self.baseNo_Input = QLineEdit(self)
        self.baseNo_Input.setGeometry(150, 20, 200, 30)
        self.baseNo_Input.setPlaceholderText("Type number here")
        self.baseNo_Input.setStyleSheet("font-size:16px; font-family: Arial; color:#222; border: 2px solid #0078D7; border-radius: 6px; padding: 4px;")

def main():
    app = QApplication(sys.argv)
    converter = BaseConverter()
    converter.show()
    sys.exit(app.exec_())
if __name__ == "__main__":
    main()