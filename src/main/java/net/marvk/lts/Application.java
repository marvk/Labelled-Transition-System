package net.marvk.lts;

import guru.nidi.graphviz.engine.Engine;
import net.marvk.lts.compiler.parser.ParseException;
import net.marvk.lts.model.AtomicProposition;
import net.marvk.lts.model.LabeledTransitionSystem;
import net.marvk.lts.model.State;
import net.marvk.lts.model.ctl.CTL;
import net.marvk.lts.util.Util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public final class Application {

    private static final String HELP_STRING = "HELP\n" +
            "\t-h\n" +
            "\tPrint this help page\n" +
            "FILES\n" +
            "\t-f file1 [file2 file3...]\n" +
            "\tSpecify input file(s), must be last argument\n" +
            "COMPOSITE\n" +
            "\t-c [name]\n" +
            "\tIf set, a composite will be created from input files\n" +
            "ALL\n" +
            "\t-a\n" +
            "\tIf set, all input files will be output, not only the composite\n" +
            "SHOW UNREACHABLE STATES\n" +
            "\t-u\n" +
            "\tIf set, renders unreachable states\n" +
            "ADD ATOMIC PROPOSITIONS\n" +
            "\t-ap\n" +
            "\tSpecify a csv file to add atomic propositions to lts(es)\n" +
            "CHECK CTL FORMULA(S)\n" +
            "\t-ctl\n" +
            "\tIf set, checks the CTL formula(s) for the given lts name\n" +
            "ENGINE\n" +
            "\t-e [CIRCO|DOT|NEATO|OSAGE|TWOPI|FDP]\n" +
            "\tSet the Graphviz rendering engine\n" +
            "OUTPUT\n" +
            "\t-o file\n" +
            "\tSet the output folder\n\n" +
            "Example:\n" +
            "\tjava -jar lts.jar -c comp -ctl CTLFormulas.csv -ap aps.csv -a -o results -f switch.lts lamp.lts";

    private Application() {
        throw new AssertionError("No instances of class " + Application.class);
    }

    public static void main(final String[] args) throws IOException, ParseException {
        if (args.length == 0) {
            printHelp();
            return;
        }

        Engine engine = Engine.CIRCO;
        List<LabeledTransitionSystem> lts = null;
        boolean outputAll = false;
        boolean createComposite = false;
        boolean showUnreachables = false;
        boolean checkCTL = false;
        Path output = Paths.get("");
        String aps = null;
        String compositeName = null;
        String ctl = null;
        String ltsToCheck = null;
        HashMap<LabeledTransitionSystem, Set<CTL>> ctlFormulaChecks;
        List<LabeledTransitionSystem> allLTSs = new ArrayList<>();

        for (int i = 0; i < args.length; i++) {
            final String arg = args[i];
            switch (arg) {
                case "-h":
                    printHelp();
                    return;
                case "-f":
                    lts = getLts(args, i);
                    i = args.length;
                    break;
                case "-e":
                    i++;
                    final String arg1 = args[i];
                    engine = Engine.valueOf(arg1);
                    break;
                case "-c":
                    createComposite = true;
                    final String maybeName = args[i + 1];
                    if (!maybeName.startsWith("-")) {
                        i++;
                        compositeName = maybeName;
                    }
                    break;
                case "-ap":
                    i++;
                    aps = args[i];
                    //System.out.println("APs path is " + aps.toString());
                    break;
                case "-ctl":
                    checkCTL = true;
                    i++;
                    ctl = args[i];
                    break;
                case "-u":
                    showUnreachables = true;
                    break;
                case "-a":
                    outputAll = true;
                    break;
                case "-o":
                    i++;
                    output = Paths.get(args[i]);
                    break;
                default:
                    System.err.println("Error: Unrecognized argument " + arg);
                    return;
            }
        }

        if (lts == null || lts.isEmpty()) {
            System.err.println("Error: No input specified");
            return;
        }
        LabeledTransitionSystem composite = null;
        if (createComposite) {
            if (lts.size() <= 1) {
                System.err.println("Warning: Only one lts specified, ignoring composite instruction");
            }

            composite = lts.get(0).parallelComposition(compositeName, showUnreachables, lts.subList(1, lts
                                                                 .size()));

            Util.renderLts(composite, output, engine);
        }

        if (lts.size() == 1 || outputAll) {
            for (final LabeledTransitionSystem lt : lts) {
                Util.renderLts(lt, output, engine);
            }
        }

        if (aps != null){
            //Add APs to LTS
            HashMap<State, Set<AtomicProposition>> labelingAP = new HashMap<>();
            Set<AtomicProposition> allAPs = new HashSet<>();
            String line;
            try(BufferedReader br = new BufferedReader(new FileReader(aps))){
                while ((line = br.readLine()) != null){
                    String[] l = line.split(",");
                    //System.out.println(line);
                    if (l[0].equals("LTS name")){
                        ltsToCheck = l[1];
                    }else{
                        State key = new State(l[0]);
                        Set<AtomicProposition> atomicPropositions = new HashSet<>();
                        for (int i = 1; i < l.length; i++){
                            AtomicProposition atomicProposition = new AtomicProposition(l[i]);
                            atomicPropositions.add(atomicProposition);
                            allAPs.add(atomicProposition);
                        }
                        labelingAP.put(key, atomicPropositions);
                    }
                }
            }catch(IOException e){
                e.printStackTrace();
            }

            allLTSs.addAll(lts);
            allLTSs.add(composite);

            for (final LabeledTransitionSystem lt: allLTSs){
                if (lt.getName().equals(ltsToCheck)){
                    lt.setAtomicPropositions(allAPs);
                    lt.setLabelingAP(labelingAP);
                }
                //System.out.println(lt.toString());
            }

        }

        if (checkCTL){
            HashMap<String, Set<CTL>> ctlsToCheck = new HashMap<>();
            String line;
            try(BufferedReader bufferedReader = new BufferedReader(new FileReader(ctl))){
                while((line = bufferedReader.readLine()) != null){
                    String[] l = line.split(",");
                    if (!ctlsToCheck.containsKey(l[0])){
                        ctlsToCheck.put(l[0], new HashSet<>());
                    }
                    ctlsToCheck.get(l[0]).add(new CTL(l[1]));
                }
            }catch (IOException e){
                e.printStackTrace();
            }

            ctlFormulaChecks = allLTSs.stream().filter(lt -> ctlsToCheck.containsKey(lt.getName())).collect(Collectors.toMap(lt -> lt, lt -> ctlsToCheck.get(lt.getName()), (a, b) -> b, HashMap::new));

            System.out.println("\nCTL CHECK\n");
            String outputCTLCheck = "";
            for (Map.Entry<LabeledTransitionSystem, Set<CTL>> entry: ctlFormulaChecks.entrySet()){
                if (entry.getKey().getLabelingAP() != null){
                    outputCTLCheck += "For the LTS named *" + entry.getKey().getName() + "*:";
                    for (CTL c: entry.getValue()){
                        outputCTLCheck += "\n\t- " + c.getFormula() + " -> " +
                                (c.check(entry.getKey()) ? "TRUE" : "FALSE");
                    }
                }else{
                    outputCTLCheck += "For the LTS named *" + entry.getKey().getName() + "* exists no labeling function.\n\n";
                }

            }
            System.out.println(outputCTLCheck);
        }

    }

    private static List<LabeledTransitionSystem> getLts(final String[] args, final int i) throws IOException, ParseException {
        final List<LabeledTransitionSystem> result = new ArrayList<>();

        final List<Path> collect = Arrays.stream(Arrays.copyOfRange(args, i + 1, args.length))
                                         .map(s -> Paths.get(s))
                                         .collect(Collectors.toList());

        for (final Path path : collect) {
            result.add(Util.parseLts(path));
        }

        return result;
    }

    private static void printHelp() {
        System.out.println(HELP_STRING);
    }
}
