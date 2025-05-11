Command Executor

A Java application that executes commands specified in a file and writes the output to another file.

Features
- Reads commands from a file (commands.txt) and executes them.
- Supports one-time and recurring tasks.
- Writes output to a specified file (sample-output.txt).

Requirements
- Java 8 or later.
- A commands.txt file with the specified format.

Command File Format
The commands.txt file should contain commands in the following format:

One-time Tasks
minute hour day month year command

Recurring Tasks
*/interval command

Example

06 20 11 5 2025 cmd /c "echo %date% %time% && echo At Amex, We Do What's Right."
*/1 cmd /c "echo %date% %time% && echo Amex' motto is 'Don't live life without it!'"


Usage
1. Create a commands.txt file with the desired commands.
2. Run the CommandExecutor class.
3. The output will be written to the specified output file (sample-output.txt).
