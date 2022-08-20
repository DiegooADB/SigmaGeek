package me.diego.pi;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalTime;
import java.util.concurrent.atomic.AtomicInteger;

public class Question2 {
    static AtomicInteger value = new AtomicInteger(1211000);

    public static void main(String[] args) {

        Runnable runnable = createRunnable(value);

        for (int i = 0; i < 30; i++) {
            new Thread(runnable, "Thread - " + i).start();
        }
    }

    private static String request(long value) throws IOException, InterruptedException {
        var client = HttpClient.newHttpClient();

        String url = "https://api.pi.delivery/v1/pi?start=%d&numberOfDigits=21".formatted(value);

        var request = HttpRequest.newBuilder(
                        URI.create(url))
                .header("accept", "application/json")
                .build();

        var response = client.send(request, HttpResponse.BodyHandlers.ofString());


        if (response.statusCode() == 502) {
            System.out.println("502 status retrying with value: " + value);
            return request(value);
        }

        String responseBody = response.body();

        try {
            JSONObject jsonObject = new JSONObject(responseBody);
            return jsonObject.getString("content");
        } catch (JSONException e) {
            e.printStackTrace();
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
            System.out.println(Thread.currentThread().getName() + " has been created");
            while (true) {
                String requestBody = null;
                try {
                    if (value.get() % 1000 == 0) {
                        System.out.println(LocalTime.now() + " " + value.get());
                    }
                    requestBody = request(value.longValue());
                    if (test(requestBody) == null) {
                        value.addAndGet(1);
                    } else {
                        break;
                    }
                    //System.out.println(Thread.currentThread().getName() + " " + value.get());
                } catch (IOException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }
}
