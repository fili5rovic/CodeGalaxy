# CodeGalaxy IDE

A **custom Java IDE** built with JavaFX, designed to provide a lightweight yet powerful development environment with **Language Server Protocol (LSP)** integration for advanced Java editing features.

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
  <tr>
    <td align="center">
      <strong>Lightweight</strong><br>
      <em>Minimal resource usage compared to traditional IDEs.</em>
    </td>
    <td></td>
  </tr>
</table>

## Technologies Used

- **JavaFX** – For the desktop UI and rendering.
- **Eclipse JDT Language Server** – Backend for Java language features.
- **LSP4J** – Java implementation of the Language Server Protocol.
- **Maven** – Dependency and build management.

## Installation & Setup

### Prerequisites

- **Java 17** or higher
- **Maven 3.6+**

### Steps

1. **Clone the repository**:
   ```sh
   git clone https://github.com/yourusername/CodeGalaxy.git
   cd CodeGalaxy
   ```

2. **Build the project**:
   ```sh
   mvn clean install
   ```

3. **Run the IDE**:
   ```sh
   mvn javafx:run
   ```

## Usage 📖

- Start the IDE and create/open a **Java project**.
- Write code with **real-time diagnostics**, **code completion**, **go to definition**, and **find references**.
- Trigger suggestions with `Ctrl + Space`.
- Customize the UI theme via **Settings > Appearance**.

---

Stay tuned for updates!
