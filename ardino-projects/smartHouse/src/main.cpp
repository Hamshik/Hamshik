#include <WiFi.h>
#include <ESPmDNS.h> // Required so Java can find it via mDNS!

// --- REPLACE WITH YOUR HOME ROUTER CREDENTIALS ---
const char* ssid = "INNOVATION HUB";
const char* password = "prsc$123";

// Set web server port number to 80
WiFiServer server(80);

// Variable to store the HTTP request
String header;

// Most ESP32 DevKits use GPIO 2 for the onboard blue LED
const int ledPin = 2; 
String ledState = "OFF";

void setup() {
  Serial.begin(115200);
  
  // Initialize the output variable as an output
  pinMode(ledPin, OUTPUT);
  digitalWrite(ledPin, LOW);

  // Connect to Wi-Fi network
  Serial.print("Connecting to ");
  Serial.println(ssid);
  WiFi.begin(ssid, password);
  
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  
  // Print local IP address
  Serial.println("");
  Serial.println("WiFi connected.");
  Serial.println("IP address: ");
  Serial.println(WiFi.localIP());

  // Initialize mDNS service so your Java app can discover it automatically
  // This registers the device as "esp32.local"
  if (!MDNS.begin("esp32")) {
    Serial.println("Error setting up MDNS responder!");
  } else {
    Serial.println("mDNS responder started: http://esp32.local");
    // Add service to mDNS so _http._tcp discovery works
    MDNS.addService("http", "tcp", 80);
  }

  // Start the server
  server.begin();
  Serial.println("Web server started.");
}

void loop() {
  WiFiClient client = server.available();   // Listen for incoming clients

  if (client) {                             // If a new client connects,
    Serial.println("New Client Connected.");
    String currentLine = "";                // Make a String to hold incoming data from the client
    
    while (client.connected()) {            // Loop while the client's connected
      if (client.available()) {             // If there's bytes to read from the client,
        char c = client.read();             // Read a byte, then
        Serial.write(c);                    // Print it out the serial monitor
        header += c;
        
        if (c == '\n') {                    // If the byte is a newline character
          if (currentLine.length() == 0) {
            
            // HTTP headers
            client.println("HTTP/1.1 200 OK");
            client.println("Content-type:text/html");
            client.println("Connection: close");
            client.println();
            
            // Turns the GPIO on and off
            if (header.indexOf("GET /led/on") >= 0) {
              Serial.println("GPIO 2 on");
              ledState = "ON";
              digitalWrite(ledPin, HIGH);
            } else if (header.indexOf("GET /led/off") >= 0) {
              Serial.println("GPIO 2 off");
              ledState = "OFF";
              digitalWrite(ledPin, LOW);
            }
            
            // Display the HTML web page
            client.println("<!DOCTYPE html><html>");
            client.println("<head><meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">");
            client.println("<link rel=\"icon\" href=\"data:,\">");
            client.println("<style>html { font-family: Arial; display: inline-block; margin: 0px auto; text-align: center;}");
            client.println("body{max-width: 500px; margin:auto; padding: 50px; background: #f4f4f9;}");
            client.println(".button { background-color: #008CBA; border: none; color: white; padding: 16px 40px;");
            client.println("text-decoration: none; font-size: 24px; margin: 2px; cursor: pointer; border-radius: 8px;}");
            client.println(".button2 {background-color: #555555;}</style></head>");
            
            // Web Page Heading
            client.println("<body><h1>ESP32 Home Network Server</h1>");
            client.println("<p>Onboard LED State: <strong>" + ledState + "</strong></p>");
            
            if (ledState == "OFF") {
              client.println("<p><a href=\"/led/on\"><button class=\"button\">TURN ON</button></a></p>");
            } else {
              client.println("<p><a href=\"/led/off\"><button class=\"button button2\">TURN OFF</button></a></p>");
            }
            
            client.println("</body></html>");
            client.println();
            break;
          } else {
            currentLine = "";
          }
        } else if (c != '\r') {
          currentLine += c;
        }
      }
    }
    header = "";
    client.stop();
    Serial.println("Client disconnected.");
    Serial.println("--------------------");
  }
}