package PP.DZ;

public class DZ1 {
    public static double f(double x) {
        double val = x;
        for (int i = 0; i < 30; i++) {
            val = Math.sin(val) * Math.cos(val) + Math.sqrt(Math.abs(val));
        }
        return val;
    }

    public static double integrateSequential(double a, double b, int n) {
        double h = (b - a) / n;
        double sum = 0.0;
        for (int i = 0; i < n; i++) {
            double x = a + (i + 0.5) * h;
            sum += f(x);
        }
        return sum * h;
    }

    static class Worker extends Thread {
        double a, h;
        int startIdx, endIdx;
        double localSum = 0.0;

        public Worker(double a, double h, int startIdx, int endIdx) {
            this.a = a;
            this.h = h;
            this.startIdx = startIdx;
            this.endIdx = endIdx;
        }

        @Override
        public void run() {
            for (int i = startIdx; i < endIdx; i++) {
                double x = a + (i + 0.5) * h;
                localSum += f(x);
            }
        }
    }

    public static double integrateParallel(double a, double b, int n, int numThreads) throws InterruptedException {
        double h = (b - a) / n;
        Worker[] workers = new Worker[numThreads];
        int chunk = n / numThreads;

        for (int t = 0; t < numThreads; t++) {
            int start = t * chunk;
            int end = (t == numThreads - 1) ? n : start + chunk;
            workers[t] = new Worker(a, h, start, end);
            workers[t].start();
        }

        double totalSum = 0.0;
        for (int t = 0; t < numThreads; t++) {
            workers[t].join();
            totalSum += workers[t].localSum;
        }

        return totalSum * h;
    }

    public static void main(String[] args) throws InterruptedException {
        double a = 0.0;
        double b = Math.PI;
        int n = 10_000_000;
        int numThreads = 4;

        long start1 = System.currentTimeMillis();
        double res1 = integrateSequential(a, b, n);
        long time1 = System.currentTimeMillis() - start1;

        long start2 = System.currentTimeMillis();
        double res2 = integrateParallel(a, b, n, numThreads);
        long time2 = System.currentTimeMillis() - start2;

        System.out.println("Posledovatelno: " + time1 + "ms / res = " + res1);
        System.out.println("Parallelno: " + time2 + "ms / res = " + res2);
        System.out.println("Raznitsa: " + (time1 - time2) + "ms");
    }
}