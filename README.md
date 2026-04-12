<div align="center">

 JJournal
===
[![License](https://img.shields.io/badge/license-MIT-green)](./LICENSE) [![Stars](https://img.shields.io/github/stars/dug22/JJournal.svg)](https://github.com/dug22/JJournal/stargazers) [![Downloads](https://img.shields.io/github/downloads/dug22/JJournal/total.svg)](https://github.com/dug22/JJournal/releases)[![Java](https://img.shields.io/badge/java-23-red)](#)

</div>


# Overview
Do you wish you could easily read, write, and execute Java code in a notebook-like environment, just as you can with Julia, Python, or R, without the headache of having to download kernels and other third-party dependencies to achieve such a simple task? Then JJournal is the right software for you! JJournal 
(short for Java Journal) is a native Java desktop application that brings Jupyter Notebook-style functionality to Java developers, providing an interactive environment for writing, documenting, and executing Java code. 

### JJournal’s Features:

* Run code snippets interactively, without the necessary boilerplate.
* Dependency Support: Import and use any external dependency jars of your choice.
* Create notecells with Markdown support (like Google Colab, Kaggle, etc)
* Built-in code completion suggestions to speed up your workflow. (Press the "ALT" keybind for suggestions).
* Save your progress and load your journal to pick up right where you left off.

JJournal leverages JShell's API to the fullest extent for code features, and GSON's API to save and load journal data.

<img width="1193" height="1318" alt="image" src="https://github.com/user-attachments/assets/681321d0-2c91-4120-9cb9-3be660e40908"/>

<video src="https://github.com/user-attachments/assets/963289e6-fcf3-4c23-a02b-a6cd8878dea0"></video>

# Getting Started 
JJournal requires Java 23 or higher.

JJournal is distributed as a Jar and can be downloaded from the repository's releases tab.

## Initial Process: 

Open your command prompt and type the following command to download JJournal's jar file.

```curl -LO https://github.com/dug22/JJournal/releases/download/{version}/JJournal.jar```

Then to launch JJournal

```java -jar JJournal.jar```

After this a folder called JJournal will be created within your users folder after typing the command to launch the software. You will see the following folders and files:
-  journals - a folder where you can save your journals
-  class-paths.txt - a text file that holds all your dependencies
-  init.txt - a text file that keeps track of whether Journal has been launched for the first time or not.

You will also be prompted with this upon launch: (this will only appear once)

```
Before using JJournal for the first time, you will be prompted to provide a list of class paths you wish to use. 
Once entered, these class paths will be saved in the following text file: {class-paths_file_path.txt}.
When entering your JAR file paths, use the following format:

\\path\\to\\jar1.jar
\\path\\to\\jar2.jar

Type ‘done’ when you have finished entering class paths, or if you prefer to use JJournal without any dependencies.

If you need to update your class paths later, simply edit the class-paths.txt file. This prompt appears only once, during the initial launch.
```

Optionally, you can provide an input of class paths you wish to use (you can skip this part by typing 'done'). 
Class paths are the dependencies you wish to use upon launch. Class paths must all point to the jar files of the dependencies you wish to use. 
Most of your dependencies are likely to be located in your .m2/repository folder if you use Maven. If you wish to update your class-paths file, 
just edit the class-paths.txt file (which is located in the JJournal folder, which is in your user directory), as this prompt only shows up once on
the first launch. After typing done, JJournal will then launch. Enjoy! 

## License
JJournal is released under the MIT license [MIT License](https://github.com/dug22/Image-Shield/blob/master/LICENSE)

```
MIT License

Copyright (c) 2026 dug22

Permission is hereby granted, free of charge, to any person
obtaining a copy of this software and associateddocumentation
files (the "Software"),to deal in the Software without restriction,
including without limitationthe rights to use, copy, modify, merge,
publish, distribute, sublicense,and/or sell copies of the Software,
and to permit persons to whom the Software is furnished to do so,
subject to the following conditions:

The above copyright notice and this permission notice shall be included
in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
```

## Contributions
Contributions are welcome! If you have suggestions, bug fixes, or enhancements, please open an issue to share them.
