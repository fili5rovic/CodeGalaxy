# CodeGalaxy IDE

A **custom Java IDE** built with JavaFX, designed to provide a lightweight yet powerful development environment with **Language Server Protocol (LSP)** integration for advanced Java editing features.

## Features ✨

- **Code Completion**: Smart suggestions powered by the **Eclipse JDT Language Server**.
- **Syntax Highlighting**: Java syntax-aware code coloring.
- **Real-Time Diagnostics**: Error and warning detection via LSP.
- **Lightweight**: Minimal resource usage compared to traditional IDEs.
- **Customizable UI**: Flexible theming and layout (**work in progress**).

## Technologies Used 🛠️

- **JavaFX** – For the desktop UI and rendering.
- **Eclipse JDT Language Server** – Backend for Java language features.
- **LSP4J** – Java implementation of the Language Server Protocol.
- **Maven** – Dependency and build management.

## Installation & Setup 🚀

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
- Write code with **real-time diagnostics** and **code completion** (trigger with `Ctrl + Space`).
- Customize the UI theme via **Settings > Appearance** (upcoming feature).

## Roadmap 🗺️

Planned features:

- ✅ LSP Integration (basic)
- ⏳ Debugging support
- ⏳ Git integration
- ⏳ Plugin system
- ⏳ Dark/Light theme toggle

---

Stay tuned for updates! 🚀

