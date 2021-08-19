package de.pcfreak9000.spaceawaits.command;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import picocli.CommandLine;

public class CommandlineWrapper {
    private final CommandLine commandline;
    
    public CommandlineWrapper(PrintWriter out, PrintWriter err, Object basecommand) {
        this.commandline = new CommandLine(basecommand);
        this.commandline.setOut(out);
        this.commandline.setErr(err);
        this.commandline.setExpandAtFiles(false);
    }
    
    public void submitCommand(String input) {
        String[] parseResult = parse(input);
        if (parseResult != null) {
            this.commandline.execute(parseResult);
        }
    }
    
    public void addSubCommand(Object command) {
        this.commandline.addSubcommand(command);
    }
    
    private String[] parse(String in) {
        List<String> parts = new ArrayList<>();
        //        if (in.startsWith("%") || in.startsWith("#")) {//Indicates a comment
        //            return parts.toArray(String[]::new);
        //        }
        char[] chars = in.toCharArray();
        StringBuilder builder = new StringBuilder();
        boolean inside = false;
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (c == '"' && (i <= 1 || chars[i - 1] != '\\')) {
                inside = !inside;
                if (!inside) {
                    String s = builder.toString();
                    if (!s.isEmpty()) {
                        s = s.replace("\\\"", "\"");
                        parts.add(s);
                        builder = new StringBuilder();
                    }
                }
                continue;
            }
            if ((c == ' ' && !inside) || i == chars.length - 1) {
                if (i == chars.length - 1) {
                    builder.append(c);
                }
                String s = builder.toString();
                if (!s.isEmpty()) {
                    s = s.trim();
                    parts.add(s);
                    builder = new StringBuilder();
                }
            } else {
                builder.append(c);
            }
        }
        if (inside) {
            this.commandline.getErr().println("Unclosed '\"'");
            return null;
        }
        return parts.toArray(String[]::new);
    }
}
