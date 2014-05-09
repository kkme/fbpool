// SCCS ID: @(#) 08/23/98 1.1 SeasonScore.java

package nflpool;

/**
 * <p>
 * @author Steve Schmadeke
 * @version 1.1
 */
public class SeasonScore {

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void addScore(int score) {
        total += score;
        count++;
        if (score < lowScore) {
            secondLowScore = lowScore;
            lowScore = score;
        } else if (score < secondLowScore) {
            secondLowScore = score;
        }
    }

    public int getScoreCount() {
        return count;
    }

    public int getTotalScore() {
        return total;
    }

    public int getLowScore() {
        if (count >= 1) {
            return lowScore;
        } else {
            return 0;
        }
    }

    public int getSecondLowScore() {
        if (count >= 2) {
            return secondLowScore;
        } else {
            return 0;
        }
    }

    public int getAdjustedTotalScore() {
        return total - getSecondLowScore() - getLowScore();
    }

    public String toString() {
        StringBuffer str = new StringBuffer();
 
        str.append(getClass().getName());
        str.append("[");
        str.append("name="+name);
        str.append(",total="+total);
        str.append(",count="+count);
        str.append(",lowScore="+lowScore);
        str.append(",secondLowScore="+secondLowScore);
        str.append("]");
 
        return str.toString();
    }
    private String name = null;
    private int total = 0;
    private int count = 0;
    private int lowScore = Integer.MAX_VALUE;
    private int secondLowScore = Integer.MAX_VALUE;
}
