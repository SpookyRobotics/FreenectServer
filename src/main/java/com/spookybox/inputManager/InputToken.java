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
        if (BUTTON_A.charSequence.equals(line)) {
            return BUTTON_A;
        }
        if (BUTTON_B.charSequence.equals(line)) {
            return BUTTON_B;
        }
        if (EXIT.charSequence.equals(line)) {
            return EXIT;
        }
        return UNKNOWN;
    }
}
