import com.ncea.anticheat.core.*;
import java.util.UUID;

/** Synthetic core-only throughput, not a Paper/PvP benchmark. */
public final class CoreBenchmark {
    public static void main(String[] args) {
        CheckManager checks = BasicChecks.create();
        PlayerData data = new PlayerData(UUID.randomUUID());
        long checksum = 0;
        for (int pass = 0; pass < 2; pass++) {
            int operations = pass == 0 ? 100_000 : 1_000_000;
            long start = System.nanoTime();
            for (int i = 0; i < operations; i++) {
                long now = 1_000L + i * 50L;
                data.lastMove = now - 50;
                Observation observation = new Observation(Observation.Kind.MOVE, now,
                        i % 100 == 0 ? 1.0 : 0.2, 0, true, 0, 0, true);
                checksum += checks.inspect(data, observation).size();
            }
            long elapsed = System.nanoTime() - start;
            System.out.printf("pass=%s operations=%d elapsed-ms=%.3f mean-ns=%.1f checksum=%d%n",
                    pass == 0 ? "warmup" : "measure", operations, elapsed / 1e6, (double) elapsed / operations, checksum);
        }
    }
}
