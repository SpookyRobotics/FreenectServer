package com.spookybox.inputManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;

public class ConsoleInput {
    private Optional<Runnable> onButtonA = Optional.empty();
    private Optional<Runnable> onButtonB = Optional.empty();
    private BufferedReader mInput;

    public void setOnButtonA(Runnable r){
        onButtonA = Optional.ofNullable(r);
    }

    public void setOnButtonB(Runnable r){
        onButtonB = Optional.ofNullable(r);
    }

    public void startInputLoop(){
        mInput = new BufferedReader(new InputStreamReader(System.in));
        InputToken token = readInputToken();
        while(token != InputToken.EXIT){
            switch (token){
                case BUTTON_A:
                    onButtonA.ifPresent(Runnable::run);
                    break;
                case BUTTON_B:
                    onButtonB.ifPresent(Runnable::run);
                    break;
                default:
                    break;
            }
            token = readInputToken();
        }
    }

    private InputToken readInputToken(){
        try {
            displayPrompt();
            return InputToken.toValue(mInput.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return InputToken.UNKNOWN;
    }

    private void displayPrompt(){
        System.out.print(">> ");
    }
}
