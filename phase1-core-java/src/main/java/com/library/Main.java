package com.library;

/**
 * Entry point for the Phase 1 console application.
 *
 * <p>Phase 1 is plain Java on purpose: no Spring, no Lombok. The menu printed here
 * is wired up to real behaviour in task 1.12.
 */
public class Main {

    public static void main(String[] args) {
        printWelcome();
        printMenu();
    }

    private static void printWelcome() {
        System.out.println("=".repeat(44));
        System.out.println("   LIBRARY MANAGEMENT SYSTEM - Phase 1");
        System.out.println("   Core Java console app");
        System.out.println("=".repeat(44));
    }

    private static void printMenu() {
        System.out.println();
        System.out.println("  1. List all items");
        System.out.println("  2. Search items");
        System.out.println("  3. Borrow an item");
        System.out.println("  4. Return an item");
        System.out.println("  5. Show overdue loans");
        System.out.println("  0. Exit");
        System.out.println();
        System.out.print("Choose an option: ");
        System.out.println();
        System.out.println("(menu is not interactive yet - see task 1.12)");
    }
}
