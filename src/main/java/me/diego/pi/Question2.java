package me.diego.pi;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

public class Question2 {
    static AtomicInteger value;

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter the start number: ");
        int number = scanner.nextInt();
        value = new AtomicInteger(number);

        System.out.println("Enter the number of threads that you want to use: ");
        int threads = scanner.nextInt();

        System.out.printf("Starting with %d threads at number %d%n", threads, number);

        Runnable runnable = createRunnable(value);

        for (int i = 0; i < threads; i++) {
            new Thread(runnable, "Thread - " + i).start();
        }
    }

    private static String request(long value) {
        var client = HttpClient.newHttpClient();

        String url = "https://api.pi.delivery/v1/pi?start=%d&numberOfDigits=21".formatted(value);

        var request = HttpRequest.newBuilder(
                        URI.create(url))
                .header("accept", "application/json")
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        try {
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if(response.statusCode() == 429) {
                System.out.println("rate limit reached, retrying after 3 second");
                Thread.sleep(3000);
                return request(value);
            }

            if (response.statusCode() != 200) {
                System.out.printf("%s | Something went wrong retrying with value: %d%n", response.statusCode(), value);
                return request(value);
            }

            String responseBody = response.body();
            JSONObject jsonObject = new JSONObject(responseBody);
            return jsonObject.getString("content");
        } catch (IOException | InterruptedException e) {
            System.out.printf("Error while requesting retrying with value: %d%n", value);
            e.printStackTrace();
            request(value);
        } catch (JSONException e) {
            System.out.printf("Can't parse response to json with value: %d, retrying to send request", value);
            e.printStackTrace();
            request(value);
        }
        return null;
    }

    private static String test(String value) {
        int count = 0;
        char[] numString = String.valueOf(value).toCharArray();
        int length = numString.length;
        int arrayLength = length - 1;

        for (int i = 0; i < length; i++) {
            if (numString[i] == numString[arrayLength - i]) {
                count++;
            }
        }

        if (count == 21) {
            write(value);
            return null;
        } else {
            return null;
        }
    }

    private static void write(String number) {
        File file = new File("Number.txt");
        try (BufferedWriter buf = new BufferedWriter(new FileWriter(file, true))) {
            buf.write(number + "\n");
            System.out.println("###################################################");
            System.out.println("A Number has been written in txt file");
            System.out.println("###################################################");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    private static boolean isPrime(String number) {
//        long num = Long.parseLong(number);
//
//        System.out.println("Testou o numero: " + number);
//
//        if (num <= 1) {
//            return false;
//        }
//        for (int i = 2; i < num / 2; i++) {
//            if ((num % i) == 0)
//                return false;
//        }
//        return true;
//    }

    private static Runnable createRunnable(AtomicInteger value) {
        return () -> {
            //System.out.println(Thread.currentThread().getName() + " has been created");
            while (true) {
                String requestBody = null;
                    if (value.get() % 1000 == 0) {
                        System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + " | " + value.get());
                    }
                    requestBody = request(value.longValue());
                    if (test(requestBody) == null) {
                        value.addAndGet(1);
                    } else {
                        break;
                    }
                    //System.out.println(Thread.currentThread().getName() + " " + value.get());
            }
        };
    }
}
