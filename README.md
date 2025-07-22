# CodeGalaxy IDE

A **custom Java IDE** built with JavaFX, designed to provide a lightweight yet powerful development environment with **Language Server Protocol (LSP)** integration for advanced Java editing features.

![Platform](https://img.shields.io/badge/Platform-Windows%20%7C%20macOS%20%7C%20Linux-lightgrey)
![Java](https://img.shields.io/badge/Java-22+-ED8B00?logo=openjdk&logoColor=white)
![JavaFX](https://img.shields.io/badge/JavaFX-Powered-FF6C37?logo=java&logoColor=white)
![Eclipse JDT](https://img.shields.io/badge/Eclipse_JDT-LSP-2C2255?logo=eclipse&logoColor=white)
![Git](https://img.shields.io/badge/Git-Integration-F05032?logo=git&logoColor=white)
![Status](https://img.shields.io/badge/Status-Active%20Development-brightgreen)

## Features
<table>
  <tr>
    <td align="center">
      <strong>Code Completion</strong><br>
      <img src="https://github.com/user-attachments/assets/945d95bd-36d5-458b-a011-694bb321ea2e" width="300"><br>
      <em>Smart suggestions powered by the Eclipse JDT Language Server.</em>
    </td>
    <td align="center">
      <strong>Syntax Highlighting</strong><br>
      <img src="https://github.com/user-attachments/assets/38658734-511e-4e3a-b652-d0cc78125760" width="300"><br>
      <em>Java syntax-aware code coloring.</em>
    </td>
  </tr>
  <tr>
    <td align="center">
      <strong>Real-Time Diagnostics</strong><br>
      <img src="https://github.com/user-attachments/assets/31853920-d1b0-4ad7-8e54-9cfeb6419b5a" width="300"><br>
      <em>Error and warning detection via LSP.</em>
    </td>
    <td align="center">
      <strong>Go to Definition</strong><br>
      <img src="https://github.com/user-attachments/assets/ad22be34-ebcd-4fe5-ace7-91fa2306bfa0" width="300"><br>
      <em>Jump to the declaration of classes, methods, or variables.</em>
    </td>
  </tr>
  <tr>
    <td align="center">
      <strong>Find References</strong><br>
      <img src="https://github.com/user-attachments/assets/f9998925-9ee8-413e-ab6d-fd275b30d0fa" width="300"><br>
      <em>Locate all usages of symbols in your codebase.</em>
    </td>
    <td align="center">
      <strong>Git Integration</strong><br>
      <img src="https://github.com/user-attachments/assets/a4dfd035-853e-42f4-bc47-214dbbea52c5" width="300"><br>
      <em>Easy way to use version control within the IDE.</em>
    </td>
  </tr>
  <tr>
    <td align="center">
      <strong>Customizable UI</strong><br>
      <img src="https://github.com/user-attachments/assets/abbfbc59-83f1-480b-92c2-6fb953332b05" width="300"><br>
      <em>Flexible theming and layout.</em>
    </td>
    <td align="center">
      <strong>Code Suggestions</strong><br>
      <em>Intelligent hints and quick-fixes.</em>
    </td>
  </tr>
  
</table>

## Technologies Used

- **JavaFX** – For the desktop UI and rendering.
- **Eclipse JDT Language Server** – Backend for Java language features.
- **LSP4J** – Java implementation of the Language Server Protocol.
- **JGit** - Git integration
- **Maven** – Dependency and build management.

## Installation & Setup

### Prerequisites

- **Java 22** or higher
- **Maven 3.6+**

### 1. Clone the Repository
```sh
git clone https://github.com/fili5rovic/CodeGalaxy.git
```

### 2. Build the Project
```sh
mvn clean install
```

### 3. Run the IDE
```sh
mvn javafx:run
```

> [!NOTE]
> CodeGalaxy will prompt you to install a language server upon first launch.

## Usage 📖

- Start the IDE and create/open a **Java project**.
- Write code with **real-time diagnostics**, **code completion**, **go to definition**, and **find references**.
- Trigger suggestions with `Ctrl + Space`.
- Customize the UI theme via **Settings > Appearance**.

---

Stay tuned for updates!
