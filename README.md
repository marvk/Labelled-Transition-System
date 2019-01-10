# Labeled Transition System Utility

This application can parse a simple custom description language for labeled transition systems, create composites (synchronize on identical transition symbols), and graph them using [graphviz-java](https://github.com/nidi3/graphviz-java).

## Usage

    HELP
        -h
        Prints this usage page
    FILES
        -f file1 (file2 file3...)
        Specify input file(s), must be last argument
    COMPOSITE
        -c [name]
        If set, a composite will be created from input files
    ALL
        -a
        If set, all input files will be output, not only the composite
    SHOW UNREACHABLE STATES
        -u
        If set, renders unreachable states
    ADD ATOMIC PROPOSITIONS
        -ap
        Specify a csv file to add atomic propositions to lts(es)
    CHECK CTL FORMULA(S)
        -ctl
        If set, checks the CTL formula(s) for a given lts name
    ENGINE
        -e [CIRCO|DOT|NEATO|OSAGE|TWOPI|FDP]
        Set the Graphviz rendering engine
    OUTPUT
        -o file
        Set the output folder

## Example

    java -jar lts.jar -c compo -ctl CTLFormulasToCheck.csv -ap aps.csv -a -o results -f switch.lts lamp.lts
    
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


#### Adding Atomic Propositions
At first, state the name of the LTS where Atomic Propositions should be added to. Then, for each state in a new line,
give it's representation and the set of atomic propositions separated by ','.

    LTSname
    state1Representation,ap1
    state2Representation,ap1,ap2,ap3
    
Example (aps.csv):

    lamp
    low,lightOn
    high,lightOn,highBattUse
    compo
    low+pr,lightOn
    low+rel,lightOn
    high+rel,lightOn,highBattUse


#### CTL Formulas
Each line contains the name of the LTS to be checked and the CTL Formula (separated by ',').

    LTSName,formula1
    
Example(CTLFormulasToCheck.csv):

    lamp,lightOn∨E[EX lightOn U lightOn]
    lamp,EG lightOn
    lamp,EG ¬highBattUse
    lamp,EX lightOn
    lamp,EX ¬lightOn
    lamp,EX highBattUse
    lamp,EX ¬highBattUse
    lamp,E[1 U lightOn]
    lamp,¬E[1 U ¬E[1 U ¬lightOn]]
    lamp,¬E[1 U EG ¬highBattUse]
    lamp,E[1 U ¬highBattUse]
    lamp,lightOn∨EX highBattUse
    lamp,E[lightOn U ¬highBattUse]
    compo,lightOn
    compo,lightOn∨highBattUse
    compo,EX ¬lightOn
    compo,EG ¬highBattUse
    compo,E[1 U lightOn]
    compo,¬E[1 U ¬E[1 U ¬lightOn]]
    compo,¬E[1 U EG ¬highBattUse]
    compo,E[1 U ¬highBattUse]


## Grammar

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
