# RatGame
Swansea Uni CS-230 Group 39's Rat Game

## Compile & Run Instructions
To be able to compile the project, you will want to enter the working directory of the "src" folder, using the "cd" command in a terminal.
You will then use the "javac" command to compile the "Main.java" class, which will reference all dependancies, as follows:

```javac --module-path ../lib/javafx/lib --add-modules=javafx.controls,javafx.fxml,javafx.media launcher/Main.java```

To then run the project, you will stay in the same working directory, and use the "java" command as follows:

```java --module-path ../lib/javafx/lib --add-modules=javafx.controls,javafx.fxml,javafx.media launcher/Main.java```

In the former commands, the "module-path" can be any directory that contains the JavaFX libraries. The game has been successfully tested using the "OpenJFX-17.0.1" version of JavaFX.

The project was developed and tested using Java 17, specifically "Amazon Corretto 17.0.1".
