# Area Calculator
import sys
from PyQt5.QtWidgets import QApplication, QWidget, QLineEdit, QPushButton, QLabel, QMainWindow
from PyQt5.QtGui import QIcon, QPixmap

IconPATH = "C:/Users/hp/OneDrive/Pictures/Area.png"
class MainWindow(QMainWindow):
    def __init__(self):
        super().__init__()
        self.setWindowTitle("Area Calculator")
        self.setFixedSize(500, 350)
        self.initUI()
        pixmap = QPixmap(IconPATH)
        pic = QIcon(pixmap)
        self.setWindowIcon(pic)

    def initUI(self):
        self.label = QLabel("Enter shape name:", self)
        self.label.setGeometry(30, 20, 180, 35)
        self.label.setStyleSheet("font-size:18px; font-family: Arial; font-weight: bold; color:#222;")

        self.input = QLineEdit(self)
        self.input.setGeometry(210, 20, 180, 35)
        self.input.setPlaceholderText("Type help for list of shapes")
        self.input.setStyleSheet("font-size:16px; font-family: Arial; color:#222; border: 2px solid #0078D7; border-radius: 6px; padding: 4px;")

        self.button = QPushButton("Next", self)
        self.button.setGeometry(400, 20, 70, 35)
        self.button.setStyleSheet("font-size:16px; font-family: Arial; background-color: #0078D7; color: white; border-radius: 6px;")
        self.button.clicked.connect(self.show_shape_inputs)

        self.input_labels = []
        self.input_fields = []

        self.result = QLabel("", self)
        self.result.setGeometry(30, 70, 440, 60)
        self.result.setWordWrap(True)
        self.result.setStyleSheet("font-size:16px; font-family: Arial; color:#222; background: #D3D3D3; border-radius: 6px; padding: 6px;")

        self.inCorrect = QLabel("", self)
        self.inCorrect.setGeometry(30, 290, 440, 30)
        self.inCorrect.setStyleSheet("font-size:15px; font-family: Arial; font-weight: bold; color:#B22222;")
        self.inCorrect.setWordWrap(True)

    def clear_inputs(self):
        for label in self.input_labels:
            label.deleteLater()
        for field in self.input_fields:
            field.deleteLater()
        self.input_labels = []
        self.input_fields = []
        if hasattr(self, 'calc_button'):
            self.calc_button.deleteLater()
            del self.calc_button

    def show_shape_inputs(self):
        self.clear_inputs()
        shape = self.input.text().strip().lower()
        self.result.clear()
        self.inCorrect.clear()
        self.shape = shape
        shape_inputs = {
            "circle": ["Radius"],
            "rectangle": ["Length", "Breadth"],
            "square": ["Length"],
            "triangle": ["Height", "Base"],
            "parallelogram": ["Height", "Base"],
            "trapezium": ["First parallel side", "Second parallel side", "Height"],
            "ellipse": ["Semi-major axis", "Semi-minor axis"],
            "rhombus": ["First diagonal", "Second diagonal"],
            "kite": ["First diagonal", "Second diagonal"],
            "sector": ["Radius", "Angle (degrees)"],
            "segment": ["Radius", "Angle (degrees)"],
            "annulus": ["Outer radius", "Inner radius"],
            "frustum": ["Top base radius", "Bottom base radius", "Height"],
            "cylinder": ["Radius", "Height"],
            "cone": ["Radius", "Height"],
            "sphere": ["Radius"],
            "hemisphere": ["Radius"],
            "cuboida": ["Length", "Breadth", "Height"],
            "cube": ["Side length"],
            "torus": ["Major radius", "Minor radius"]
        }
        if shape in shape_inputs:
            for i, label_text in enumerate(shape_inputs[shape]):
                label = QLabel(label_text + ':', self)
                label.setGeometry(30, 140 + i*40, 170, 32)
                label.setStyleSheet("font-size:18px; font-family: Arial; font-weight: bold; color:#222;")
                label.show()
                field = QLineEdit(self)
                field.setGeometry(210, 140 + i*40, 180, 32)
                field.setStyleSheet("font-size:15px; font-family: Arial; color:#222; border: 1.5px solid #0078D7; border-radius: 5px; padding: 3px;")
                field.show()
                self.input_labels.append(label)
                self.input_fields.append(field)
            # Place the Calculate button below the last input field
            y_pos = 140 + len(shape_inputs[shape]) * 40
            self.calc_button = QPushButton("Calculate", self)
            self.calc_button.setGeometry(210, y_pos, 120, 36)
            self.calc_button.setStyleSheet("font-size:16px; font-family: Arial; background-color: #28A745; color: white; border-radius: 6px;")
            self.calc_button.clicked.connect(self.calculate_area)
            self.calc_button.show()
            self.button.setDisabled(True)
            self.button.setDisabled(True)
            self.button.setStyleSheet("font-size:16px; font-family: #D3D3D3; color:#222; background-color: #D3D3D3; border-radius: 6px;")
            self.input.setStyleSheet("font-size:16px; font-family: #D3D3D3; color:#222; border: 2px solid #D3D3D3; border-radius: 6px; padding: 4px;")
        elif shape == "help":
            self.result.setText("Available shapes: " + ", ".join(shape_inputs.keys()))
        elif shape == "exit":
            self.close()
        elif shape == "":
            self.result.setText("Please enter a shape name.")
        else:
            self.inCorrect.setText("Invalid input")
            self.input.setStyleSheet("font-size:16px; font-family: Arial; color:#222; border: 2px solid red; border-radius: 6px; padding: 4px;")

    def calculate_area(self):
        try:
            values = [float(field.text()) for field in self.input_fields]
        except ValueError:
            self.inCorrect.setText("Please enter valid numbers for all fields.")
            return
        shape = self.shape
        a = None
        if shape == "circle":
            a = 3.14 * (values[0] ** 2)
            self.result.setText(f"Area of circle is {a}")
        elif shape == "rectangle":
            a = values[0] * values[1]
            self.result.setText(f"Area of rectangle is {a}")
        elif shape == "square":
            a = values[0] ** 2
            self.result.setText(f"Area of square is {a}")
        elif shape == "triangle":
            a = 0.5 * values[0] * values[1]
            self.result.setText(f"Area of triangle is {a}")
        elif shape == "parallelogram":
            a = values[0] * values[1]
            self.result.setText(f"Area of parallelogram is {a}")
        elif shape == "trapezium":
            a = 0.5 * (values[0] + values[1]) * values[2]
            self.result.setText(f"Area of trapezium is {a}")
        elif shape == "ellipse":
            a = 3.14 * values[0] * values[1]
            self.result.setText(f"Area of ellipse is {a}")
        elif shape == "rhombus":
            a = 0.5 * values[0] * values[1]
            self.result.setText(f"Area of rhombus is {a}")
        elif shape == "kite":
            a = 0.5 * values[0] * values[1]
            self.result.setText(f"Area of kite is {a}")
        elif shape == "sector":
            a = (3.14 * values[0] * values[0] * values[1]) / 360
            self.result.setText(f"Area of sector is {a}")
        elif shape == "segment":
            a = (3.14 * values[0] * values[0] * (values[1] - (3.14 / 180) * (values[1]))) / 360
            self.result.setText(f"Area of segment is {a}")
        elif shape == "annulus":
            a = 3.14 * (values[0] ** 2 - values[1] ** 2)
            self.result.setText(f"Area of annulus is {a}")
        elif shape == "frustum":
            a = 3.14 * (values[0] + values[1]) * ((values[0] - values[1]) ** 2 + values[2] ** 2) ** 0.5 + 3.14 * (values[0] ** 2 + values[1] ** 2)
            self.result.setText(f"Area of frustum is {a}")
        elif shape == "cylinder":
            a = 2 * 3.14 * values[0] * (values[0] + values[1])
            self.result.setText(f"Area of cylinder is {a}")
        elif shape == "cone":
            a = 3.14 * values[0] * (values[0] + (values[1] ** 2 + values[0] ** 2) ** 0.5)
            self.result.setText(f"Area of cone is {a}")
        elif shape == "sphere":
            a = 4 * 3.14 * values[0] * values[0]
            self.result.setText(f"Area of sphere is {a}")
        elif shape == "hemisphere":
            a = 3 * 3.14 * values[0] * values[0]
            self.result.setText(f"Area of hemisphere is {a}")
        elif shape == "cuboida":
            a = 2 * (values[0] * values[1] + values[1] * values[2] + values[2] * values[0])
            self.result.setText(f"Area of cuboida is {a}")
        elif shape == "cube":
            a = 6 * values[0] * values[0]
            self.result.setText(f"Area of cube is {a}")
        elif shape == "torus":
            a = 4 * 3.14 * 3.14 * values[0] * values[1]
            self.result.setText(f"Area of torus is {a}")
        else:
            self.inCorrect.setText("Invalid input")
            self.input.setStyleSheet("border: 2px DOUBLE red;")
def main():
    app = QApplication(sys.argv)
    window = MainWindow()
    window.show()
    app.exec_()
if __name__ == "__main__":
    main()