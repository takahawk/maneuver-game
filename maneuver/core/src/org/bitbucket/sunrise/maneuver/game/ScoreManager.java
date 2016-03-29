package org.bitbucket.sunrise.maneuver.game;

import com.badlogic.gdx.Preferences;

import java.util.Arrays;
import java.util.Iterator;

/**
 * Created by takahawk on 29.03.16.
 */
public final class ScoreManager {
    private static final int SCORES_COUNT = 3;
    private Preferences appPrefs;
    private ScoreResult[] scores = new ScoreResult[SCORES_COUNT];

    public class ScoreResult {
        public final int score;
        public final String name;

        public ScoreResult(String name, int score) {
            this.name = name;
            this.score = score;
        }
    }

    public ScoreManager(Preferences appPrefs) {
        this.appPrefs = appPrefs;
        for (int i = 0; i < scores.length; i++) {
            if (appPrefs.contains("scoreName" + (i + 1)))
                scores[i] = new ScoreResult(
                        appPrefs.getString("scoreName" + (i + 1)),
                        appPrefs.getInteger("scoreResult" + (i + 1))
                );
            else
                scores[i] = null;
        }
    }

    public void addScore(String name, int score) {
        for (int i = 0; i < scores.length; i++) {
            if (score > scores[i].score) {
                for (int j = i + 1; j < scores.length; j++) {
                    scores[j] = scores[j - 1];
                }
                scores[i] = new ScoreResult(name, score);
                invalidateResults();
                return;
            }
        }
    }

    public Iterable<ScoreResult> getScores() {
        return new Iterable<ScoreResult>() {
            @Override
            public Iterator<ScoreResult> iterator() {
                return new Iterator<ScoreResult>() {
                    int index = -1;
                    @Override
                    public boolean hasNext() {
                        return (++index) < scores.length;
                    }

                    @Override
                    public ScoreResult next() {
                        return scores[index];
                    }
                };
            }
        };
    }

    private void invalidateResults() {
        for (int i = 0; i < scores.length; i++) {
            appPrefs.putString("scoreName" + (i + 1), scores[i].name);
            appPrefs.putInteger("scoreResult" + (i + 1), scores[i].score);
        }
        appPrefs.flush();
    }
}
