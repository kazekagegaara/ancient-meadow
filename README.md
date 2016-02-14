# ancient-meadow
Static Analysis tool for syntactic dependency analysis for front end web apps.

Execute the jar in the build folder. Accepted flag values are -
--help - shows possible flags that can be used
--source - pass the path of the root directory of the code to be analyzed with this flag, e.g., --sourceFile=/User/home/testingdata/
--outputFormat - supported formats are json or text, default value is text
--verbosity - low,medium or high, default value is medium
--recommendations - get suggestions to fix the defects, possible options are yes or no, default value is no
--rules - comma separated list of rules to check. Possible options are ParseError, ReferenceError, FileNotFound, Warnings or all. Default value is all.
--outputFileName - File name to save the output to. Default value is output.


Use OutputJsonViewer.html to view exported JSON as HTML. Firefox is the recommended browser to be used.