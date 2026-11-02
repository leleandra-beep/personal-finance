package com.finance;

import com.finance.cli.ConsoleApp;
import com.finance.infra.FileStorage;

public class Main {
    public static void main(String[] args) {
        String path = args.length > 0 ? args[0] : "wallets.txt";
        new ConsoleApp(new FileStorage(path)).run();
    }
}

