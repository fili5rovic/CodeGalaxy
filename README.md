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
cd CodeGalaxy
```

### 2. Download Eclipse JDT Language Server

CodeGalaxy requires the Eclipse JDT Language Server to provide advanced Java editing features. You need to download it manually:

   **Visit the Eclipse JDT LS Downloads page**:
   - Go to: https://download.eclipse.org/jdtls/milestones/
   - Select the latest milestone
   - Download compressed file ending with `.tar.gz` (e.g., `jdt-language-server-1.40.0-202501161421.tar.gz`)  
   
> [!NOTE]  
> **CodeGalaxy** has been tested and verified to work with 2025 versions of the Eclipse JDT Language Server. Older versions may still work, but are not guaranteed.

### 3. Extract the Language Server

**For Linux/macOS:**
```sh
# Create lsp directory in the project root
mkdir lsp

# Extract the downloaded tar.gz file into the lsp directory
tar -xzf jdt-language-server-*.tar.gz -C lsp/
```

**For Windows:**
```cmd
# Create lsp directory in the project root
mkdir lsp

# Extract using your preferred tool (7-Zip, WinRAR, etc.)
# Or use Windows Subsystem for Linux (WSL) with the tar command above
# Extract the contents to: C:\path\to\CodeGalaxy\lsp\
```

> [!IMPORTANT]  
> Your folder structure should now be `/path/to/CodeGalaxy/lsp/`. CodeGalaxy expects this exact folder structure.

### 4. Verify the LSP Structure

After extraction, your `lsp/` directory should contain:
```
lsp/
├── bin/
├── config_win/      (Windows configuration)
├── config_linux/    (Linux configuration)
├── config_mac/      (macOS configuration)
├── features/
└── plugins/
```

> [!TIP]  
> If you are using an IDE, you can skip the next steps and run the project.

### 5. Build the Project
```sh
mvn clean install
```

### 6. Run the IDE
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
