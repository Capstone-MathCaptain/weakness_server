package MathCaptain.weakness.domain.Record.entity.UserLog;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor
public class ExerciseInfo {

    private String exerciseName;

    private int weight;

    private int reps;

    private int sets;

    private ExerciseInfo(String exerciseName, int weight, int reps, int sets) {
        this.exerciseName = exerciseName;
        this.weight = weight;
        this.reps = reps;
        this.sets = sets;
    }

    public static ExerciseInfo of(String exerciseName, int weight, int reps, int sets) {
        return new ExerciseInfo(exerciseName, weight, reps, sets);
    }
}
