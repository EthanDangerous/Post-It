package com.brothers_trouble.postit.util;

public final class Catenary {
    private final double paramA;
    private final double vertexX;
    private final double translationY;

    // this is give or take what it says in the Catenary wikipedia page
    // https://en.wikipedia.org/wiki/Catenary

    private Catenary(double paramA, double vertexX, double translationY) {
        this.paramA = paramA;
        this.vertexX = vertexX;
        this.translationY = translationY;
    }

    public static Catenary of(double horizDist, double vertDist, double length) {
        double rootLengthSqMinusVertSq = Math.sqrt(length * length - vertDist * vertDist);

        double lowerBound = 0.05;
        double upperBound = 1000;
        for (int iteration = 0; iteration < 40; iteration++) {
            double midpoint = (lowerBound + upperBound) / 2;
            double candidate = 2 * midpoint * Math.sinh(horizDist / (2 * midpoint));
            if (candidate > rootLengthSqMinusVertSq) {
                lowerBound = midpoint;
            } else {
                upperBound = midpoint;
            }
        }

        double paramA = (lowerBound + upperBound) / 2;
        double vertexX = horizDist / 2
                - paramA * arsinh(vertDist / (2 * paramA * Math.sinh(horizDist / (2 * paramA))));
        double translationY = -paramA * Math.cosh(vertexX / paramA);

        return new Catenary(paramA, vertexX, translationY);
    }

    public double yAt(double x) {
        return paramA * Math.cosh((x - vertexX) / paramA) + translationY;
    }

    private static double arsinh(double value) {
        return Math.log(value + Math.sqrt(value * value + 1));
    }
}