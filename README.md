# BKMTMEdit

A text editor. Written in **Java** using Swing and *JavaFX*.

A bigger brother to TkEdit (A text editor written in python and tkinter).

### How to use the UI?

Learn it yourself, I do not have the time to create a complete documentation yet.

### FAQs

##### What the hell is this? Why do you make another one?

> It's **better** than before! More extensible and neater code (try to make some dry, but it's still wet). Syntax Highlighting included. It is almost like an IDE, except it's not intended to be an IDE.

##### How to do stuff with this thing?

> If you want to run this, use the JAR in the release:

>> 1. Go to the release
>> 2. Download the latest version
>> 3. Download the JAR
>> 4. Open the folder where you download the JAR
>> 5. If it is the first time running it, follow this: Get it to make the config by running it.
>> 7. Run the jar

> If you want to build, read the [How to make file, this is the link to it](appdocs/how2make.txt)


##### Why do I see MXPSQL.BKMTMEdit.utils and MXPSQL.BKMTMEdit.widgets packages

> Those are reusable components that you can actually use!

##### Where is the javadoc online

> No, first too lazy to upload it online and if I am not lazy, I am having trouble exporting the javadoc

##### Why does it say that the platform theme is broken

> Find the code that will enable the platform theme and uncomment it. Build the project and see the problem.

##### Why is there Jetty and why is the variable called MJFX eventhough the editor is called TxEditor

> The editor component originally used is [Monaco-Editor](https://github.com/microsoft/monaco-editor "The editor that powers VS-Code (Visual Studio)") and it uses service workers and you know they need to come from https or http, so Jetty for it. But there is a problem with the webview and I ended up replacing it with [RSyntaxTextArea](https://github.com/bobbylight/RSyntaxTextArea "Nice component btw"). Jetty and MJFX is the remains of that.