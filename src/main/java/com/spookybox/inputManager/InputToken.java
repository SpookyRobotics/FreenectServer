package com.spookybox.inputManager;

enum InputToken {
    BUTTON_A,
    BUTTON_B,
    EXIT,
    UNKNOWN;

    public static InputToken toValue(String line) {
        if (line == null || line.length() != 1) {
            return UNKNOWN;
        }
        if ("a".equals(line)) {
            return BUTTON_A;
        }
        if ("b".equals(line)) {
            return BUTTON_B;
        }
        if ("e".equals(line)) {
            return EXIT;
        }
        return UNKNOWN;
    }
}
