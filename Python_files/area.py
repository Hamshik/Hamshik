# Area Calculator
import sys
from PyQt5.QtWidgets import QApplication, QWidget, QLineEdit, QPushButton, QLabel, QMainWindow
from PyQt5.QtGui import QIcon, QPixmap

IconPATH = "C:/Users/hp/OneDrive/Pictures/alram.png"
class MainWindow(QMainWindow):
    def __init__(self):
        super().__init__()
        self.setWindowTitle("Area Calculator")
        self.setFixedSize(400, 175)
        self.initUI()
        pixmap = QPixmap(IconPATH)
        pic = QIcon(pixmap)
        self.setWindowIcon(pic)

    def initUI(self):
        self.label = QLabel("Enter shape name:", self)
        self.label.setGeometry(10, 10, 150, 30)
        self.label.setStyleSheet("font-size:20px; font-family: Algerian; font-weight: bold; color:black;")

        self.input = QLineEdit(self)
        self.input.setGeometry(160, 10, 150, 30)

        self.button = QPushButton("Next", self)
        self.button.setGeometry(320, 10, 60, 30)
        self.button.clicked.connect(self.show_shape_inputs)
        

        self.input_labels = []
        self.input_fields = []

        self.result = QLabel("", self)
        self.result.setGeometry(10, 50, 380, 80)
        self.result.setWordWrap(True)

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
                label.setGeometry(10, 50 + i*40, 150, 30)
                label.show()
                field = QLineEdit(self)
                field.setGeometry(160, 50 + i*40, 150, 30)
                field.show()
                self.input_labels.append(label)
                self.input_fields.append(field)
            # Place the Calculate button below the last input field
            y_pos = 50 + len(shape_inputs[shape]) * 40
            self.calc_button = QPushButton("Calculate", self)
            self.calc_button.setGeometry(160, y_pos, 100, 30)
            self.calc_button.clicked.connect(self.calculate_area)
            self.calc_button.show()
            self.button.setDisabled(True)
        elif shape == "help":
            self.result.setText("Available shapes: " + ", ".join(shape_inputs.keys()))
        elif shape == "exit":
            self.close()
        elif shape == "":
            self.result.setText("Please enter a shape name.")
        else:
            self.result.setText("Invalid input")

    def calculate_area(self):
        try:
            values = [float(field.text()) for field in self.input_fields]
        except ValueError:
            self.result.setText("Please enter valid numbers for all fields.")
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
            self.result.setText("Invalid input")
def main():
    app = QApplication(sys.argv)
    window = MainWindow()
    window.show()
    app.exec_()
if __name__ == "__main__":
    main()