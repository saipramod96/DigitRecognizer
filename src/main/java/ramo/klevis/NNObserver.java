package ramo.klevis;

/**
 * Observer interface for Neural Networks observing UI for input.
 * @author Anchit Bhattacharya
 * @version 1.0
 * @since 10/12/2020
 */
public interface NNObserver {
	int update(LabeledImage labeledImage);

}
