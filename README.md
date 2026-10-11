
<img width="1360" height="719" alt="Screenshot_20260819_182803" src="https://github.com/user-attachments/assets/44c04f8d-ab27-4fd6-bbc2-3920a24bdb63" />


# 🐦 Flappy Ansh

<div align="center">

### 🎮 A Java-Based Flappy Bird Clone

**Custom difficulty • Secret skins • Power-ups • Particle effects • Weather effects • Windows • Linux • macOS • ChromeOS**

[![Java](https://img.shields.io/badge/Java-21%2B-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.java.com/)
[![Platform](https://img.shields.io/badge/Platform-Windows%20%7C%20macOS%20%7C%20Linux%20%7C%20ChromeOS-lightgrey?style=for-the-badge)](https://github.com/anshlabs716/flappyansh)



</div>

---

## 📑 Table of Contents

- [🎮 A Java-Based Flappy Bird Clone](#-a-java-based-flappy-bird-clone)
- [🐦 About](#-about)
- [✨ Features](#-features)
- [🖥️ Platform Support](#-platform-support)
- [☕ Requirements](#-requirements)
- [Java Source](#java-source)
- [Windows `.exe`](#windows-exe)
- [📦 Installation](#-installation)
- [🪟 Windows](#-windows)
- [🍎 macOS](#-macos)
- [🐧 Debian-Based Linux](#-debian-based-linux)
- [🚀 Run From Source](#-run-from-source)
- [🪟 Windows Executable](#-windows-executable)
- [⚠️ Windows SmartScreen](#-windows-smartscreen)
- [🧑‍💻 IDE Support](#-ide-support)
- [⚡ Flappy Ansh Pro](#-flappy-ansh-pro)
- [📁 Project Structure](#-project-structure)
- [🛠️ Troubleshooting](#-troubleshooting)
- [`java` is not found](#java-is-not-found)
- [The `.exe` is blocked by Windows](#the-exe-is-blocked-by-windows)
- [The game does not start](#the-game-does-not-start)
- [🗺️ Roadmap](#-roadmap)
- [🤝 Contributing](#-contributing)
- [🐛 Bug Reports](#-bug-reports)
- [👨‍💻 Contributors](#-contributors)
- [📜 License](#-license)

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

---

## 📦 Installation

### 🪟 Windows

Install Java using Winget in PowerShell:

~~~~powershell
winget install Eclipse.Temurin.21.JDK
~~~~

### 🍎 macOS

Using Homebrew:

~~~~bash
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
brew install openjdk@21
~~~~

### 🐧 Debian-Based Linux

For Ubuntu, Linux Mint, Kali, and other Debian-based distributions:

~~~~bash
sudo apt update
sudo apt install openjdk-21-jdk -y
~~~~

Verify the installation:

~~~~bash
java -version
~~~~

---

## 🚀 Run From Source

Clone the repository:

~~~~bash
git clone https://github.com/anshlabs716/flappyansh.git
cd flappyansh
~~~~

Compile the Java source:

~~~~bash
javac FlappyAnshPro.java
~~~~

Run the game:

~~~~bash
java FlappyAnshPro
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
├── FlappyAnshPro.java
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

### The `.exe` is blocked by Windows

Windows SmartScreen may warn about the unsigned executable.

Use:

**More info → Run anyway**

### The game does not start

Check:

- Java version
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

**Java • Windows • Linux • macOS • ChromeOS**

</div>