package ramo.klevis;

/**
 * Subject interface for UI components being observed by Neural Networks.
 * @author Anchit Bhattacharya
 * @version 1.0
 * @since 10/12/2020
 */
public interface UISubject {

    void attach(NNObserver observer);

    void detach(NNObserver observer);

    int notifyObs(LabeledImage labeledImage);

}
