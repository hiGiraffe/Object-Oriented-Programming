import java.util.Scanner;

import library.Scheduler;
import library.School;
import requests.Request;

public class Main {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        Scheduler scheduler = new Scheduler();
        int t = input.nextInt();
        input.nextLine();
        int n;
        for (int i = 0; i < t; i++) {
            School school = new School(input.nextLine(), scheduler);
            scheduler.addSchool(school);
            for (int j = 0; j < school.getBookNum(); j++) {
                school.addBook(input.nextLine());
            }
        }
        n = input.nextInt();
        input.nextLine();
        for (int i = 0; i < n; i++) {
            Request request = new Request(input.nextLine(), scheduler);
            scheduler.addRequest(request);
        }
        scheduler.deal();
        input.close();
    }
}