package org.arxing.axutils_java.comm;

public class WinCommand extends Command {

    public WinCommand() {
        super(new String[]{"cmd.exe", "/c"});
    }
}
