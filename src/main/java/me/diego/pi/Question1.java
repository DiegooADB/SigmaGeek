package me.diego.pi;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Question1 {
    public static void main(String[] args) throws IOException, InterruptedException {
        var client = HttpClient.newHttpClient();

        String url1Milion = "https://uploadbeta.com/api/pi/?cached&n=1000000";

        var request = HttpRequest.newBuilder(
                        URI.create(url1Milion))
                .header("accept", "application/json")
                .build();

        var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        var responseBody = response.body();

        for (int i = 2; i < responseBody.length(); i++) {
            String charSequence = responseBody.subSequence(i, i + 9).toString();
            long longVar = Integer.parseInt(charSequence);
            long returnValue = test(longVar);
            if(returnValue != 0) {
                System.out.println(returnValue);
                break;
            }

        }
    }

    private static long test(long value) {
        int count = 0;
        char[] numString = String.valueOf(value).toCharArray();
        int length = numString.length;
        int arrayLength = length - 1;

        for (int i = 0; i < length; i++) {
            if(numString[i] == numString[arrayLength - i]) {
                count++;
            }
        }

        if(count == 9) {
            if(!isPrime(value)) {
                return 0;
            }
            return value;
        } else {
            return 0;
        }
    }

    private static boolean isPrime(long num) {
        if(num<=1)
        {
            return false;
        }
        for(int i=2;i<num/2;i++)
        {
            if((num%i)==0)
                return  false;
        }
        return true;
    }
}