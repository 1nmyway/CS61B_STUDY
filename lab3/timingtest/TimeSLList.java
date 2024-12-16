package timingtest;
import edu.princeton.cs.algs4.Stopwatch;

import java.util.List;

/**
 * Created by hug.
 */
public class TimeSLList {
    private static void printTimingTable(AList<Integer> Ns, AList<Double> times, AList<Integer> opCounts) {
        System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
        System.out.printf("------------------------------------------------------------\n");
        for (int i = 0; i < Ns.size(); i += 1) {
            int N = Ns.get(i);
            double time = times.get(i);
            int opCount = opCounts.get(i);
            double timePerOp = time / opCount * 1e6;
            System.out.printf("%12d %12.2f %12d %12.2f\n", N, time, opCount, timePerOp);
        }
    }

    public static void main(String[] args) {
        timeGetLast();
    }

    public static void timeGetLast() {
        AList<Integer> Ns = new AList<>();
        AList<Double> times = new AList<>();
        AList<Integer> Ms = new AList<>();
        for (int i = 1000; i <= 128000; i *= 2) {
            Ns.addLast(i);
            Ms.addLast(10000);
            SLList<Integer> test = new SLList<>();

            for (int j = 0; j < i; j++) {
                test.addLast(j);
            }
            Stopwatch sw = new Stopwatch();
            for(int j = 0; j < 10000; j++){
                test.getLast();
            }
            times.addLast(sw.elapsedTime());
        }
        printTimingTable(Ns, times, Ms);
    }

}
