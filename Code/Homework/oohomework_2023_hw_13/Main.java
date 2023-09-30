import java.util.Scanner;

import requests.Request;
import library.Scheduler;

public class Main {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        //BookLibrary bookLibrary = new BookLibrary();
        Scheduler scheduler = new Scheduler();
        int n = input.nextInt();
        input.nextLine();
        for (int i = 0; i < n; i++) {
            scheduler.addBook(input.nextLine());
        }
        n = input.nextInt();
        input.nextLine();
        for (int i = 0; i < n; i++) {
            Request request = new Request(input.nextLine());
            scheduler.addRequest(request);
        }
        scheduler.deal();
        input.close();
    }
}