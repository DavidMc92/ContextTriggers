package x.contextualtriggers.Misc;

/**
 * Created by Sean on 16/04/2016.
 */
public class CommuteDetection{
    private float currentDistanceToHome, currentDistanceToWork,
            previousDistanceToHome, previousDistanceToWork;
    private CommuteStatus commuteStatus;

    public CommuteDetection(CommuteStatus status){
        this.commuteStatus = status;
    }

    public float getCurrentDistanceToHome() {
        return currentDistanceToHome;
    }

    public float getCurrentDistanceToWork() {
        return currentDistanceToWork;
    }

    public float getPreviousDistanceToHome() {
        return previousDistanceToHome;
    }

    public float getPreviousDistanceToWork() {
        return previousDistanceToWork;
    }

    public CommuteStatus getCommuteStatus() {
        return commuteStatus;
    }

    public CommuteDetection setCurrentDistanceToHome(float currentDistanceToHome) {
        this.currentDistanceToHome = currentDistanceToHome;
        return this;
    }

    public CommuteDetection setCurrentDistanceToWork(float currentDistanceToWork) {
        this.currentDistanceToWork = currentDistanceToWork;
        return this;
    }

    public CommuteDetection setPreviousDistanceToHome(float previousDistanceToHome) {
        this.previousDistanceToHome = previousDistanceToHome;
        return this;
    }

    public CommuteDetection setPreviousDistanceToWork(float previousDistanceToWork) {
        this.previousDistanceToWork = previousDistanceToWork;
        return this;
    }

    public CommuteDetection setCommuteStatus(CommuteStatus commuteStatus) {
        this.commuteStatus = commuteStatus;
        return this;
    }
}