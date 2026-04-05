<div align="center">

 JJournal
===
[![License](https://img.shields.io/badge/license-MIT-green)](./LICENSE) [![Stars](https://img.shields.io/github/stars/dug22/JJournal.svg)](https://github.com/dug22/JJournal/stargazers) [![Downloads](https://img.shields.io/github/downloads/dug22/JJournal/total.svg)](https://github.com/dug22/JJournal/releases)[![Java](https://img.shields.io/badge/java-23-red)](#)

</div>

## Overview
JJournal is a desktop based notebook software that allow users to write and execute Java code. JJournal leverages JShell's API, making it possible
to interactively run code snippets, visualize outputs, and document workflows with in a single, user-friendly ecosystem. With JJournal you never have to lose your progress,
as JJournal allows you to save your notebook's work and  load it back up to work on later. JJournal utilizes GSON to save and load notebook components
(including cells and their content). Give JJournal a try if your work primarily revolves around simple code testing, analysis, and more.

![overview.png](src/main/resources/images/overview.png)

## Getting Started

```JJournal``` requires Java 23 or higher.

```JJournal``` is distributed as Jar and can be downloaded from the repository's releases tab.

```
curl -LO https://github.com/dug22/JJournal/releases/download/{version}/JJournal.jar
```

Then to launch the software:

```
java -jar JJournal.jar 
```

### Initial Setup
You will be prompted with this upon launch: (this will only appear once)
```txt
Before using JJournal for the first time, you will be prompted to provide a list of class paths you wish to use. 
Once entered, these class paths will be saved in the following text file: {class-paths_file_path.txt}.
When entering your JAR file paths, use the following format:

\\path\\to\\jar1.jar
\\path\\to\\jar2.jar

Type ‘done’ when you have finished entering class paths, or if you prefer to use JJournal without any dependencies.

If you need to update your class paths later, simply edit the class-paths.txt file. This prompt appears only once, during the initial launch.
```

<div align="justify">
Optionally, you can provide an input of class paths you wish to use (you can skip this part by typing 'done'). 
Class paths are the dependencies you wish to use upon launch. Class paths must all point to the jar files of 
the dependencies you wish to use. Most of your dependencies are likely to be located in your .m2/repository folder if you use Maven. 
If you wish to update your class-paths file, just edit the class-paths.txt file (which is located in the JJournal folder, which is in your user directory), 
as this prompt only shows up once on the first launch.
</div>


After typing done, JJournal will then launch. Enjoy!

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