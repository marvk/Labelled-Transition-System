# Labeled Transition System Utility

This application can parse a simple custom description language for labeled transition systems, create composites (synchronize on identical states), and graph them using [graphviz-java](https://github.com/nidi3/graphviz-java).

##Usage

    HELP
        -h
        Prints this usage page
    FILES
        -f file1 [file2 file3...]
        Specify input file(s), must be last argument
    COMPOSITE
        -c [name]
        If set, a composite will be created from input files
    ALL
        -a
        If set, all input files will be output, not only the composite
    OUTPUT
        -o file
        Set the output folder

##Example

    java lts.jar -c compo -a -o results -f switch.lts lamp.lts
    
This will produce the following images from their respective .lts files and composite respectively:

#### Switch

    switch(rel){
        rel = (p -> pr);
        pr = (r -> rel);
        rel = (h -> rel);
    }

![switch_graph](https://i.imgur.com/YkRcS3P.png) 

#### Lamp

    lamp(off){
        off = (p -> low);
        low = (p -> off);
        low = (h -> high);
        high = (p -> off);
    }

![lamp_graph](https://i.imgur.com/Z6OZ3Im.png) 

#### Composite (Switch||Lamp)

![composite_graph](https://i.imgur.com/tPHVkG1.png) 

##Grammar

The custom grammar is as follows

    S -> NAME(INITIALS){ASSIGNMENTS}
    INITIALS -> STATE INITIAL
    INITIAL -> ,STATE INITIAL | ε
    ASSIGNMENTS -> ASSIGN ASSIGNMENTS | ε
    ASSIGN -> STATE = TRANS;
    TRANS -> (SYMBOL->STATE)
    STAET -> IDENT
    SYMBOL -> IDENT
    NAME -> IDENT
    
    An identifier has the following regular expression:
    
    IDENT = [A-Za-z][A-Za-z0-9_] 