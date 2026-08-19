<img width="1360" height="719" alt="Screenshot_20260819_182803" src="https://github.com/user-attachments/assets/d58fe72b-0d02-4fcb-b36b-49814e2e743f" />
<img width="1360" height="719" alt="Screenshot_20260819_182803" src="https://github.com/user-attachments/assets/44c04f8d-ab27-4fd6-bbc2-3920a24bdb63" />
# 🐦 Flappy Ansh

<div align="center">

### 🎮 A Java-Based Flappy Bird Clone

**Custom difficulty • Secret skins • Power-ups • Particle effects • Weather effects • Windows • Linux • macOS • ChromeOS**

[![Java](https://img.shields.io/badge/Java-21%2B-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.java.com/)
[![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)](https://maven.apache.org/)
[![Platform](https://img.shields.io/badge/Platform-Windows%20%7C%20macOS%20%7C%20Linux%20%7C%20ChromeOS-lightgrey?style=for-the-badge)](https://github.com/anshlabs716/flappyansh)



<img width="1360" height="719" alt="Screenshot_20260819_182803" src="https://github.com/user-attachments/assets/3316f691-249e-4861-a2c8-0b24b842561d" />

</div>

---

## 🐦 About

**Flappy Ansh** is a Java-based Flappy Bird-inspired game with custom difficulty levels, secret skins, power-ups, particle effects, and atmospheric weather effects.

The project includes both a Java source version and a Windows `.exe`.

The Java version is designed for:

- 🪟 Windows
- 🍎 macOS
- 🐧 Linux
- 💻 ChromeOS

---

## ✨ Features

- 🐦 Flappy Bird-style gameplay
- 🎚️ Custom difficulty levels
- 🎨 Secret skins
- ⚡ Power-up mechanics
- ✨ Particle effects
- 🌦️ Atmospheric weather effects
- 🖥️ Windows executable
- ☕ Java source version
- 🧩 Maven support
- 🌍 Cross-platform Java support

---

## 🖥️ Platform Support

| Platform | Support |
|---|---|
| 🪟 Windows | ✅ Supported |
| 🍎 macOS | ✅ Java version |
| 🐧 Linux | ✅ Java version |
| 💻 ChromeOS | ✅ Java version |

> ⚠️ Compatibility may vary depending on the operating system, Java installation, and system configuration.

---

## ☕ Requirements

### Java Source

The raw Java source requires:

- **JDK 21+**

Check your Java version:

~~~~bash
java -version
~~~~

### Windows `.exe`

The Windows executable requires:

- **JRE 21+**

### Maven

Maven is required when building and running the project through the Maven workflow.

Check Maven:

~~~~bash
mvn -version
~~~~

---

## 📦 Installation

### 🪟 Windows

Install Java and Maven using Winget in PowerShell:

~~~~powershell
winget install Eclipse.Temurin.21.JDK
winget install Apache.Maven
~~~~

### 🍎 macOS

Using Homebrew:

~~~~bash
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
brew install openjdk@21
brew install maven
~~~~

### 🐧 Debian-Based Linux

For Ubuntu, Linux Mint, Kali, and other Debian-based distributions:

~~~~bash
sudo apt update
sudo apt install openjdk-21-jdk maven -y
~~~~

Verify the installation:

~~~~bash
java -version
mvn -version
~~~~

---

## 🚀 Run From Source

Clone the repository:

~~~~bash
git clone https://github.com/anshlabs716/flappyansh.git
cd flappyansh
~~~~

Build and run with Maven:

~~~~bash
mvn clean compile exec:java
~~~~

---

## 🪟 Windows Executable

The repository includes:

~~~~text
FlappyAnsh.exe
~~~~

Launch the executable on Windows to play without manually compiling the Java source.

### ⚠️ Windows SmartScreen

Because the executable is not digitally signed, Windows SmartScreen may display a warning.

If Windows blocks the executable:

1. Select **More info**
2. Select **Run anyway**

---

## 🧑‍💻 IDE Support

You can also open the Java project using:

- VS Code
- IntelliJ IDEA
- Eclipse
- Another Java-compatible IDE

Install **JDK 21+**, open the project, and run the game's main Java class.

---

## ⚡ Flappy Ansh Pro

The repository also contains an enhanced **Flappy Ansh Pro** implementation.

It includes additional improvements such as:

- ⚡ Power-up mechanics
- ✨ Improved particle effects
- 🎚️ Adjusted difficulty
- 🎨 Additional visual effects
- 🧩 Additional game classes
- 🌦️ Atmospheric effects

---

## 📁 Project Structure

~~~~text
flappyansh/
├── .gitignore
├── FlappyAnsh.exe
├── FlappyAnsh.java
├── Flappy-Anshpro/
└── README.md
~~~~

The `Flappy-Anshpro` directory contains additional classes and files used to enhance the game.

---

## 🛠️ Troubleshooting

### `java` is not found

Make sure **JDK 21+** is installed and available in your system `PATH`.

~~~~bash
java -version
~~~~

### `mvn` is not found

Install Maven and verify:

~~~~bash
mvn -version
~~~~

### The `.exe` is blocked by Windows

Windows SmartScreen may warn about the unsigned executable.

Use:

**More info → Run anyway**

### The game does not start

Check:

- Java version
- Maven installation
- Operating system
- Project files
- Terminal output

---

## 🗺️ Roadmap

- [ ] More secret skins
- [ ] More power-ups
- [ ] More weather effects
- [ ] More difficulty modes
- [ ] Improved menus
- [ ] More visual effects
- [ ] Better cross-platform testing
- [ ] Improved build system
- [ ] More game modes

---

## 🤝 Contributing

Contributions are welcome!

1. Fork the repository
2. Create a branch
3. Make your changes
4. Test the game
5. Commit your changes
6. Push your branch
7. Open a Pull Request

Bug reports, feature ideas, gameplay improvements, and code contributions are welcome.

---

## 🐛 Bug Reports

When reporting a bug, include:

- Operating system
- Java version
- Maven version
- Game version
- What happened
- Any error messages

---

## 👨‍💻 Contributors

**AnshLabs716 — Ansh Bhatia**

**shozanthebozan**

---

## 📜 License

See the repository for the current licensing information.

---

<div align="center">

### 🐦 Flappy Ansh

**Fly. Dodge. Survive.**

**Java • Maven • Windows • Linux • macOS • ChromeOS**

</div>

