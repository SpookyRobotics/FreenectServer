package com.spookybox.inputManager;

enum InputToken {
    BUTTON_A("a"),
    BUTTON_B("b"),
    EXIT("exit"),
    UNKNOWN(null);

    private final String charSequence;

    InputToken(String sequence) {
        charSequence = sequence;
    }

    public static InputToken toValue(String line) {
        if (line == null ) {
            return UNKNOWN;
        }
        if (BUTTON_A.charSequence.equals(line.toLowerCase())) {
            return BUTTON_A;
        }
        if (BUTTON_B.charSequence.equals(line.toLowerCase())) {
            return BUTTON_B;
        }
        if (EXIT.charSequence.equals(line.toLowerCase())) {
            return EXIT;
        }
        return UNKNOWN;
    }
}
