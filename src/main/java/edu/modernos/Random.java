package edu.modernos;

import java.util.concurrent.ThreadLocalRandom;

import static java.lang.Math.*;

public class Random {
    static public double Generate () {
        while(true) {
            double U = ThreadLocalRandom.current().nextDouble(0, 1);
            double V = ThreadLocalRandom.current().nextDouble(0, 1);
            double X = sqrt((8/E)) * (V - 0.5) / U;

            double square = X * X;
            if (square <= 5 - 4 * exp(0.25) * U &&
                square < 4 * exp(-1.35) / U + 1.4 &&
                square < -4 * log(U))
                return X;
        }
    }
}

