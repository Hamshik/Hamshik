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

        self.input_base = QLabel("Enter the Base no. (2-36):", self)
        self.input_base.setGeometry(30, 70, 120, 30)
        self.input_base.setStyleSheet("font-size:16px; font-family: Arial; font-weight: bold; color:#222;")
        self.input_base.setAlignment(Qt.AlignRight)

        self.base_input = QLineEdit(self)
        self.base_input.setGeometry(150, 70, 200, 30)
        self.base_input.setPlaceholderText("Type base no. here")
        self.base_input.setStyleSheet("font-size:16px; font-family: Arial; color:#222; border: 2px solid #0078D7; border-radius: 6px; padding: 4px;")
        self.base_input.setMaxLength(2)

        self.baseNo = QLabel("Enter number:", self)
        self.baseNo.setGeometry(30, 20, 100, 30)
        self.baseNo.setStyleSheet("font-size:16px; font-family: Arial; font-weight: bold; color:#222;")
        self.baseNo.setAlignment(Qt.AlignRight)
        self.baseNo.hide()

        self.baseNo_Input = QLineEdit(self)
        self.baseNo_Input.setGeometry(150, 20, 200, 30)
        self.baseNo_Input.setPlaceholderText("Type number here")
        self.baseNo_Input.setStyleSheet("font-size:16px; font-family: Arial; color:#222; border: 2px solid #0078D7; border-radius: 6px; padding: 4px;")
        self.baseNo_Input.hide()

        self.input = QLineEdit(self)
        self.input.setGeometry(150, 20, 200, 30)
        self.input.setPlaceholderText("Type number here")
        self.input.setStyleSheet("font-size:16px; font-family: Arial; color:#222; border: 2px solid #0078D7; border-radius: 6px; padding: 4px;")

        

def main():
    app = QApplication(sys.argv)
    converter = BaseConverter()
    converter.show()
    sys.exit(app.exec_())
if __name__ == "__main__":
    main()