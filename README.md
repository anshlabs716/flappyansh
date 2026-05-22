# 🐦 Flappy Ansh now with chrome os mac windows and linux support. (Contact me if there is an issue) 

Welcome to **Flappy Ansh**! A Java-based game with custom difficulty levels, secret skins, and atmospheric weather effects.

---

## 🛠️ Prerequisites

Before running the game, make sure you have the Java Development Kit (JDK) installed. You can check your version by running this in your terminal:
```bash
java -version

## 🛠️ Prerequisites

Before running the game, you need to install **Java (JDK 17+)** and **Maven**. Follow the commands for your Operating System below:

### 🪟 Windows
1. **Install Java & Maven** (Using Winget in PowerShell):
   ```powershell
   winget install Eclipse.Temurin.17.JDK
   winget install Apache.Maven

MACOS
/bin/bash -c "$(curl -fsSL [https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh](https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh))"
brew install openjdk@17
brew install maven


DEBIAN BASED DISTROS EG, UBUNTU,MINT AND KALI.
sudo apt update
sudo apt install openjdk-17-jdk maven -y

java -version
mvn -version

HOW TO RUN
# 1. Clone the repository
git clone [https://github.com/YOUR_GITHUB_USERNAME/flappy-ansh.git](https://github.com/YOUR_GITHUB_USERNAME/flappy-ansh.git)

# 2. Go into the project folder
cd flappy-ansh

# 3. Build and run the game using Maven
mvn clean compile exec:java

HOW THE FILE LAYOUT SHOULD BE
flappy-ansh/
├── .gitignore          # Keeps the repo clean
├── pom.xml             # Maven configuration file
├── README.md           # This file!
└── src/
    └── main/
        └── java/
            └── Main.java   # Your game's main entry point

OR U CAN DOWNLOAD A JAVA IDE EG VS CODE, INTELLIJ IDEA AND COPY PASTE THE CODE AND ENJOY.
