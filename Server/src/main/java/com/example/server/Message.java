package com.example.server;

public class Message {
    private String code;
    private int[] numbers;
    private String[] words;

    Message(String code, int[] numbers, String[] words)
    {
        this.code = code;
        this.numbers = numbers;
        this.words = words;
    }

    public String getCode()
    {
        return code;
    }

    public int[] getNumbers()
    {
        return numbers;
    }

    public String[] getWords()
    {
        return words;
    }

}
